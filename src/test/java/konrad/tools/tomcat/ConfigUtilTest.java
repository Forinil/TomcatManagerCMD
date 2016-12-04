package konrad.tools.tomcat;

import konrad.tools.tomcat.utils.ConfigUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Stworzone przez Konrad Botor dnia 2016-07-14.
 */
public class ConfigUtilTest {

    @Test
    public void testAllProperties() {
        ConfigUtil configUtil = ConfigUtil.getInstance();
        System.out.println(configUtil);
        System.out.println(configUtil.getProperty("tomcat.user"));
        assertEquals("Error reading tomcat.user", configUtil.getProperty("tomcat.user"), "Konrad-script");
    }
}
