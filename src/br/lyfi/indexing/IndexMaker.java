package br.lyfi.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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

	/*
	public void makeIndex() {
		File path = new File(indexDirectory);
		// Create instance of Directory where index files will be stored
		Directory fsDirectory = null;
		try {
			fsDirectory = FSDirectory.open(path);
		} catch (IOException e) {
			// TOD_O Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Create instance of analyzer, which will be used to tokenize the input
		 * data
		 *_
		Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				standardAnalyzer);

		config.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
		
		// Create a new index
		try {
			IndexWriter indexWriter = new IndexWriter(fsDirectory, config);
		} catch (CorruptIndexException e) {
			// TOD_O Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TOD_O Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TOD_O Auto-generated catch block
			e.printStackTrace();
		}

	}
	*/
	
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
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void indexData() throws FileNotFoundException, IOException {
		
		// TODO analyze ID3 tags from mp3 files

		File[] files = getFilesToBeIndxed();
		for (File file : files) {
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));

			/* Step 1. Prepare the data for indexing. Extract the data. */
			String sender = properties.getProperty("sender");
			String receiver = properties.getProperty("receiver");
			String date = properties.getProperty("date");
			String month = properties.getProperty("month");
			String subject = properties.getProperty("subject");
			String message = properties.getProperty("message");
			String emaildoc = file.getAbsolutePath();

			/* Step 2. Wrap the data in the Fields and add them to a Document */

			/*
			 * We plan to show the value of sender, subject and email document
			 * location along with the search results,for this we need to store
			 * their values in the index
			 */

			Field senderField = new Field("sender", sender, Field.Store.YES,
					Field.Index.NOT_ANALYZED);

			Field receiverfield = new Field("receiver", receiver,
					Field.Store.NO, Field.Index.NOT_ANALYZED);

			Field subjectField = new Field("subject", subject, Field.Store.YES,
					Field.Index.ANALYZED);

			if (subject.toLowerCase().indexOf("pune") != -1) {
				// Display search results that contain pune in their subject
				// first by setting boost factor
				subjectField.setBoost(2.2F);
			}

			Field emaildatefield = new Field("date", date, Field.Store.NO,
					Field.Index.NOT_ANALYZED);

			Field monthField = new Field("month", month, Field.Store.NO,
					Field.Index.NOT_ANALYZED);

			Field messagefield = new Field("message", message, Field.Store.NO,
					Field.Index.ANALYZED);

			Field emailDocField = new Field("emailDoc", emaildoc,
					Field.Store.YES, Field.Index.NO);

			// Add these fields to a Lucene Document
			Document doc = new Document();
			doc.add(senderField);
			doc.add(receiverfield);
			doc.add(subjectField);
			doc.add(emaildatefield);
			doc.add(monthField);
			doc.add(messagefield);
			doc.add(emailDocField);
			if (sender.toLowerCase().indexOf("job") != -1) {
				// Display search results that contain 'job' in their sender
				// email address
				doc.setBoost(2.1F);
			}

			// Step 3: Add this document to Lucene Index.
			indexWriter.addDocument(doc);
		}
		/*
		 * Commits all changes to the index and closes all associated files.
		 */
		indexWriter.close();
	}

	private File[] getFilesToBeIndxed() {
		File dataDir = new File(dataDirectory);
		if (!dataDir.exists()) {
			throw new RuntimeException(dataDirectory + " does not exist");
		}
		File[] files = dataDir.listFiles();
		return files;
	}
}
