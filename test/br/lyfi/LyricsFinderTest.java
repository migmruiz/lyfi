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
		numberOfLyricsExp = 3;
		lyricsExp = new String[numberOfLyricsExp];
		lyricsExp[0] = "one";
		lyricsExp[1] = "then";
		lyricsExp[2] = "better";
	}

	/**
	 * Test method for {@link br.lyfi.LyricsFinder#find(java.lang.String)}.
	 */
	@Test
	public void testFind() {
		assertNotNull(lyfi);
		String result;
		for (int i = 0; i < numberOfLyricsExp; i++) {
			System.out.println("Performing index search #" + (i + 1));
			result = lyfi.find(lyricsExp[i]);
			assertNotNull(result);
			System.out.println(result);
		}
	}
}
