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
		
		options.addOption(new Option("f", "find", true,
				"partial lyrics expression to use on query"));
		options.addOption(new Option("d", "data-dir", true,
				"directory which contains music files where"
						+ " the search will be performed"));
		options.addOption(new Option("i", "index-dir", true,
				"directory where lyfi will store index files"));
		options.addOption(new Option("p", "play", false,
				"plays the music file" + System.getProperty("line.separator") + 
				"Requires sox, but not yet implemented."));
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printUsage(options);
			throw new RuntimeException(e);
		}
		
		if (cmd.hasOption("f")) {
			LyricsFinder lyfi;
			if (cmd.hasOption("d")) {
				if (cmd.hasOption("i")) {
					lyfi = new LyricsFinder(cmd.getOptionValue("i"),
							cmd.getOptionValue("d"));
				} else {
					lyfi = new LyricsFinder(cmd.getOptionValue("d"));
				}
			} else {
				lyfi = new LyricsFinder();
			}
			String[] result = lyfi.find(cmd.getOptionValue("f"));
			System.out.println(result[1]);
		} else {
			printUsage(options);
		}
	}

	/**
	 * Print default usage message
	 * 
	 * @param options
	 */
	private static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("lyfi", "find your music by partial lyrics",
				options, "lyfi has been brought to life by Miguel Mendes Ruiz");
	}

}
