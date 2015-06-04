package au.org.emii.gogoduck.worker;

import org.junit.Test;
import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GoGoDuckModuleTest {
    @Test
    public void testGetSubsetParameters() throws Exception {
        GoGoDuckModule ggdm = new GoGoDuckModule();
        ggdm.init("", "", "TIME,1,2;LONGITUDE,2,3", null);

        // Make sure it removes `TIME` subset parameter
        assertFalse(ggdm.getSubsetParameters().containsKey("TIME"));

        // But did that only on a copy of the subset parameters
        Field privateSubset = GoGoDuckModule.class.getDeclaredField("subset");
        privateSubset.setAccessible(true);
        SubsetParameters sp = (SubsetParameters) privateSubset.get(ggdm);
        assertTrue(sp.containsKey("TIME"));
    }

    @Test
    public void testUpdateMetadata() {
    }
}
