package br.lyfi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

	private LyricsIndexFinder finder;

	private IndexWriter indexWriter;

	/**
	 * Constructor to a LyricsFinder
	 * 
	 * @param indexDirPath
	 *            the path to the index directory
	 * @param dataDirPath
	 *            the path to the data directory, data directory must exist
	 */
	public LyricsFinder(String indexDirPath, String dataDirPath) {
		this.indexDirPath = indexDirPath;
		this.dataDirPath = dataDirPath;

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
	 * @return the lyrics and full path to the mp3 file of the found lyrics
	 */
	public String find(String lyricsExp) {

		Vector<Document> documents;
		try {
			documents = finder.find(lyricsExp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String output = "";
		for (Document doc : documents) {
			output = output + "Lyrics: "
					+ doc.getFieldable("lyrics").stringValue() + "\n";
			output = output + "Music file location: "
					+ doc.getFieldable("mp3FileDoc").stringValue() + "\n";
		}

		return output;
	}

	/**
	 * Creates a local IndexMaker to index the dataDirectory, storing the
	 * private indexWriter too
	 */
	private void createLuceneIndex() {
		IndexMaker indexMaker = new IndexMaker(indexDirPath, dataDirPath);
		indexMaker.createIndexWriter();
		try {
			// Index data
			indexMaker.indexData();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		indexWriter = indexMaker.getIndexWriter();
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
