package br.lyfi.postindexing;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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

	/**
	 * Constructor that should be faster and less error-prone as it uses the
	 * inner references of the IndexWriter instance of the index of the search.
	 * 
	 * @param iw
	 *            the IndexWriter instance of the index of the search
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public LyricsIndexFinder(IndexWriter iw) throws CorruptIndexException,
			IOException {
		indexReader = IndexReader.open(iw, true);
		finder = new IndexSearcher(indexReader);
	}

	/**
	 * Constructor of the finder using the direct path reference to the index
	 * directory location
	 * 
	 * @param indexPath
	 *            the location of the index of the search
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public LyricsIndexFinder(String indexPath) throws CorruptIndexException,
			IOException {
		indexReader = IndexReader.open(FSDirectory.open(new File(indexPath)));
		finder = new IndexSearcher(indexReader);
	}

	/**
	 * Performs a TermQuery search for the expression lyricsExp
	 * 
	 * @param lyricsExp
	 *            the partial lyrics expression
	 * @return a Vector of Documents with the top 10 hits, each Document
	 *         representing a music file, with its associated lyrics,
	 * @throws IOException
	 */
	public Vector<Document> find(String lyricsExp) throws IOException {

		Term term = new Term("lyrics", lyricsExp);
		Query termQuery = new TermQuery(term);
		TopDocs topDocs = finder.search(termQuery, 10);

		System.out.println("Total hits " + topDocs.totalHits);

		// Get an array of references to matched documents
		ScoreDoc[] scoreDosArray = topDocs.scoreDocs;
		Vector<Document> documents = new Vector<Document>(topDocs.totalHits);
		for (ScoreDoc scoredoc : scoreDosArray) {
			documents.add(finder.doc(scoredoc.doc));
		}
		return documents;

	}
}
