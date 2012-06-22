package br.lyfi.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.lyfi.LyricsFinder;

/**
 * @author migmruiz
 * 
 */
public class LyricsFinderSWT_UI extends Composite {

	Text _dataDirField;
	Text _lyricsField;
	Text _artistField;

	List<Text> _fields; // all fields
	
	Button _dataDirBrowse;

	DirectoryDialog _dataDirFileDialog;

	Button _findButton;
	Button _clearButton;

	public LyricsFinderSWT_UI(Shell shell) {
		this(shell, SWT.NONE);
	}

	public LyricsFinderSWT_UI(Shell shell, int style) {
		super(shell, style);
		_fields = new ArrayList<Text>();
		_dataDirFileDialog = new DirectoryDialog(shell, SWT.OPEN);
		createGui();
	}

	protected void clearFields() {
		for (Text field : _fields) {
			field.setText("");
		}
	}

	protected Text createLabelledText(Composite parent, String label,
			int limit, String tip) {
		Label l = new Label(parent, SWT.LEFT);
		l.setText(label);
		Text text = new Text(parent, SWT.SINGLE);
		if (limit > 0) {
			text.setTextLimit(limit);
		}
		if (tip != null) {
			text.setToolTipText(tip);
		}
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		_fields.add(text);
		return text;
	}

	protected Button createButton(Composite parent, String label, String tip,
			SelectionListener l) {
		Button b = new Button(parent, SWT.NONE);
		b.setText(label);
		if (tip != null) {
			b.setToolTipText(tip);
		}
		if (l != null) {
			b.addSelectionListener(l);
		}
		return b;
	}

	protected void createGui() {

		setLayout(new GridLayout(1, true));

		// create the setup area

		Group entryGroup = new Group(this, SWT.NONE);
		entryGroup.setText("Setup");
		// use 2 columns, not same width
		GridLayout entryLayout = new GridLayout(2, false);
		entryGroup.setLayout(entryLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 400;
		entryGroup.setLayoutData(gridData);
		// make all buttons the same size
		FillLayout buttonLayout = new FillLayout();
		buttonLayout.marginHeight = 2;
		buttonLayout.marginWidth = 2;
		buttonLayout.spacing = 5;
		Composite browseButton = new Composite(this, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		browseButton.setLayout(buttonLayout);

		_dataDirField = createLabelledText(entryGroup, "Data directory: ", 80,
				"Enter the data directory path"
						+ " or use the browse button below");

		_dataDirBrowse = createButton(browseButton, "&Browse",
				"Browse and select" + " the audio library directory",
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						String filename = _dataDirFileDialog.open();
						if (filename != null) {
							_dataDirField.setText(filename);
						}
					}
				});

		// create the input area

		entryGroup = new Group(this, SWT.NONE);
		entryGroup.setText("Input Values");
		entryGroup.setLayout(entryLayout);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 400;
		entryGroup.setLayoutData(gridData);

		_lyricsField = createLabelledText(entryGroup, "Partial lyrics: ", 40,
				"Enter the partial lyrics");
		_artistField = createLabelledText(entryGroup, "Artist: ", 20,
				"Enter the artist name, if you remember");

		// create the button area

		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// set all buttons the same size
		buttons.setLayout(buttonLayout);

		_findButton = createButton(buttons, "&Find", "Process input and find"
				+ " the matching audio file", new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO graphical output
				System.out.println("Data directory path:         "
						+ _dataDirField.getText());
				System.out.println("Partial lyrics:         "
						+ _lyricsField.getText());
				System.out.println("Artist:      " + _artistField.getText());
				if (validate()) {
					doFind(_dataDirField.getText(), _lyricsField.getText(),
							_artistField.getText());
				}
			}
		});

		_clearButton = createButton(buttons, "&Clear", "clear inputs",
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						clearFields();
						_lyricsField.forceFocus();
					}
				});
	}

	protected boolean validate() {
		boolean valid = false;
		if (!(_dataDirField.getText() == null || _lyricsField.getText() == null)) {
			valid = !(_dataDirField.getText().isEmpty() || _lyricsField
					.getText().isEmpty());
		}
		return valid;
	}

	protected void doFind(String dataDirPath, String lyricsExp, String artist) {

		// TODO Use given artist
		LyricsFinder lyfi = new LyricsFinder(dataDirPath);
		String[] result = lyfi.find(lyricsExp);
		// TODO graphical output
		System.out.println(result[1]);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		
		Image icon = new Image(display, LyricsFinder.IMG_APP_ICON_PATH);
		
		shell.setText(LyricsFinder.NAME);
		shell.setLayout(new FillLayout());
		shell.setImage(icon);

		@SuppressWarnings("unused")
		LyricsFinderSWT_UI basic = new LyricsFinderSWT_UI(shell);

		shell.pack();
		shell.open();

		// main loop
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

}
