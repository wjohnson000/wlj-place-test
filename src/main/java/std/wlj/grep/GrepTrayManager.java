package std.wlj.grep;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class GrepTrayManager {

	// Default constructor ...
	public GrepTrayManager() {

	}


	// Set this up in a tray manager ...
	private void startTrayManager() {
		if (SystemTray.isSupported()) {
			PopupMenu popup = new PopupMenu();

			MenuItem openItem = new MenuItem("open");
			popup.add(openItem);
			openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doOpenAction();
				}				
			});

			MenuItem exitItem = new MenuItem("exit");
			popup.add(exitItem);
			exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doExitAction();
				}				
			});

			Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("search_16.png"));

			TrayIcon trayIcon = new TrayIcon(image, "Grep", popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("Linux-like GREP app");
			trayIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doOpenAction();
				}
			});

			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (Exception ex) { }
		}
	}


	// "Open" action ... open a new Grep UI
	private void doOpenAction() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GrepUI grepUI = new GrepUI();
				grepUI.setVisible(true);
			}
		});
	}


	// "Exit" action ... exit everything
	private void doExitAction() {
		System.exit(0);
	}


	// See if another instance of this class is already running ...
	// There is no easy way in Java to get the PID of the current
	// JVM, so we do something ugly.  We run the "jps" command, which
	// lists all JVMs, including this new one.  If there is only one
	// JVM running the "GrepTrayManager" application, we know it's
	// the current one, and we can let it proceed.  If there are
	// two [or more] JVM instances running the "GrepTrayManager"
	// application, we return true.
	private static boolean isAlreadyRunning() {
		int count = 0;
		BufferedReader rbuf = null;

		try {
			String line;
			String jvmPath = System.getProperty("java.home");
			if (jvmPath != null) {
				if (jvmPath.endsWith("\\jre")) {
					jvmPath = jvmPath.substring(0, jvmPath.length()-4);
				}
				Process proc = Runtime.getRuntime().exec("jps", null, new File(jvmPath));
				rbuf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((line = rbuf.readLine()) != null) {
					System.out.println(">>" + line);
					if (line.indexOf("GrepTrayManager") >= 0) {
						count++;
					}
				}
			}
		} catch (IOException ioex) {
			count = 0;
			System.out.println("IOEX: " + ioex);
		} finally {
			if (rbuf != null) try { rbuf.close(); } catch (Exception ex) { }			
		}

		return (count > 1);
	}


	// Get this silly thing a-goin' ... but only if we determine that we
	// don't already have an instance going.
	public static void main(String[] args) {
		System.out.println("Here ...");
		if (! isAlreadyRunning()) {
			GrepTrayManager gtmMe = new GrepTrayManager();
			gtmMe.startTrayManager();			
		}
	}
}
