package br.lyfi;

import static org.junit.Assert.*;

import java.io.File;

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
		String[] result;
		result = new String[3];
		for (int i = 0; i < numberOfLyricsExp; i++) {
			System.out.println("Performing index search #" + (i + 1) + ": "
					+ lyricsExp[i]);
			result = lyfi.find(lyricsExp[i]);
			assertNotNull(result);
			assertNotNull(result[0]);
			assertNotNull(result[1]);
			assertNotNull(result[2]);
			if (!result[2].isEmpty()) {
				assertTrue((new File(result[0])).exists());
			}
			System.out.println(result[1]);

		}
	}
}
