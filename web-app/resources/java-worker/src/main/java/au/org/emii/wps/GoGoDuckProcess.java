package au.org.emii.wps;

import java.io.File;

import au.org.emii.gogoduck.worker.GoGoDuckException;
import au.org.emii.gogoduck.worker.GoGoDuck;

import net.opengis.wps10.ExecuteType;
import org.geoserver.ows.Dispatcher;
import org.geoserver.platform.Operation;
import org.opengis.util.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geoserver.wps.process.FileRawData;
import org.geoserver.wps.resource.WPSResourceManager;

import javax.servlet.ServletContext;

@DescribeProcess(title="GoGoDuck", description="Subset and download gridded collection as NetCDF files")
public class GoGoDuckProcess implements GeoServerProcess {
    private static final Logger logger = LoggerFactory.getLogger(GoGoDuck.class);

    private final WPSResourceManager resourceManager;
    // TODO all values here are hardcoded
    private final int fileLimit = 2232;
    private final int threadCount = 2;

    public GoGoDuckProcess(WPSResourceManager resourceManager, ServletContext context) {
        this.resourceManager = resourceManager;
    }

    @DescribeResult(name="result", description="NetCDF file", meta={"mimeTypes=application/x-netcdf"})
    public FileRawData execute(
            @DescribeParameter(name="layer", description="WFS layer to query")
            String layer,
            @DescribeParameter(name="subset", description="Subset, semi-colon separated")
            String subset,
            ProgressListener progressListener
    ) throws ProcessException {
        try {
            final File outputFile = resourceManager.getOutputResource(
                    resourceManager.getExecutionId(true), layer + ".nc").file();

            final String filePath = outputFile.toPath().toAbsolutePath().toString();

            GoGoDuck ggd = new GoGoDuck(getBaseUrl(), layer, subset, filePath, fileLimit);

            ggd.setTmpDir(getWorkingDir(resourceManager));
            ggd.setThreadCount(threadCount);
            ggd.setProgressListener(progressListener);

            ggd.run();
            return new FileRawData(outputFile, "application/x-netcdf", "nc");
        } catch (GoGoDuckException e) {
            logger.error(e.toString());
            throw new ProcessException(e);
        }
    }

    private String getBaseUrl() {
        // TODO is there a nicer way of getting BaseUrl?
        Dispatcher.REQUEST.get().getOperation();
        Operation op = Dispatcher.REQUEST.get().getOperation();
        ExecuteType execute = (ExecuteType) op.getParameters()[0];
        return execute.getBaseUrl();
    }

    private String getWorkingDir(WPSResourceManager resourceManager) {
        try {
            // Use WPSResourceManager to create a temporary directory that will get cleaned up
            // when the process has finished executing (Hack! Should be a method on the resource manager)
            return resourceManager.getTemporaryResource("").dir().getAbsolutePath();
        } catch (Exception e) {
            logger.info("Exception accessing working directory: \n" + e);
            return System.getProperty("java.io.tmpdir");
        }
    }
}

