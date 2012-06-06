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
	private String testIndexDirPath;
	private String testDataDirPath;
	private LyricsFinder lyfi;
	private String[] lyricsExp;
	private int numberOfLyricsExp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testIndexDirPath = "resources/indexdir";
		testDataDirPath = "resources/datadir";
		lyfi = new LyricsFinder(testIndexDirPath, testDataDirPath);
		numberOfLyricsExp = 1;
		lyricsExp = new String[numberOfLyricsExp];
		lyricsExp[0] = "one";
	}

	/**
	 * Test method for {@link br.lyfi.LyricsFinder#find(java.lang.String)}.
	 */
	@Test
	public void testFind() {
		assertNotNull(lyfi);
		for (int i = 0; i < numberOfLyricsExp; i++) {
			System.out.println("Performing index search #" + (i + 1));
			assertNotNull(lyfi.find(lyricsExp[i]));
		}
	}
}
