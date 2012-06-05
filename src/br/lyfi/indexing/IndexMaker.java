package br.lyfi.indexing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

import lyrics.crawler.Crawler;
import lyrics.crawler.LyricsNotFoundException;
import lyrics.crawler.LyricsWikiaCrawler;
import lyrics.crawler.MetroLyricsCrawler;
import lyrics.crawler.SongLyricsCrawler;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * 
 * 
 * @author migmruiz
 * 
 */
public class IndexMaker {
	private IndexWriter indexWriter;

	/* Location of directory where index files are stored */
	private String indexDirectory;

	/* Location of data directory */
	private String dataDirectory;

	public IndexMaker(String dataDirectory, String indexDirectory) {
		this.dataDirectory = dataDirectory;
		this.indexDirectory = indexDirectory;
	}

	public IndexMaker() {
		this(System.getProperty("user.dir"), System
				.getProperty("java.io.tmpdir"));
	}
	
	/**
	 * This method creates an instance of IndexWriter which is used to add
	 * Documents and write indexes on the disc.
	 */
	public void createIndexWriter() {
		if (indexWriter == null) {
			try {
				File path = new File(indexDirectory);
				// Create instance of Directory where index files will be stored
				Directory fsDirectory = FSDirectory.open(path);
				/*
				 * Create instance of analyzer, which will be used to tokenize
				 * the input data
				 */
				Analyzer standardAnalyzer = new StandardAnalyzer(
						Version.LUCENE_36);
				IndexWriterConfig config = new IndexWriterConfig(
						Version.LUCENE_36, standardAnalyzer);
				// Configure the deletion policy
				config.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());

				// Create a new index
				indexWriter = new IndexWriter(fsDirectory, config);
			} catch (IOException ie) {
				System.out.println("Error in creating IndexWriter");
				throw new RuntimeException(ie);
			}
		}
	}

	/**
	 * This method reads data directory and loads all properties files. It
	 * extracts various fields and writes them to the index using IndexWriter.
	 * @return true if it has files to index and false if it doesn't
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void indexData() throws FileNotFoundException, IOException {
		
		File[] files = getFilesToBeIndexed();
		for (File file : files) {
			
			/* Step 1. Prepare the data for indexing. Extract the data. */
			
			String lyrics = null;
			String artist = null;
			String title = null;
			
			if (file.isFile()) {
				try {
					AudioFile audioFile = AudioFileIO.read(file);
					Tag tag = audioFile.getTag();
					if (tag == null) {
					System.out.println("warning: The file " + file
								+ " doesn't have any tag");
					}
					lyrics = tag.getFirst(FieldKey.LYRICS);
					artist = tag.getFirst(FieldKey.ARTIST);
					title = tag.getFirst(FieldKey.TITLE);
					if (!Pattern.matches("\\s*", lyrics)) {
						// lyrics already in the mp3 file
					} else {
						// We want to look for the lyrics in the web
						Vector<Crawler> crawlers = new Vector<Crawler>();
						crawlers.add(new LyricsWikiaCrawler());
						crawlers.add(new MetroLyricsCrawler());
						crawlers.add(new SongLyricsCrawler());
						for (Crawler crawler : crawlers) {
							//logger.info("Searching lyrics for " + title
							//		+ " by " + artistName + " with "
							//		+ crawler.getClass());
							try {
								lyrics = crawler.getLyrics(artist, title);
								tag.setField(FieldKey.LYRICS, lyrics);
								audioFile.commit();
								//notifySuccess(artist, title);

								if (Pattern.matches("\\s*", lyrics)) {
									System.out.println(lyrics + " in " + artist + " - " + title);
									// If the lyrics are not meaningful I drop them
									tag.setField(FieldKey.LYRICS, "");
									audioFile.commit();
								} else {
									break;
								}
							} catch (LyricsNotFoundException ex) {
							}
						}
					}
					
				} catch (Exception e) {
					System.out.println("Error getting lyrics for " + file);
				}
			}			
			
			String mp3file = file.getAbsolutePath();

			/* Step 2. Wrap the data in the Fields and add them to a Document */

			/*
			 * We plan to show the value of artist, title and mp3 file
			 * location along with the search results,for this we need to store
			 * their values in the index
			 */

			Field artistField = new Field("artist", artist, Field.Store.YES,
					Field.Index.NOT_ANALYZED);


			Field titleField = new Field("title", title, Field.Store.YES,
					Field.Index.ANALYZED);

			if (title.toLowerCase().indexOf("pune") != -1) {
				// Display search results that contain pune in their title
				// first by setting boost factor
				// titleField.setBoost(2.2F);
			}

			Field lyricsField = new Field("lyrics", lyrics, Field.Store.NO,
					Field.Index.ANALYZED);

			Field mp3FileField = new Field("mp3Doc", mp3file,
					Field.Store.YES, Field.Index.NO);

			// Add these fields to a Lucene Document
			Document doc = new Document();
			doc.add(artistField);
			doc.add(titleField);
			doc.add(lyricsField);
			doc.add(mp3FileField);
			

			// Step 3: Add this document to Lucene Index.
			indexWriter.addDocument(doc);
		}
		/*
		 * Commits all changes to the index.
		 */
		indexWriter.commit();
	}
	
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	private File[] getFilesToBeIndexed() {
		File dataDir = new File(dataDirectory);
		if (!dataDir.exists()) {
			throw new RuntimeException(dataDirectory + " does not exist");
		}
		File[] files = dataDir.listFiles();
		return files;
	}
}
