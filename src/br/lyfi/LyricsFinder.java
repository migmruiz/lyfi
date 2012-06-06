package br.lyfi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Vector;

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

	// a path to directory where Lucene will store index files
	private String indexDirPath;
	// a path to directory which contains data files that need to be indexed
	private String dataDirPath;
	// whether or not forces a new indexing process
	private boolean forceIndex;

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
	public LyricsFinder(String dataDirPath) {
		this(System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator") + "lyfi_index",
				dataDirPath);
	}

	/**
	 * Constructor to a LyricsFinder
	 * 
	 * @param indexDirPath
	 *            the path to the index directory
	 * @param dataDirPath
	 *            the path to the data directory, data directory must exist
	 */
	public LyricsFinder(String indexDirPath, String dataDirPath) {
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
	public LyricsFinder(String indexDirPath, String dataDirPath,
			boolean forceIndex) {
		this.indexDirPath = indexDirPath;
		this.dataDirPath = dataDirPath;
		this.forceIndex = forceIndex;

		createLuceneIndex();
		createIndexFinder();

		// close the indexWriter
		Directory dir = null;
		try {
			dir = FSDirectory.open(new File(indexDirPath));
			indexWriter.close();
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Index is corrupted", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (IndexWriter.isLocked(dir)) {
					IndexWriter.unlock(dir);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
	public String[] find(String lyricsExp) {

		Vector<Document> documents;
		try {
			documents = finder.find(lyricsExp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String pathToFirst;
		String output = "";
		String fullOutput = "";
		for (Document doc : documents) {
			output = output + "Song: " + doc.getFieldable("artist").stringValue() + " - "
					+ doc.getFieldable("title").stringValue() + "\n";
			output = output + "File location: "
					+ doc.getFieldable("mp3FileDoc").stringValue() + "\n";
			fullOutput = "Lyrics: " + doc.getFieldable("lyrics").stringValue()
					+ "\n" + output;
		}
		try {
			pathToFirst = documents.firstElement().getFieldable("mp3FileDoc")
					.stringValue();
		} catch (NoSuchElementException e) {
			pathToFirst = "";
		}

		return new String[] { pathToFirst, output, fullOutput };
	}

	/**
	 * Creates a local IndexMaker to index the dataDirectory, storing the
	 * private indexWriter too
	 */
	private void createLuceneIndex() {
		IndexMaker indexMaker = new IndexMaker(indexDirPath, dataDirPath);
		indexMaker.createIndexWriter();
		indexWriter = indexMaker.getIndexWriter();
		try {
			if (isIndexCompatibleWithData() || forceIndex) {
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
		File dataDir = new File(dataDirPath);
		return indexWriter.getDirectory().equals(FSDirectory.open(dataDir))
				&& indexWriter.numDocs() < dataDir.list().length;
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
