
package org.pdxfinder;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.pdxfinder.services.ds.SearchDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableNeo4jRepositories
@EnableTransactionManagement
@ComponentScan(value = "org.pdxfinder", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SearchDS.class)
}) // SearchDS has to be excluded here as it may refer to nodes that are being created during the load process
public class Loader {

    private final static Logger log = LoggerFactory.getLogger(Loader.class);

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            Options options = new Options();
            HelpFormatter formatter = new HelpFormatter();

            Option createOpt = Option.builder("create").desc("Purgess all data and re-creates the base graph database with the basic nodes pre loaded (standard tissues, implantation details, bacckground strains, etc.").build();
            Option loadOpt = Option.builder("load").desc("load command ").build();
            Option jaxOpt = Option.builder("loadJAX").desc("Load JAX PDX models from file.").build();

            options.addOption(createOpt);
            options.addOption(loadOpt);
            options.addOption(jaxOpt);

            formatter.printHelp("Application", options);
        } else {
            SpringApplication.run(Loader.class, args);
        }
    }

}
