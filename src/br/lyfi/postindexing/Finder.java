package br.lyfi.postindexing;

import org.apache.lucene.search.IndexSearcher;

/**
 * Class used searching indexed lyrics
 * 
 * @author migmruiz
 *
 */
public class Finder {
	private IndexSearcher finder;
	
	// TODO implement the search using Lucene
	public String result() {
		return finder.toString();
	}

}
