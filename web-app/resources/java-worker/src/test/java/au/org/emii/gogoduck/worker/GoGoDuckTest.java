package au.org.emii.gogoduck.worker;

import org.junit.Test;
import java.net.URI;
import java.net.URL;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GoGoDuckTest {
    @Test
    public void testFileURItoURL() throws Exception {
        Method fileURItoURLMethod = GoGoDuck.class.getDeclaredMethod("fileURItoURL", URI.class);
        fileURItoURLMethod.setAccessible(true);
        URL u = (URL) fileURItoURLMethod.invoke(GoGoDuck.class, new URI("/mnt/imos-t3/file.nc"));
        assertEquals(u, new URL("http://data.aodn.org.au/file.nc"));
    }

    @Test
    public void testNextProfile() throws Exception {
        Method nextProfileMethod = GoGoDuck.class.getDeclaredMethod("nextProfile", String.class);
        nextProfileMethod.setAccessible(true);

        String profile = "this_is_a_profile";

        profile = (String) nextProfileMethod.invoke(GoGoDuck.class, profile);
        assertEquals(profile, "this_is_a");

        profile = (String) nextProfileMethod.invoke(GoGoDuck.class, profile);
        assertEquals(profile, "this_is");

        profile = (String) nextProfileMethod.invoke(GoGoDuck.class, profile);
        assertEquals(profile, "this");
    }
}
