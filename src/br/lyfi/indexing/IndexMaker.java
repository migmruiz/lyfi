package br.lyfi.indexing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

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

import br.lyfi.preindexing.LyricsWebSearcher;

/**
 * This class reads the input audio files from the data directory, analyzes it's
 * contents, search the web for it's lyrics and creates indexes and writes them
 * in the index directory
 * 
 * @author migmruiz
 * 
 */
public class IndexMaker {
	private IndexWriter indexWriter;

	/* Location of directory where index files are stored */
	private String indexDirectory;

	/* Location of the data directory */
	private String dataDirectory;

	/**
	 * Constructor for the IndexMaker
	 * 
	 * @param indexDirectory
	 *            Location of directory where index files will be stored
	 * @param dataDirectory
	 *            Location of the data directory
	 */
	public IndexMaker(String indexDirectory, String dataDirectory) {
		this.indexDirectory = indexDirectory;
		this.dataDirectory = dataDirectory;
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
	 * This method reads data directory and loads all audio files. It extracts
	 * various tags and writes them to the index using IndexWriter.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void indexData() throws FileNotFoundException, IOException {
		
		// clean old index
		indexWriter.deleteAll();
		indexWriter.commit();
		
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
						// lyrics already in the mp3 file, use it!
					} else {
						// if not we want to look for the lyrics in the web
						LyricsWebSearcher webSearcher = new LyricsWebSearcher();
						lyrics = webSearcher.fetchLyrics(artist, title);
					}

				} catch (Exception e) {
					System.out.println("Error getting lyrics for " + file);
				}
			}

			String mp3file = file.getAbsolutePath();

			/* Step 2. Wrap the data in the Fields and add them to a Document */

			/*
			 * We plan to show the value of artist, title, lyrics and mp3 file location
			 * along with the search results,for this we need to store their
			 * values in the index
			 */

			Field artistField = new Field("artist", artist, Field.Store.YES,
					Field.Index.NOT_ANALYZED);

			Field titleField = new Field("title", title, Field.Store.YES,
					Field.Index.ANALYZED);

			Field lyricsField = new Field("lyrics", lyrics, Field.Store.YES,
					Field.Index.ANALYZED);

			Field mp3FileField = new Field("mp3FileDoc", mp3file, Field.Store.YES,
					Field.Index.NO);

			// Add these fields to a Lucene Document
			Document doc = new Document();
			doc.add(artistField);
			doc.add(titleField);
			doc.add(lyricsField);
			doc.add(mp3FileField);

			/* Step 3: Add this document to Lucene Index. */
			indexWriter.addDocument(doc);
		}
		/*
		 * Commits all changes to the index.
		 */
		indexWriter.commit();
	}

	/**
	 * Getter for IndexWriter instance created that manages indexDirectory to
	 * index dataDirectory
	 * 
	 * @return IndexWriter instance
	 */
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	/**
	 * If dataDirectory does not exist, throw a RuntimeException. If it does,
	 * lists it's files.
	 * 
	 * @return Returns an array of Files denoting the files in the dataDirectory
	 *         directory.
	 */
	private File[] getFilesToBeIndexed() {
		File dataDir = new File(dataDirectory);
		if (!dataDir.exists()) {
			throw new RuntimeException(dataDirectory + " does not exist");
		}
		File[] files = dataDir.listFiles();
		return files;
	}
}
