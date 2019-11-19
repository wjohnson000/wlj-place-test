package std.wlj.grep;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class GrepUI extends JFrame implements Observer {

	/** Class file 'serialVersionUID' */
	private static final long serialVersionUID = -6117169134905413504L;

	/** Save the most recent directory ... */
	private static String saveDirectoryValue = "";


	private JButton     openDirBtn;
	private JButton     startBtn;
	private JButton     stopBtn;
	private JCheckBox   filterOnCB;
	private JCheckBox   cFlag;  // limit number of characters around match
	private JCheckBox   fFlag;  // if TRUE, do a match on the filename
	private JCheckBox   hFlag;  // if TRUE, ignore hidden files [start with "."]
	private JCheckBox   iFlag;  // if TRUE, ignore case of the match
	private JCheckBox   jFlag;  // if TRUE, search inside of ZIP and JAR archives
	private JCheckBox   lFlag;  // if TRUE, list filename of matches, not match details
	private JCheckBox   mFlag;  // if TRUE, trim the match line of leading spaces
	private JCheckBox   nFlag;  // if TRUE, list line number of match
	private JCheckBox   qFlag;  // if TRUE, ignore 'qa' directories
	private JCheckBox   tFlag;  // if TRUE, ignore 'target' directories
	private JCheckBox   vFlag;  // if TRUE, reverse match/non-match criteria
	private JCheckBox   xFlag;  // if TRUE, ignore 'test' directories
	private JScrollPane resultsPane;
	private JTextArea   resultsTA;
	private JTextField  directoryTF;
	private JTextField  filterTF;
	private JTextField  patternTF;
	private JTextField  cFlagTF;
	private JTextField  outputDelimTF;
	private JLabel      statusLbl;

	private Font  boldFont   = new java.awt.Font("Verdana", Font.BOLD, 11);
	private Font  monoFont   = new java.awt.Font("Lucida Sans Typewriter", Font.PLAIN, 10);
	private Color appBkColor = new Color(248, 236, 212);
	private Color resBkColor = new Color(248, 248, 248);

	private GrepEngine grepEngine = null;
	protected String prevDir = null; // Save the directory last used on a file search


	public GrepUI() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		buildUI();
	}


	/**
	 * Create all of the UI elements
	 */
	private void buildUI() {
		int row = 0;
		getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().setBackground(appBkColor);
		setTitle("Linux GREP-like Search Functionality ...");
		setLocation(200, 160);

		// ====================================================================
		// Set the primary label
		// ====================================================================
		JLabel jLabel01 = new JLabel("Search a file or directory for a string of characters");
		jLabel01.setFont(boldFont);
		
		getContentPane().add(
				jLabel01, new GridBagConstraints(0, row, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(12, 6, 0, 2), 160, 0));
		row++;

		// ====================================================================
		// Add the line for the file/directory line
		// ====================================================================
		JLabel jLabel03 =  new JLabel("Directory: ");
		jLabel03.setFont(boldFont);
		getContentPane().add(
				jLabel03, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 6, 0, 2), 0, 0));

		directoryTF = new JTextField(32);
		directoryTF.setText(saveDirectoryValue);
		directoryTF.setFont(monoFont);
		getContentPane().add(
				directoryTF, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(2, 6, 0, 2), 0, 0));

		openDirBtn = new JButton("Browse ...");
		openDirBtn.setFont(boldFont);
		openDirBtn.setMargin(new Insets(0, 10, 0, 10));
		openDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileDir = getFilePathOpen("Select file or directory");
				if (fileDir != null) {
					directoryTF.setText(fileDir);
					saveDirectoryValue = fileDir;
				}
			}
		});
		getContentPane().add(
				openDirBtn, new GridBagConstraints(2, row, GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(2, 6, 0, 16), 0, 0));
		row++;

		// ====================================================================
		// Add the line for the match [pattern] criteria
		// ====================================================================
		JLabel jLabel02 =  new JLabel("Pattern: ");
		jLabel02.setFont(boldFont);
		getContentPane().add(
				jLabel02, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 6, 0, 2), 0, 0));
		patternTF = new JTextField(32);
		patternTF.setFont(monoFont);
		getContentPane().add(
				patternTF, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(2, 6, 0, 2), 0, 0));
		row++;

		// ====================================================================
		// Add the line for the file-name filter
		// ====================================================================
		JLabel jLabel04 =  new JLabel("Filter: ");
		jLabel04.setFont(boldFont);
		getContentPane().add(
				jLabel04, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 6, 0, 2), 0, 0));

		filterTF = new JTextField(32);
		filterTF.setFont(monoFont);
		filterTF.setText("*.java,*.jsp,*.htm,*.html,*.properties,*.txt,*.xml,*.js");
		getContentPane().add(
				filterTF, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(2, 6, 0, 2), 0, 0));

		filterOnCB = new JCheckBox("Enable filter");
		filterOnCB.setSelected(true);
		filterOnCB.setBackground(appBkColor);
		filterOnCB.setToolTipText("enable filtering based on file name extensions");
		filterOnCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleFilterOnCB();
			}			
		});
		getContentPane().add(
				filterOnCB,new GridBagConstraints(2, row, GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(2, 6, 0, 16), 0, 0));
		row++;

		// ====================================================================
		// Add five CheckBox-es for the standard options
		// Add additional CheckBox-es for WLJ-specific options
		// ====================================================================
		JPanel cbPanelTop = new JPanel();
		cbPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 0));
		cbPanelTop.setBackground(appBkColor);

		JPanel cbPanelBot = new JPanel();
		cbPanelBot.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 0));
		cbPanelBot.setBackground(appBkColor);

		iFlag = new JCheckBox("-i");
		iFlag.setBackground(appBkColor);
		iFlag.setToolTipText("ignore case");
		lFlag = new JCheckBox("-l");
		lFlag.setBackground(appBkColor);
		lFlag.setToolTipText("list file name only");
		nFlag = new JCheckBox("-n");
		nFlag.setBackground(appBkColor);
		nFlag.setToolTipText("show line number in match");
		nFlag.setSelected(true);
		vFlag = new JCheckBox("-v");
		vFlag.setBackground(appBkColor);
		vFlag.setToolTipText("invert match [show lines that DON'T match]");
		cFlag = new JCheckBox("-c");
		cFlag.setBackground(appBkColor);
		cFlag.setToolTipText("limit number of characters before and after match");
		cFlagTF = new JTextField(5);
		cFlagTF.setText("256");
		outputDelimTF = new JTextField(5);
		outputDelimTF.setText(" ");
		outputDelimTF.setToolTipText("text to separate file-name from matching text");

		mFlag = new JCheckBox("-trim");
		mFlag.setBackground(appBkColor);
		mFlag.setToolTipText("trim text data before trying match");
		hFlag = new JCheckBox("-ignore 'hidden'");
		hFlag.setBackground(appBkColor);
		hFlag.setToolTipText("skip 'hidden' files and directories");
		hFlag.setSelected(true);
		tFlag = new JCheckBox("-ignore 'target'");
		tFlag.setBackground(appBkColor);
		tFlag.setToolTipText("skip 'target' directories");
		tFlag.setSelected(true);
		xFlag = new JCheckBox("-ignore 'test'");
		xFlag.setBackground(appBkColor);
		xFlag.setToolTipText("skip 'test' directories");
		xFlag.setSelected(false);
		qFlag = new JCheckBox("-ignore 'qa'");
		qFlag.setBackground(appBkColor);
		qFlag.setToolTipText("skip 'qa' directories");
		qFlag.setSelected(true);
		jFlag = new JCheckBox("-java 'archives'");
		jFlag.setBackground(appBkColor);
		jFlag.setToolTipText("search inside java archives (.jar, .war, .ear files)");
		fFlag = new JCheckBox("-filename only");
		fFlag.setBackground(appBkColor);
		fFlag.setToolTipText("match on file name, not file contents");

		cbPanelTop.add(iFlag);
		cbPanelTop.add(lFlag);
		cbPanelTop.add(nFlag);
		cbPanelTop.add(vFlag);
		cbPanelTop.add(cFlag);
		cbPanelTop.add(cFlagTF);
		cbPanelTop.add(new JLabel("    Delim: "));
		cbPanelTop.add(outputDelimTF);

		cbPanelBot.add(mFlag);
		cbPanelBot.add(tFlag);
		cbPanelBot.add(xFlag);
		cbPanelBot.add(qFlag);
		cbPanelBot.add(hFlag);
		cbPanelBot.add(jFlag);
		cbPanelBot.add(fFlag);

		JLabel jLabel0x =  new JLabel("Options: ");
		jLabel0x.setFont(boldFont);
		getContentPane().add(
				jLabel0x, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 6, 0, 0), 0, 0));
		getContentPane().add(
				cbPanelTop, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(0, 6, 0, 0), 0, 0));
		row++;

		getContentPane().add(
				cbPanelBot, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(0, 6, 0, 0), 0, 0));
		row++;

		// ====================================================================
		// Add the line for the "start" and "stop" buttons ...
		// ====================================================================
		startBtn = new JButton("Start ...");
		startBtn.setFont(boldFont);
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDirectoryValue = directoryTF.getText();
				startAction();
			}
		});
		getContentPane().add(
				startBtn, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(12, 6, 0, 2), 0, 0));

		stopBtn = new JButton("Stop ...");
		stopBtn.setFont(boldFont);
		stopBtn.setEnabled(false);
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAction();
			}
		});
		getContentPane().add(
				stopBtn, new GridBagConstraints(1, row, GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(12, 6, 0, 2), 0, 0));
		row++;

		// ====================================================================
		// Add the text-area to the bottom
		// ====================================================================
		resultsTA = new JTextArea(24, 120);
		resultsTA.setFont(monoFont);
		resultsTA.setEditable(false);
		resultsTA.setBackground(resBkColor);
		resultsPane = new JScrollPane();
		resultsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultsPane.getViewport().add(resultsTA);
		getContentPane().add(
				resultsPane, new GridBagConstraints(0, row, GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(12, 6, 0, 2), 0, 0));
		row++;

		// ====================================================================
		// Add a status line
		// ====================================================================
		statusLbl = new JLabel("Fill in some nice values and hit the 'Start' button to start.");
		statusLbl.setFont(boldFont);
		statusLbl.setForeground(Color.BLUE);
		getContentPane().add(
				statusLbl, new GridBagConstraints(0, row, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(4, 6, 8, 2), 0, 0));		
		
		pack();
	}


	protected void handleFilterOnCB() {
		filterTF.setEnabled(filterOnCB.isSelected());
	}


