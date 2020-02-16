package org.yamcs.ui.archivebrowser;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.yamcs.ConfigurationException;
import org.yamcs.YConfiguration;
import org.yamcs.YamcsException;
import org.yamcs.api.YamcsConnectDialog;
import org.yamcs.api.YamcsConnectDialog.YamcsConnectDialogResult;
import org.yamcs.api.YamcsConnectionProperties;
import org.yamcs.api.YamcsConnector;
import org.yamcs.api.ws.ConnectionListener;
import org.yamcs.protobuf.Yamcs.ArchiveTag;
import org.yamcs.protobuf.Yamcs.IndexResult;
import org.yamcs.ui.YamcsArchiveIndexReceiver;
import org.yamcs.utils.TimeEncoding;
import org.yamcs.utils.TimeInterval;
import org.yamcs.utils.YObjectLoader;

/**
 * Standalone Archive Browser (useful for browsing the index, retrieving telemetry packets and parameters).
 * 
 * @author nm
 *
 */
public class ArchiveBrowser extends JFrame implements ArchiveIndexListener, ConnectionListener, ActionListener {
    private static final long serialVersionUID = 1L;
    ArchiveIndexReceiver indexReceiver;
    public ArchivePanel archivePanel;
    public JMenuBar menuBar;

    JMenuItem connectMenuItem;

    YamcsConnector yconnector;

    private String instance;

    private long lastErrorDialogTime = 0;

