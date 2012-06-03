package br.lyfi.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

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
		options.addOption(new Option("opt", "description"));
		CommandLineParser parser = new GnuParser();
		try {
			@SuppressWarnings("unused")
			CommandLine cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printUsage(options);

			// TODO Log exception
			e.printStackTrace();
		}

	}
	
	/**
	 * Print default usage mensage
	 * 
	 * @param options
	 */
	private static void printUsage(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "lyfi", options );
	}

}
