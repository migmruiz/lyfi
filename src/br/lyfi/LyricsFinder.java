package br.lyfi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	private String indexDirectory;
	// a path to directory which contains data files that need to be indexed
	private String dataDirectory;

	private LyricsIndexFinder finder;

	private IndexWriter indexWriter;

	public LyricsFinder(String indexDirectory, String dataDirectory) {
		this.indexDirectory = indexDirectory;
		this.dataDirectory = dataDirectory;

		createLuceneIndex();
		createIndexFinder();

		
		// close the indexWriter
		Directory dir = null;
		try {
			dir = FSDirectory.open(new File(indexDirectory));
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

	public String find(String lyricsExp) {

		try {
			finder.find(lyricsExp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// TODO handle search output

		return null;
	}

	private void createLuceneIndex() {
		IndexMaker indexMaker = new IndexMaker(indexDirectory, dataDirectory);
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

	private void createIndexFinder() {

		// Create instance of IndexSearcher
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
