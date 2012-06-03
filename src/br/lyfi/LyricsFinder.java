package br.lyfi;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;

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

	public LyricsFinder(String indexDirectory, String dataDirectory) {
		this.indexDirectory = indexDirectory;
		this.dataDirectory = dataDirectory;

		createLuceneIndex();
		createIndexFinder();
	}

	public String find(String lyricsExp) {
		try {
			finder.find("cause I'm the taxman");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// TODO handle search output

		return null;
	}

	private void createLuceneIndex() {
		IndexMaker indexMaker = new IndexMaker(indexDirectory, dataDirectory);
		// Create IndexWriter
		indexMaker.createIndexWriter();
		try {
			// Index data
			indexMaker.indexData();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createIndexFinder() {
		/*
		 * Create instance of IndexSearcher
		 */
		try {
			finder = new LyricsIndexFinder(indexDirectory);
		} catch (CorruptIndexException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
