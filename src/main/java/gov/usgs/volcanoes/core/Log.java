/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core;

import java.io.IOException;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * A utility class to handle implementation-specific logging configuration. Most classes will not
 * need to configure the logger. Most classes that do need to configure the logger will do so
 * through a properties file. I'm here for those few remaining classes.
 * 
 * @author Tom Parker
 *
 */
public class Log {
  private static String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} %5p - %m%n";

  /**
   * Add a rolling file appender to the root logger with the default pattern.
   * 
   * @param name name of file
   * @throws IOException when file cannot be created or modified
   */
  public static void addFileAppender(String name) throws IOException {
    addFileAppender(name, LOG_PATTERN);
  }

  /**
   * Add a rolling file appender to the root logger.
   *
   * @param name name of the file
   * @param pattern log pattern to use
   * @throws IOException when file cannot be created
   */
  public static void addFileAppender(String name, String pattern) throws IOException {
    PatternLayout layout = new PatternLayout(pattern);
    RollingFileAppender fileAppender = new RollingFileAppender(layout, name);
    fileAppender.setMaxFileSize("100MB");
    fileAppender.setMaxBackupIndex(5);
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.addAppender(fileAppender);
    Logger.getRootLogger().addAppender(asyncAppender);
  }

  /**
   * Set root logger level.
   * 
   * @param level minimum logging level
   */
  public static void setLevel(Level level) {
    Logger.getRootLogger().setLevel(level);
  }
}
