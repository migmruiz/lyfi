package br.lyfi.postindexing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.lyfi.indexing.IndexMaker;

/**
 * Automated tests for the {@link br.lyfi.postindexing.LyricsIndexFinder} class,
 * interesting results with The Beatles - Getting Better audio file on
 * resources/datadir
 * 
 * @author migmruiz
 * 
 */
public class LyricsIndexFinderTest {

	private static String indexDirPath;
	private static String dataDirPath;
	private static IndexWriter indexWriter;
	private static String[] lyricsExp;
	private static int numberOfLyricsExp;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println(LyricsIndexFinderTest.class.getSimpleName()
				+ " test: setting up...");
		indexDirPath = "src/test/resources/test/indexdir";
		dataDirPath = "src/test/resources/test/datadir";
		try (IndexMaker indexMaker = new IndexMaker(indexDirPath, dataDirPath)) {
			indexMaker.createIndexWriter();
			indexWriter = indexMaker.getIndexWriter();
			final File dataDir = new File(dataDirPath);
			if (indexWriter.numDocs() < dataDir.list().length) {
				indexMaker.indexData();
			}
		}
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
	 * Test method for
	 * {@link br.lyfi.postindexing.LyricsIndexFinder#LyricsIndexFinder(java.lang.String)}
	 * .
	 */
	@Test
	public void testLyricsIndexFinderString() {
		try (LyricsIndexFinder lyinfiPath = new LyricsIndexFinder(indexDirPath)) {
			assertNotNull(lyinfiPath);
		} catch (CorruptIndexException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link br.lyfi.postindexing.LyricsIndexFinder#LyricsIndexFinder(org.apache.lucene.index.IndexWriter)}
	 * .
	 */
	@Test
	public void testLyricsIndexFinderIndexWriter() {
		try (LyricsIndexFinder lyinfiIW = new LyricsIndexFinder(indexWriter)) {
			assertNotNull(lyinfiIW);
		} catch (CorruptIndexException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link br.lyfi.postindexing.LyricsIndexFinder#find(java.lang.String)} and
	 * overall benchmark.
	 */
	@Test
	public void testFind_Benchmark() {
		long time = 0l;
		// String path case
		time = System.nanoTime();
		try (LyricsIndexFinder lyinfiPath = new LyricsIndexFinder(indexDirPath)) {

			for (int i = 0; i < numberOfLyricsExp; i++) {
				try {
					System.out.println("Performing index search #" + (i + 1)
							+ ": \"" + lyricsExp[i] + "\" with String path "
							+ "created LyricsIndexFinder");
					assertNotNull(lyinfiPath.find(lyricsExp[i]));
				} catch (IOException e) {
					fail(e.getMessage());
				}
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}

		time = System.nanoTime() - time;
		System.out.println("LyricsIndexFinder(String path) is created in: "
				+ time);

		// IndexWriter instance case

		time = System.nanoTime();
		try (LyricsIndexFinder lyinfiIW = new LyricsIndexFinder(indexWriter)) {
			for (int i = 0; i < numberOfLyricsExp; i++) {
				try {
					System.out.println("Performing index search #" + (i + 1)
							+ ": \"" + lyricsExp[i]
							+ "\" with IndexWriter instance created "
							+ "LyricsIndexFinder");
					assertNotNull(lyinfiIW.find(lyricsExp[i]));
				} catch (IOException e) {
					fail(e.getMessage());
				}
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}

		time = System.nanoTime() - time;
		System.out.println("LyricsIndexFinder(IndexWriter iw) is created in: "
				+ time);

	}
	
	@AfterClass public static void closeAll() throws CorruptIndexException, IOException {
		indexWriter.close();
	}

}
