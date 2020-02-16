/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.parser;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import java.util.TimeZone;

/**
 * Parse a TimeZone from a command line argument.
 * 
 * @author Tom Parker
 */
public class TimeZoneParser extends StringParser {

  @Override
  public TimeZone parse(String arg) throws ParseException {

    TimeZone timeZone = TimeZone.getTimeZone(arg);

    if ("GMT".equals(timeZone.getID()) && !"GMT".equals(arg)) {
      throw new ParseException("Cannot parse time zone " + arg);
    }

    return timeZone;
  }
}
