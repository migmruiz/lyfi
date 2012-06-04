package br.lyfi.postindexing;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * Class used searching indexed lyrics
 * 
 * @author migmruiz
 * 
 */
public class LyricsIndexFinder {
	private IndexSearcher finder;
	private IndexReader indexReader;

	public LyricsIndexFinder(IndexWriter iw) throws CorruptIndexException, IOException {
		indexReader = IndexReader.open(iw, false);
		finder = new IndexSearcher(indexReader);
	}
	
	// TODO needs to work
	public LyricsIndexFinder(String indexDirectory) throws CorruptIndexException,
			IOException {
		indexReader = IndexReader.open(FSDirectory
				.open(new File(indexDirectory)));
		finder = new IndexSearcher(indexReader);
	}

	public void find(String lyricsExp) throws IOException {
		
		Term term = new Term("lyrics", lyricsExp);
		Query termQuery = new TermQuery(term);
		TopDocs topDocs = finder.search(termQuery, 10);
		
		// TODO handle search output
		if (topDocs.totalHits > 0) {
			Document doc = finder.doc(topDocs.scoreDocs[0].doc);
			System.out.println("Lyrics: "
					+ doc.getFieldable("lyrics").stringValue());
			System.out.println("Music file location: "
					+ doc.getFieldable("musicDoc").stringValue());
		}
		// TODO Log exception
	}
}