//	Get the full path to a file for OPEN-ing
	protected String getFilePathOpen(String title) {
		return getFilePath(title, JFileChooser.OPEN_DIALOG, "Open");
	}


//	Get the full path to a file for SAVE-ing
	protected String getFilePathSave(String title) {
		return getFilePath(title, JFileChooser.SAVE_DIALOG, "Save");
	}
	

	/**
	 *  Get a file name (full path); by default we don't allow multiple
	 *  file name selections, and we don't want to show those nasty old
	 *  hidden file names ...
	 */
	private String getFilePath(String title, int mode, String buttonText) {
		// Find our parent frame and create the "choose" dialog
		Frame parent = this;
		JFileChooser jfcDialog = new JFileChooser();
		jfcDialog.setDialogTitle(title);

		// Set the default directory to the most recent directory used, and
		// disable hidden files and multiple selection
		if (directoryTF.getText().trim().length() > 0) {
			jfcDialog.setCurrentDirectory(new java.io.File(directoryTF.getText()));
		} else if (prevDir != null) {
			jfcDialog.setCurrentDirectory(new java.io.File(prevDir));
		}
		jfcDialog.setFileHidingEnabled(true);
		jfcDialog.setMultiSelectionEnabled(false);
		if (mode == JFileChooser.OPEN_DIALOG) {
			jfcDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}

		// Show the dialog and wait for it to complete
		int retVal = jfcDialog.showDialog(parent, buttonText);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = jfcDialog.getSelectedFile();
			prevDir = file.getParent();
			return file.getAbsolutePath();
		}
		return null;
	}

	/**
	 * Start the GREP command ... set the command options based on the
	 * setting in the UI
	 */
	private void startAction() {
		grepEngine = new GrepEngine(true);

		if (lFlag.isSelected()) {
			grepEngine.showMatch = false;
		}
		if (iFlag.isSelected()) {
			grepEngine.ignoreCase = true;
		} 
		if (nFlag.isSelected()) {
			grepEngine.showLine = true;
		}
		if (vFlag.isSelected()) {
			grepEngine.invertMatch = true;
			grepEngine.showMatch = false;
		}
		if (mFlag.isSelected()) {
			grepEngine.trimData = true;
		} 
		if (cFlag.isSelected()) {
			int maxChar = 256;
			try {
				maxChar = Integer.parseInt(cFlagTF.getText().trim());
			} catch (Exception ex) { }
			grepEngine.maxChar = maxChar;
		}
		if (outputDelimTF.getText().length() > 0) {
			grepEngine.outputDelim = outputDelimTF.getText();
		}
		if (tFlag.isSelected()) {
			grepEngine.ignoreTarget = true;
		}
		if (xFlag.isSelected()) {
			grepEngine.ignoreTest = true;
		}
		if (qFlag.isSelected()) {
			grepEngine.ignoreQA = true;
		}
		if (hFlag.isSelected()) {
			grepEngine.ignoreHidden = true;
		}
		if (jFlag.isSelected()) {
			grepEngine.searchArchive = true;
		}
		if (fFlag.isSelected()) {
			grepEngine.fileNameMatch = true;
		}

		if (filterOnCB.isSelected()  &&  filterTF.getText().length() > 0) {
			grepEngine.filter = filterTF.getText();
		}

		resultsTA.setText("");
		statusLbl.setText("Starting the search ...");
		grepEngine.addObserver(this);
		Thread grepThread = new Thread() {
			@Override
			public void run() {
				grepEngine.doSearch(directoryTF.getText(), patternTF.getText());
			}
		};
		startBtn.setEnabled(false);
		grepThread.start();
		stopBtn.setEnabled(true);
	}

	/**
	 * Start the GREP command ...
	 */
	private void stopAction() {
		if (grepEngine != null) {
			grepEngine.stopSearch();
		}
	}

	/**
	 * Show finished statements ...
	 */
	private void showStats() {
		long[] metrics = { 0, 0, 0, 0, 0 };

		if (grepEngine != null) {
			grepEngine.stopSearch();
			metrics = grepEngine.getSearchMetrics();
		}

		statusLbl.setText(
			"FINISHED.  ExecuteTime=" + metrics[0]/1000000 + "ms" +
			";   Matches=" + metrics[1] +
			";   Directories=" + metrics[2] +
			";   Files=" + metrics[3] +
			";   Filter files=" + metrics[4] + "\n");			

		grepEngine = null;
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == null) {
			showStats();
		} else if (arg instanceof File) {
			statusLbl.setText(((File)arg).getAbsolutePath());
		} else if (arg instanceof String) {
			resultsTA.append(arg.toString() + "\n");			
		}
	}
}
