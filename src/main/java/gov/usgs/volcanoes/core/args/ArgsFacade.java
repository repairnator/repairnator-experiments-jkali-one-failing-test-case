/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;

import gov.usgs.volcanoes.core.args.decorator.ConfigFileArg;
import gov.usgs.volcanoes.core.args.decorator.CreateConfigArg;
import gov.usgs.volcanoes.core.args.decorator.VerboseArg;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class demonstrating use of the args package.
 *
 * @author Tom Parker
 */
public class ArgsFacade {
  private static final String DEFAULT_CONFIG_FILENAME = "facadeConfig.config";

  private static final String EXAMPLE_CONFIG_FILENAME = "facadeConfig.config";
  private static final String EXPLANATION = "I am demonstrate how to use the args package\n";
  private static final Logger LOGGER = LoggerFactory.getLogger(ArgsFacade.class);

  private static final Parameter[] PARAMETERS =
      new Parameter[] {new Switch("knockKnock", 'k', "knockKnock", "Knock Knock.")};

  private static final String PROGRAM_NAME = "java -jar gov.usgs.volcanoes.args.ArgsFacade";

  /**
   *
   * @param args Arguments provided at program launch
   * @throws Exception if anything goes wrong. Yes, pretty much anything.
   */
  public static void main(String... args) throws Exception {

    // keep log4j happy
    BasicConfigurator.configure();

    // base arguments
    Arguments arguments = new Args(PROGRAM_NAME, EXPLANATION, PARAMETERS);

    // add any decorators that are needed

    // config file decorator
    arguments = new ConfigFileArg(DEFAULT_CONFIG_FILENAME, arguments);
    arguments = new CreateConfigArg(EXAMPLE_CONFIG_FILENAME, arguments);
    arguments = new VerboseArg(arguments);

    JSAPResult jsapResult = null;
    jsapResult = arguments.parse(args);

    final boolean verbose = jsapResult.getBoolean("verbose");
    LOGGER.debug("Setting: verbose={}", verbose);

    final String configFileName = jsapResult.getString("config-filename");
    LOGGER.debug("Setting: config-filename={}", configFileName);

    if (jsapResult.getBoolean("knockKnock")) {
      System.out.println("Who's there?");
    }
  }
}
