package br.lyfi.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

import br.lyfi.LyricsFinder;

/**
 * Command-line user interface for LyricsFinder
 * 
 * @author migmruiz
 * 
 */
public class LyricsFinderCommandLineUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options();
		boolean hasArg = true;
		options.addOption(new Option("opt", "longOpt", hasArg, "description"));
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printUsage(options);

			// TODO Log exception
			e.printStackTrace();
		}

		if (cmd.hasOption("opt_indexDir") && cmd.hasOption("opt_dataDir")) {
			LyricsFinder lyfi = new LyricsFinder(
					cmd.getOptionValue("opt_indexDir"),
					cmd.getOptionValue("opt_dataDir"));

			if (cmd.hasOption("opt_lyricsExp")) {
				lyfi.find(cmd.getOptionValue("opt_lyricsExp"));
			}
		}

	}

	/**
	 * Print default usage mensage
	 * 
	 * @param options
	 */
	private static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("lyfi", options);
	}

}
