package br.lyfi.ui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author migmruiz
 * 
 */
public class LyricsFinderCommandLineUITest {
	private String testIndexDirPath;
	private String testDataDirPath;
	private String[] lyricsExp;
	private int numberOfLyricsExp;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println(this.getClass().getSimpleName()
				+ " test: setting up...");
		testIndexDirPath = "src/test/resources/test/indexdir";
		testDataDirPath = "src/test/resources/test/datadir";
		numberOfLyricsExp = 3;
		lyricsExp = new String[numberOfLyricsExp];
		lyricsExp[0] = "one";
		lyricsExp[1] = "then";
		lyricsExp[2] = "better";
	}

	@Test
	public void test() {
		final String[] args = new String[] { "-d", testDataDirPath, "-i",
				testIndexDirPath, "-f", lyricsExp[2] };
		assertNotNull(args);
		LyricsFinderCommandLineUI.main(args);
	}

}
