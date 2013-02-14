package br.lyfi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.lyfi.indexing.IndexMaker;
import br.lyfi.postindexing.LyricsIndexFinder;

/**
 * Main class that performs all needed action to provide an index and make a
 * search on it
 * 
 * @author migmruiz
 * 
 */
public class LyricsFinder {

	// Application constants

	public final static String NAME = "Lyrics Finder";
	public final static String SIMPLE_NAME = "lyfi";

	public final static String IMG_APP_ICON_PATH = "resources"
			+ System.getProperty("file.separator") + "icon_128x128.png";

	// a path to directory where Lucene will store index files
	private final String indexDirPath;
	// a path to directory which contains data files that need to be indexed
	private final String dataDirPath;
	// whether or not forces a new indexing process
	private final boolean forceIndex;

	private IndexWriter indexWriter;
	private LyricsIndexFinder finder;

	/**
	 * Default constructor, uses "java.io.tmpdir" as parent to index directory
	 * and "user.dir" as data directory
	 */
	public LyricsFinder() {
		this(System.getProperty("user.dir"));
	}

	/**
	 * Constructor to a LyricsFinder, uses "java.io.tmpdir" as parent to index
	 * directory
	 * 
	 * @param dataDirPath
	 *            the path to the data directory, data directory must exist
	 */
	public LyricsFinder(final String dataDirPath) {
		this(System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator")
				+ LyricsFinder.SIMPLE_NAME + "_index", dataDirPath);
	}

	/**
	 * Constructor to a LyricsFinder
	 * 
	 * @param indexDirPath
	 *            the path to the index directory
	 * @param dataDirPath
	 *            the path to the data directory, data directory must exist
	 */
	public LyricsFinder(final String indexDirPath, final String dataDirPath) {
		this(indexDirPath, dataDirPath, false);
	}

	/**
	 * Constructor to a LyricsFinder setting all the possible parameters
	 * 
	 * @param indexDirPath
	 *            the path to the index directory
	 * @param dataDirPath
	 *            the path to the data directory, data directory must exist
	 * @param forceIndex
	 *            true if it is intended to force index files again, defaults to
	 *            false
	 */
	public LyricsFinder(final String indexDirPath, final String dataDirPath,
			final boolean forceIndex) {
		this.indexDirPath = indexDirPath;
		this.dataDirPath = dataDirPath;
		this.forceIndex = forceIndex;

		createLuceneIndex();
		createIndexFinder();

		// close the indexWriter
		try (Directory dir = FSDirectory.open(new File(indexDirPath))){
			try {
				if (IndexWriter.isLocked(dir)) {
					IndexWriter.unlock(dir);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Index is corrupted", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs a search on the indexed music library
	 * 
	 * @param lyricsExp
	 *            the partial lyrics expression
	 * @return the an array with full path to the first mp3 file on the first
	 *         index, the minimal description artist - title top results on the
	 *         second and the results with the found lyrics on the third {path
	 *         to the first, artist - title top results,artist - title and
	 *         lyrics of top results}
	 */
	public String[] find(final String lyricsExp) {

		final List<Document> documents;
		try {
			documents = finder.find(lyricsExp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final String pathToFirst;
		String output = "";
		String fullOutput = "";
		for (final Document doc : documents) {
			output = output + "Song: "
					+ doc.getFieldable("artist").stringValue() + " - "
					+ doc.getFieldable("title").stringValue()
					+ System.getProperty("line.separator");
			output = output + "File location: "
					+ doc.getFieldable("mp3FileDoc").stringValue()
					+ System.getProperty("line.separator");
			fullOutput = "Lyrics: " + doc.getFieldable("lyrics").stringValue()
					+ System.getProperty("line.separator") + output;
		}
		if (!documents.isEmpty()) {
			pathToFirst = documents.get(0).getFieldable("mp3FileDoc")
					.stringValue();
		} else {
			pathToFirst = "";
		}

		return new String[] { pathToFirst, output, fullOutput };
	}

	/**
	 * Creates a local IndexMaker to index the dataDirectory, storing the
	 * private indexWriter too
	 */
	private void createLuceneIndex() {
		final IndexMaker indexMaker = new IndexMaker(indexDirPath, dataDirPath);
		indexMaker.createIndexWriter();
		indexWriter = indexMaker.getIndexWriter();
		try {
			if (!isIndexCompatibleWithData() || forceIndex) {
				// Index data
				indexMaker.indexData();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Compares stored index with the data directory
	 * 
	 * @return true if the index is compatible with the data directory
	 * @throws IOException
	 */
	private boolean isIndexCompatibleWithData() throws IOException {
		final Path dataDir = Paths.get(dataDirPath);
		if (!Files.exists(dataDir)) {
			Files.createDirectories(dataDir);
		}
		return indexWriter.numDocs() == dataDir.toFile().list().length;
	}

	/**
	 * Creates a LyricsIndexFinder instance for the same index provided by
	 * createLuceneIndex() and stores it privately
	 */
	private void createIndexFinder() {
		try {
			finder = new LyricsIndexFinder(indexWriter);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Index Corrupted", e);
		} catch (IndexNotFoundException e) {
			throw new RuntimeException("Index not found", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
