package br.lyfi.postindexing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
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

	private String indexDirPath;
	private String dataDirPath;
	private IndexWriter indexWriter;
	private String[] lyricsExp;
	private int numberOfLyricsExp;
	private LyricsIndexFinder lyinfiIW, lyinfiPath;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println(this.getClass().getSimpleName()
				+ " test: setting up...");
		indexDirPath = "resources/test/indexdir";
		dataDirPath = "resources/test/datadir";
		IndexMaker indexMaker = new IndexMaker(indexDirPath, dataDirPath);
		indexMaker.createIndexWriter();
		indexWriter = indexMaker.getIndexWriter();
		File dataDir = new File(dataDirPath);
		if (indexWriter.numDocs() < dataDir.list().length) {
			indexMaker.indexData();
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
		try {
			lyinfiPath = new LyricsIndexFinder(indexDirPath);
		} catch (CorruptIndexException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(lyinfiPath);
	}

	/**
	 * Test method for
	 * {@link br.lyfi.postindexing.LyricsIndexFinder#LyricsIndexFinder(org.apache.lucene.index.IndexWriter)}
	 * .
	 */
	@Test
	public void testLyricsIndexFinderIndexWriter() {
		try {
			lyinfiIW = new LyricsIndexFinder(indexWriter);
		} catch (CorruptIndexException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(lyinfiIW);
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
		try {
			time = System.nanoTime();
			lyinfiPath = new LyricsIndexFinder(indexDirPath);
			time = System.nanoTime() - time;
		} catch (IOException e) {
			fail(e.getMessage());
		}
		System.out.println("LyricsIndexFinder(String path) is created in: "
				+ time);
		for (int i = 0; i < numberOfLyricsExp; i++) {
			try {
				System.out.println("Performing index search #" + (i + 1) + ": \""
						+ lyricsExp[i] + "\" with String path "
						+ "created LyricsIndexFinder");
				assertNotNull(lyinfiPath.find(lyricsExp[i]));
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}

		// IndexWriter instance case
		try {
			time = System.nanoTime();
			lyinfiIW = new LyricsIndexFinder(indexWriter);
			time = System.nanoTime() - time;
		} catch (IOException e) {
			fail(e.getMessage());
		}
		System.out.println("LyricsIndexFinder(IndexWriter iw) is created in: "
				+ time);
		for (int i = 0; i < numberOfLyricsExp; i++) {
			try {
				System.out.println("Performing index search #" + (i + 1) + ": \""
						+ lyricsExp[i] + "\" with IndexWriter instance created "
						+ "LyricsIndexFinder");
				assertNotNull(lyinfiIW.find(lyricsExp[i]));
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
	}

	@After
	public void closeAll() throws IOException {
		Directory dir = null;
		try {
			dir = FSDirectory.open(new File(indexDirPath));
			indexWriter.close();
		} finally {
			if (IndexWriter.isLocked(dir)) {
				IndexWriter.unlock(dir);
			}
		}
	}

}
