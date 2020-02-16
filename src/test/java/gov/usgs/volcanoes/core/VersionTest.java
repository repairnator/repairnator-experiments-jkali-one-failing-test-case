package gov.usgs.volcanoes.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import gov.usgs.volcanoes.core.Version;

import org.junit.Test;

/**
 * 
 * @author Tom Parker
 *
 */
public class VersionTest {

  /**
   * 
   */
  @Test
  public void testBuildTime() {
    assertFalse("".equals(Version.BUILD_TIME));
  }

  /**
   * 
   */
  @Test
  public void testPomVersion() {
    assertFalse("".equals(Version.POM_VERSION));
  }

  /**
   * 
   */
  @Test
  public void testVersionString() {
    assertFalse("".equals(Version.VERSION_STRING));
  }
}
