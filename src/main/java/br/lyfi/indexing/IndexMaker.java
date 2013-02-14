package br.lyfi.indexing;

import java.io.Closeable;
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
public class IndexMaker implements Closeable {
	private IndexWriter indexWriter;

	/* Location of the data directory */
	private final String dataDirectory;

	/* Directory where index files are stored */
	private final Directory fsDirectory;

	/**
	 * Constructor for the IndexMaker
	 * 
	 * @param indexDirectory
	 *            Location of directory where index files will be stored
	 * @param dataDirectory
	 *            Location of the data directory
	 */
	public IndexMaker(final String indexDirectory, final String dataDirectory) {
		this.dataDirectory = dataDirectory;

		// Create instance of Directory where index files will be stored
		try {
		    this.fsDirectory  = FSDirectory.open(new File(indexDirectory));
		} catch (IOException e) {
		    throw new RuntimeException(e);
	    }
	}

	/**
	 * This method creates an instance of IndexWriter which is used to add
	 * Documents and write indexes on the disc.
	 */
	public void createIndexWriter() {
		if (indexWriter == null) {

			/*
			 * Create instance of analyzer, which will be used to tokenize
			 * the input data
			 */
			try (final Analyzer standardAnalyzer = new StandardAnalyzer(
							Version.LUCENE_36)) {
				
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

		final File[] files = getFilesToBeIndexed();
		for (final File file : files) {

			/* Step 1. Prepare the data for indexing. Extract the data. */

			String lyrics = null;
			String artist = null;
			String title = null;

			if (file.isFile()) {
				try {
					final AudioFile audioFile = AudioFileIO.read(file);
					final Tag tag = audioFile.getTag();
					if (tag == null) {
						System.out.println("warning: The file " + file
								+ " doesn't have any tag");
					} else {
						lyrics = tag.getFirst(FieldKey.LYRICS);
						artist = tag.getFirst(FieldKey.ARTIST);
						title = tag.getFirst(FieldKey.TITLE);
						if (!Pattern.matches("\\s*", lyrics)) {
							// lyrics already in the mp3 file, use it!
						} else {
							// if not we want to look for the lyrics in the web
							final LyricsWebSearcher webSearcher = new LyricsWebSearcher();
							lyrics = webSearcher.fetchLyrics(artist, title);
						}
					}

				} catch (Exception e) {
					System.out.println("Error getting lyrics for " + file);
				}
			}

			final String mp3file = file.getAbsolutePath();

			/* Step 2. Wrap the data in the Fields and add them to a Document */

			/*
			 * We plan to show the value of artist, title, lyrics and mp3 file
			 * location along with the search results,for this we need to store
			 * their values in the index
			 */

			final Field artistField = new Field("artist", artist, Field.Store.YES,
					Field.Index.NOT_ANALYZED);

			final Field titleField = new Field("title", title, Field.Store.YES,
					Field.Index.ANALYZED);

			final Field lyricsField = new Field("lyrics", lyrics, Field.Store.YES,
					Field.Index.ANALYZED);

			final Field mp3FileField = new Field("mp3FileDoc", mp3file,
					Field.Store.YES, Field.Index.NO);

			// Add these fields to a Lucene Document
			final Document doc = new Document();
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
		// TODO lists dataDirectory's children list of files
		final File dataDir = new File(dataDirectory);
		if (!dataDir.exists()) {
			throw new RuntimeException(dataDirectory + " does not exist");
		}
		return dataDir.listFiles();
	}
	
	@Override public void close() throws IOException {
		fsDirectory.close();
	}
}
