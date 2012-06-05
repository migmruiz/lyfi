package br.lyfi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Sample application test
 * 
 * @author migmruiz
 *
 */
public class LyricsFinderTest {
	private String indexDirectory;
	private String dataDirectory;
	private LyricsFinder lyfi;
	private String[] lyricsExp;
	private int numberOfLyricsExp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		indexDirectory = "resources/indexdir";
		dataDirectory = "resources/datadir";
		lyfi = new LyricsFinder(indexDirectory, dataDirectory);
		numberOfLyricsExp = 1;
		lyricsExp = new String[1];
		lyricsExp[0] = "one";
	}

	/**
	 * Test method for {@link br.lyfi.LyricsFinder#find(java.lang.String)}.
	 */
	@Test
	public void testFind() {
		assertNotNull(lyfi);
		for (int i = 0; i < numberOfLyricsExp; i++) {
			assertNotNull(lyfi.find(lyricsExp[i]));
		}
	}
}
