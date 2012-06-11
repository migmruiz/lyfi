package br.lyfi.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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

	Text dataDirField;
	Text lyricsField;
	Text artistField;

	List<Text> fields; // all fields

	public LyricsFinderSWT_UI(Composite parent) {
		this(parent, SWT.NONE);
	}

	public LyricsFinderSWT_UI(Composite parent, int style) {
		super(parent, style);
		fields = new ArrayList<Text>();
		createGui();
	}

	protected void clearFields() {
		for (Iterator<Text> i = fields.iterator(); i.hasNext();) {
			((Text) i.next()).setText("");
		}
	}

	protected Text createLabelledText(Composite parent, String label) {
		return createLabelledText(parent, label, 20, null);
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
		fields.add(text);
		return text;
	}

	protected Button createButton(Composite parent, String label,
			SelectionListener l) {
		return createButton(parent, label, l);
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

	// partial selection listener
	private class MySelectionAdapter implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			// default is to do nothing
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	protected void createGui() {
		setLayout(new GridLayout(1, true));

		// create the input area

		Group entryGroup = new Group(this, SWT.NONE);
		entryGroup.setText("Input Values");
		// use 2 columns, not same width
		GridLayout entryLayout = new GridLayout(2, false);
		entryGroup.setLayout(entryLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 400;
		entryGroup.setLayoutData(gridData);

		dataDirField = createLabelledText(entryGroup, "Data directory: ", 80,
				"Enter the data directory path");
		// TODO Set music directory using a browse button
		lyricsField = createLabelledText(entryGroup, "Partial lyrics: ", 40,
				"Enter the partial lyrics");
		artistField = createLabelledText(entryGroup, "Artist: ", 20,
				"Enter the artist name, if you remember");

		// create the button area

		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// make all buttons the same size
		FillLayout buttonLayout = new FillLayout();
		buttonLayout.marginHeight = 2;
		buttonLayout.marginWidth = 2;
		buttonLayout.spacing = 5;
		buttons.setLayout(buttonLayout);

		@SuppressWarnings("unused")
		Button findButton = createButton(buttons, "&Find",
				"Process input and find" + " the matching audio file",
				new MySelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						// TODO graphical output
						System.out.println("Data directory path:         "
								+ dataDirField.getText());
						System.out.println("Partial lyrics:         "
								+ lyricsField.getText());
						System.out.println("Artist:      "
								+ artistField.getText());
						doFind(dataDirField.getText(), lyricsField.getText(), artistField.getText());
					}
				});

		@SuppressWarnings("unused")
		Button clearButton = createButton(buttons, "&Clear", "clear inputs",
				new MySelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						clearFields();
						lyricsField.forceFocus();
					}
				});
	}
	
	private void doFind(String dataDirPath, String lyricsExp, String artist) {

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

		shell.setText(LyricsFinder.name);
		shell.setLayout(new FillLayout());

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
