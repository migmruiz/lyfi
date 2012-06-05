package br.lyfi.preindexing;

import java.util.Vector;
import java.util.regex.Pattern;

import lyrics.crawler.Crawler;
import lyrics.crawler.LyricsNotFoundException;
import lyrics.crawler.LyricsWikiaCrawler;
import lyrics.crawler.MetroLyricsCrawler;
import lyrics.crawler.SongLyricsCrawler;

/**
 * Class representing an Lyric search over the Web
 * 
 * @author migmruiz
 * 
 */
public class LyricsWebSearcher {
	private Vector<Crawler> crawlers;

	/**
	 * Constructor for a new lyrics web searcher
	 */
	public LyricsWebSearcher() {
		crawlers = new Vector<Crawler>();
		crawlers.add(new LyricsWikiaCrawler());
		crawlers.add(new MetroLyricsCrawler());
		crawlers.add(new SongLyricsCrawler());
	}

	/**
	 * Tries to fetch the lyrics for the song specified searching for it in the
	 * implemented Crawlers' web sites
	 * 
	 * @param artist
	 *            Song's artist
	 * @param title
	 *            Song's title
	 * @return Song's lyrics
	 */
	public String fetchLyrics(String artist, String title) {
		String lyrics = "";

		for (Crawler crawler : crawlers) {
			try {
				lyrics = crawler.getLyrics(artist, title);

				if (Pattern.matches("\\s*", lyrics)) {
					System.out.println(lyrics + " in " + artist + " - " + title
							+ " are not meaningful.");
					// If the lyrics are not meaningful I run
					// another crawler
				} else {
					break;
				}
			} catch (LyricsNotFoundException ex) {
			}
		}
		return lyrics;
	}
}
