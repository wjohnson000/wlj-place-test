package std.wlj.grep;

import javax.swing.UIManager;


public class GrepMain {

	// Open up a simple UI interface to get the search parameters
	private static void showUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GrepUI grepUI = new GrepUI();
				grepUI.setVisible(true);
			}
		});
	}

	// Get us started by parsing in the line parameters . . .
	private static void noUI(String args[]) {
		String match = null;
		boolean start = false;
		GrepEngine engine = new GrepEngine();

		for (int i = 0; i < args.length; i++) {
			if (start) {
				String res = engine.doSearch(args[i], match);
				System.out.println(res);
			} else {
				if (args[i].equals("-l"))
					engine.showMatch = false;
				else if (args[i].equals("-i"))
					engine.ignoreCase = true;
				else if (args[i].equals("-n"))
					engine.showLine = true;
				else if (args[i].equals("-trim"))
					engine.trimData = true;
				else if (args[i].equals("-notarget"))
					engine.ignoreTarget = true;
				else if (args[i].equals("-notest"))
					engine.ignoreTest = true;
				else if (args[i].equals("-noqa"))
					engine.ignoreQA = true;
				else if (args[i].equals("-nohidden"))
					engine.ignoreHidden = true;
				else if (args[i].startsWith("-c")  &&  args[i].length() > 2)
					engine.maxChar = Integer.parseInt(args[i].substring(2));
				else if (args[i].startsWith("-f")  &&  args[i].length() > 2)
					engine.filter = args[i].substring(2);
				else {
					start = true;
					match = args[i];
				}
			}
		}

		if (!start)
			System.out.println("No pattern specified . . .");
	}


	// Get us started by parsing in the line parameters . . .
	public static void main(String args[]) {
		if (args.length == 0) {
			showUI();
		} else {
			noUI(args);
		}
	}
}
