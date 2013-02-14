package br.lyfi.ui;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
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
		final Options options = new Options();

		options.addOption(new Option("f", "find", true,
				"partial lyrics expression to use on query (required)"));
		options.addOption(new Option(
				"d",
				"data-dir",
				true,
				"directory which contains music files where"
						+ " the search will be performed (optional, defaults to "
						+ System.getProperty("user.dir") + ")"));
		options.addOption(new Option("i", "index-dir", true, "directory where "
				+ LyricsFinder.SIMPLE_NAME
				+ " will store index files (optional, defalts to "
				+ System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator")
				+ LyricsFinder.SIMPLE_NAME + "_index" + ")"));
		// WISHLIST implement playback capabilities
		final CommandLineParser parser = new GnuParser();
		try {
			final CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("f")) {
				final LyricsFinder lyfi;
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
				final String[] result = lyfi.find(cmd.getOptionValue("f"));
				System.out.println(result[1]);
				try {
					lyfi.close();
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
					
			} else {
				printUsage(options);
			}
		} catch (ParseException e) {
			printUsage(options);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Print default usage message
	 * 
	 * @param options
	 */
	private static void printUsage(final Options options) {
		System.out.println(System.getProperty("line.separator")
				+ System.getProperty("line.separator") + "\t\t"
				+ LyricsFinder.SIMPLE_NAME + " has been brought to life by"
				+ System.getProperty("line.separator") + "\t\t"
				+ "Miguel Mendes Ruiz (migmruiz@gmail.com),"
				+ System.getProperty("line.separator") + "\t\t"
				+ "please enjoy :)");
		final HelpFormatter formatter = new HelpFormatter();
		formatter
				.printHelp(
						LyricsFinder.SIMPLE_NAME,
						"Here are the options for you to find your music by partial lyrics \n"
								+ "the first run will take some time, but then it will work\n"
								+ " like a charm", options,
						System.getProperty("line.separator")
								+ "usage example: lyfi"
								+ " -d ../resources/test/datadir/"
								+ " -i ../resources/test/indexdir/"
								+ " -f \"better\"");
	}

}
