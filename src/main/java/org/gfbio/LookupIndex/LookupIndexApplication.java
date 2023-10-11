package org.gfbio.LookupIndex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
@SpringBootApplication
public class LookupIndexApplication {

	public static void main(String[] args) {

		SpringApplication.run(LookupIndexApplication.class, args);

		String mode = null, acronym = null, pathToConfig = null;
		int numParams = 0;

		Options options = new Options();
		HelpFormatter formatter = new HelpFormatter();

		try {

			// create commandline options
			options.addOption("m", true, "generator mode ('create', 'update')");
			options.addOption("a", true, "terminology acronym, e.g., ENVO");
			options.addOption("c", true,
					"path to configuration file (default: /var/opt/ts/lookup_index_generator.properties)");

			// create the parser
			CommandLineParser parser = new DefaultParser();

			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// read supplied parameters
			if (line.hasOption("m")) {
				mode = line.getOptionValue("m");
				numParams++;
			}

			if (line.hasOption("a")) {
				acronym = line.getOptionValue("a");
				numParams++;
			}

			if (line.hasOption("c")) {
				pathToConfig = line.getOptionValue("c");
				numParams++;
			}

			if (numParams < 3) {
				formatter.printHelp("<this>.jar", options, true);
				System.exit(-1);
			}

			// load application.properties
			AppProperties appProperties = AppProperties.getInstance();
			appProperties.initProperties(pathToConfig);

			// start application
			Facade facade = new Facade(acronym);
			facade.setMode(mode);
			facade.run();


		} catch (ParseException e) {

			formatter.printHelp("<this>.jar", options, true);

			System.exit(-1);
		}

	}

}
