/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import std.wlj.hhs.admin.ui.model.CollectionTreeModel;
import std.wlj.hhs.admin.ui.model.FolderNode;
import std.wlj.hhs.admin.ui.model.FolderType;

/**
 * @author wjohnson000
 *
 */
public class AdminUI extends JFrame {

    private static final long serialVersionUID = -1234567890L;

    S3Helper s3Helper = new S3Helper();

    private JMenuBar  mainMenuBar = new JMenuBar();
    private JMenu     fileMenu = new JMenu("File");
    private JMenuItem quitItem = new JMenuItem("Quit");
    private JMenu     editMenu = new JMenu("Edit");
    private JMenuItem readItem = new JMenuItem("Read S3");
    private JMenuItem fontItem = new JMenuItem("Font");
    private JMenuItem downItem = new JMenuItem("Download");

    private JPanel contentPane;
    private JTree  s3Tree;
    private CollectionTreeModel treeModel;
    private JLabel statusBar = new JLabel();

    private Font defaultFont = new Font("Monospaced", Font.PLAIN, 18);

    public AdminUI() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.enableInputMethods(false);
        try {
            setupS3Tree();

            swingInit();
            buildMenus();
            setFontAll(defaultFont);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void swingInit() {
        // Main pane stuff
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        this.setSize(new Dimension(1120, 720));
        this.setTitle("Homelands Admin UI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Status Bar
        statusBar.setText(">> Status <<");

        // Content Pane
        JScrollPane scrollPane = new JScrollPane(s3Tree);
        contentPane.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                        GridBagConstraints.WEST, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));
        contentPane.add(statusBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                                       GridBagConstraints.WEST, GridBagConstraints.NONE,
                                       new Insets(0, 0, 0, 0), 0, 0));
    }

    protected void buildMenus() {
        this.setJMenuBar(mainMenuBar);

        mainMenuBar.add(fileMenu);
        mainMenuBar.add(editMenu);
        fileMenu.add(quitItem);
        fileMenu.add(readItem);
        editMenu.add(fontItem);
        editMenu.add(downItem);

        quitItem.addActionListener(ae -> quitApp());
        fontItem.addActionListener(ae -> chooseFont());
        downItem.addActionListener(ae -> downloadFile());
    }

    protected void setupS3Tree() {
        s3Helper = new S3Helper();
//        List<FolderNode> s3Nodes = s3Helper.getDetails();
        List<FolderNode> s3Nodes = s3Helper.getDetailsSaved();
        treeModel = new CollectionTreeModel(s3Nodes);
        s3Tree = new JTree(treeModel);
    }

    protected void quitApp() {
        dispose();
        System.exit(0);
    }

    protected void chooseFont() {
        setFontAll(FontChooser.GetFont(this));
    }

    protected void downloadFile() {
        TreePath[] paths = s3Tree.getSelectionPaths();
        for (TreePath path : paths) {
            if (path.getLastPathComponent() instanceof FolderNode) {
                FolderNode folder = (FolderNode)path.getLastPathComponent();
                if (folder.getType() == FolderType.FILE) {
                    File file = getFileToSave(folder.getId());
                    if (file != null) {
                        try {
                            byte[] contents = s3Helper.readFile(folder.getPath());
                            if (contents == null) {
                                System.out.println("No File Contents!!");
                            } else {
                                FileUtils.writeByteArrayToFile(file, contents);
                            }
                        } catch(Exception ex) {
                            System.out.println("Unable to save file!");
                        }
                    }
                }
            }
        }
    }

    protected void setFontAll(Font newFont) {
        if (newFont != null) {
            mainMenuBar.setFont(newFont);
            fileMenu.setFont(newFont);
            quitItem.setFont(newFont);
            readItem.setFont(newFont);
            editMenu.setFont(newFont);
            fontItem.setFont(newFont);
            downItem.setFont(newFont);
            s3Tree.setFont(newFont);
            statusBar.setFont(newFont);
        }
    }

    protected File getFileToSave(String name) {
        JFileChooser jfcDialog = new JFileChooser();
        jfcDialog.setDialogTitle("Save file ... ");
        jfcDialog.setCurrentDirectory(new File("C:/temp/"));
        jfcDialog.setSelectedFile(new File("C:/temp/" + name));
        jfcDialog.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        jfcDialog.setFileHidingEnabled(true);
        jfcDialog.setMultiSelectionEnabled(false);

        int retVal = jfcDialog.showDialog(this, "Save");
        if (retVal == JFileChooser.APPROVE_OPTION) {
            return jfcDialog.getSelectedFile();
        } else {
            return null;
        }
    }
}
