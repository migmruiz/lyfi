package br.lyfi;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * Sample application test, interesting results with The Beatles - Getting
 * Better audio file on resources/datadir
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
		System.out.println(this.getClass().getSimpleName()
				+ " test: setting up...");
		testIndexDirPath = "resources/indexdir";
		testDataDirPath = "resources/datadir";
		lyfi = new LyricsFinder(testIndexDirPath, testDataDirPath);
		numberOfLyricsExp = 6;
		lyricsExp = new String[numberOfLyricsExp];
		lyricsExp[0] = "one";
		lyricsExp[1] = "then";
		lyricsExp[2] = "better";
		lyricsExp[3] = "better all";
		lyricsExp[4] = "the time"; // FIXME should give a hit, but does not
		lyricsExp[5] = "time";
	}

	/**
	 * Test method for {@link br.lyfi.LyricsFinder#find(java.lang.String)}.
	 */
	@Test
	public void testFind() {
		assertNotNull(lyfi);
		String[] result;
		for (int i = 0; i < numberOfLyricsExp; i++) {
			System.out.println("Performing index search #" + (i + 1) + ": \""
					+ lyricsExp[i] + "\"");
			result = lyfi.find(lyricsExp[i]);
			assertNotNull(result);
			assertNotNull(result[0]); // path
			assertNotNull(result[1]); // artist - title /n path
			assertNotNull(result[2]); // lyrics /n artist - title /n path
			if (!result[2].isEmpty()) {
				assertTrue((new File(result[0])).exists());
			}
			System.out.println(result[1]);

		}
	}
}