    public ArchiveBrowser(YamcsConnector yconnector, ArchiveIndexReceiver ir, boolean replayEnabled)
            throws IOException, ConfigurationException {
        super("Archive Browser");
        this.yconnector = yconnector;
        this.indexReceiver = ir;

        setIconImage(ArchivePanel.getIcon("yamcs-32x32.png").getImage());

        /*
         * MENU
         */
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        connectMenuItem = new JMenuItem();
        connectMenuItem.setText("Connect to Yamcs...");
        connectMenuItem.setMnemonic(KeyEvent.VK_C);
        connectMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(connectMenuItem);

        fileMenu.addSeparator();

        JMenuItem closeMenuItem = new JMenuItem();
        closeMenuItem.setMnemonic(KeyEvent.VK_Q);
        closeMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        closeMenuItem.setText("Quit");
        closeMenuItem.setActionCommand("exit");
        closeMenuItem.addActionListener(this);
        fileMenu.add(closeMenuItem);
        JMenu toolsMenu = getToolsMenu();
        if (toolsMenu != null) {
            menuBar.add(toolsMenu);
        }

        /*
         * BUTTONS
         */
        archivePanel = new ArchivePanel(this, replayEnabled);
        archivePanel.prefs.reloadButton.addActionListener(this);

        // While resizing, only update active item (slight performance gain)
        // When done resizing, update all
        getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                archivePanel.onWindowResizing();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                archivePanel.onWindowResized();
            }
        });

        setContentPane(archivePanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    protected JMenu getToolsMenu() throws ConfigurationException, IOException {
        if (!YConfiguration.isDefined("yamcs-ui")) {
            return null;
        }
        YConfiguration config = YConfiguration.getConfiguration("yamcs-ui");
        if (!config.containsKey("archiveBrowserTools")) {
            return null;
        }
        List<Map<String, String>> tools = config.getList("archiveBrowserTools");

        JMenu toolsMenu = new JMenu("Tools");
        for (Map<String, String> m : tools) {
            JMenuItem menuItem = new JMenuItem(m.get("name"));
            toolsMenu.add(menuItem);
            String className = m.get("class");
            YObjectLoader<JFrame> objLoader = new YObjectLoader<>();
            final JFrame f = objLoader.loadObject(className, yconnector);
            menuItem.addActionListener(e -> f.setVisible(true));
        }
        return toolsMenu;
    }

    protected void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, getTitle(), JOptionPane.PLAIN_MESSAGE);
    }

    protected void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, getTitle(), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void connecting(String url) {
        log("Connecting to " + url);
    }

    @Override
    public void connected(String url) {
        try {
            List<String> instances = yconnector.getYamcsInstances();
            if (instances != null) {
                archivePanel.setInstances(instances);
                archivePanel.connected();
                requestData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionFailed(String url, YamcsException exception) {
        archivePanel.disconnected();
        // Display a helpful dialog, but prevent multiple dialogs if all
        // connections are failing.
        if (lastErrorDialogTime < (System.currentTimeMillis() - 5000)) {
            JOptionPane.showMessageDialog(this, exception.toString(), getClass().getName(), JOptionPane.ERROR_MESSAGE);
            lastErrorDialogTime = System.currentTimeMillis();
        }
    }

    @Override
    public void disconnected() {
        archivePanel.disconnected();
    }

    @Override
    public void log(String text) {
        System.out.println(text);
    }

    public void popup(String text) {
        showMessage(text);
    }

    @Override
    public void receiveArchiveRecords(IndexResult ir) {
        archivePanel.receiveArchiveRecords(ir);
    }

    @Override
    public void receiveArchiveRecordsError(String errorMessage) {
        archivePanel.receiveArchiveRecordsError(errorMessage);
    }

    @Override
    public void receiveArchiveRecordsFinished() {
        if (indexReceiver.supportsTags()) {
            TimeInterval interval = archivePanel.getRequestedDataInterval();
            indexReceiver.getTag(instance, interval);
        } else {
            archivePanel.archiveLoadFinished();
        }
    }

    @Override
    public void receiveTagsFinished() {
        archivePanel.archiveLoadFinished();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        final String cmd = ae.getActionCommand();
        if (cmd.equalsIgnoreCase("reload")) {
            requestData();
        } else if (cmd.equalsIgnoreCase("exit")) {
            System.exit(0);
        }
    }

    private void requestData() {
        archivePanel.startReloading();
        TimeInterval interval = archivePanel.getRequestedDataInterval();
        indexReceiver.getIndex(instance, interval);
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: archive-browser.sh [-h] [url]");
        System.err.println("-h:\tShow this help text");
        System.err.println("url:\tConnect at startup to the given url");
        System.err.println("Example to yamcs archive:\n\t archive-browser.sh http://localhost:8090/");
        System.exit(1);
    }

    @Override
    public void receiveTags(final List<ArchiveTag> tagList) {
        SwingUtilities.invokeLater(() -> archivePanel.tagsAdded(tagList));
    }

    @Override
    public void tagAdded(final ArchiveTag ntag) {
        SwingUtilities.invokeLater(() -> archivePanel.tagAdded(ntag));
    }

    @Override
    public void tagRemoved(final ArchiveTag rtag) {
        SwingUtilities.invokeLater(() -> archivePanel.tagRemoved(rtag));
    }

    @Override
    public void tagChanged(final ArchiveTag oldTag, final ArchiveTag newTag) {
        SwingUtilities.invokeLater(() -> archivePanel.tagChanged(oldTag, newTag));

    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public static void main(String[] args) throws URISyntaxException, ConfigurationException, IOException {
        YamcsConnectionProperties params = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-h")) {
                printUsageAndExit();
            } else {
                if (args.length != i + 1) {
                    printUsageAndExit();
                }
                String initialUrl = args[i];
                if (initialUrl.startsWith("http://")) {
                    params = YamcsConnectionProperties.parse(initialUrl);
                } else {
                    printUsageAndExit();
                }
            }
        }
        TimeEncoding.setUp();
        final YamcsConnector yconnector = new YamcsConnector("ArchiveBrowser");
        final ArchiveIndexReceiver ir = new YamcsArchiveIndexReceiver(yconnector);
        final ArchiveBrowser archiveBrowser = new ArchiveBrowser(yconnector, ir, false);

        archiveBrowser.connectMenuItem.addActionListener(e -> {
            YamcsConnectDialogResult r = YamcsConnectDialog.showDialog(archiveBrowser, false);
            if (r.isOk()) {
                yconnector.connect(r.getConnectionProperties());
            }
        });

        ir.setIndexListener(archiveBrowser);
        yconnector.addConnectionListener(archiveBrowser);
        if (params != null) {
            yconnector.connect(params);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("shutting down");
                yconnector.disconnect();
            }
        });

        archiveBrowser.setVisible(true);
    }
}
