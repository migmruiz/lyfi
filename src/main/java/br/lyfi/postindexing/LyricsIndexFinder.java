package br.lyfi.postindexing;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
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
public class LyricsIndexFinder implements Closeable {
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
	public LyricsIndexFinder(final IndexWriter iw) throws CorruptIndexException,
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
	public LyricsIndexFinder(final String indexPath) throws CorruptIndexException,
			IOException {
		indexReader = IndexReader.open(FSDirectory.open(new File(indexPath)));
		finder = new IndexSearcher(indexReader);
	}

	/**
	 * Performs a search for the expression lyricsExp
	 * 
	 * @param lyricsExp
	 *            the partial lyrics expression
	 * @return a List of Documents with the top 10 hits, each Document
	 *         representing a music file, with its associated lyrics,
	 * @throws IOException
	 */
	public List<Document> find(final String lyricsExp) throws IOException {

		final int nQuerys = 10;
		final TopDocs topDocs;
		final String[] words = lyricsExp.trim().split(" ");
		if (words.length == 1) {
			final Term term = new Term("lyrics", lyricsExp);
			final Query termQuery = new TermQuery(term);
			topDocs = finder.search(termQuery, nQuerys);
		} else {
			final PhraseQuery query = new PhraseQuery();
			query.setSlop(1);
			for (String word : words) {
				query.add(new Term("lyrics", word));
			}
			topDocs = finder.search(query, nQuerys);
		}

		System.out.println("Total hits " + topDocs.totalHits);

		// Get an array of references to matched documents
		final ScoreDoc[] scoreDosArray = topDocs.scoreDocs;
		final List<Document> documents = new ArrayList<>(topDocs.totalHits);
		for (final ScoreDoc scoredoc : scoreDosArray) {
			documents.add(finder.doc(scoredoc.doc));
		}
		return documents;

	}

	@Override public void close() throws IOException {
		indexReader.close();
		finder.close();
	}
}
