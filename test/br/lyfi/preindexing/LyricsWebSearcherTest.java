package br.lyfi.preindexing;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

/**
 * Automated tests for the {@link br.lyfi.preindexing.LyricsWebSearcher} class
 * 
 * @author migmruiz
 * 
 */
public class LyricsWebSearcherTest {

	private LyricsWebSearcher webSearcher;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		webSearcher = new LyricsWebSearcher();
	}

	/**
	 * Test method for
	 * {@link br.lyfi.preindexing.LyricsWebSearcher#fetchLyrics(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testFetchLyrics() {
		String lyrics = webSearcher.fetchLyrics("Beatles", "Getting Better");
		assertNotNull(lyrics);
		assertFalse(Pattern.matches("\\s*", lyrics));
		System.out.println(lyrics);
	}

}
