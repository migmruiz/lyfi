package br.lyfi.preindexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import lyrics.crawler.Crawler;
import lyrics.crawler.LyricsNotFoundException;
import lyrics.crawler.LyricsWikiaCrawler;
import lyrics.crawler.MetroLyricsCrawler;
import lyrics.crawler.SongLyricsCrawler;
import lyrics.crawler.webClient.DownloadException;

/**
 * Class representing an Lyric search over the Web
 * 
 * @author migmruiz
 * 
 */
public class LyricsWebSearcher {
	private List<Crawler> crawlers;

	/**
	 * Constructor for a new lyrics web searcher
	 */
	public LyricsWebSearcher() {
		crawlers = Collections.synchronizedList(new ArrayList<Crawler>());
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
	public String fetchLyrics(final String artist, final String title) {
		String lyrics = "";

		for (final Crawler crawler : crawlers) {
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
			} catch (LyricsNotFoundException | DownloadException ex) {
			}
		}
		return lyrics;
	}
}
