package org.yamcs.ui.archivebrowser;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.yamcs.protobuf.Yamcs.ArchiveRecord;
import org.yamcs.protobuf.Yamcs.ArchiveTag;
import org.yamcs.protobuf.Yamcs.IndexResult;
import org.yamcs.ui.UiColors;
import org.yamcs.utils.TimeInterval;

/**
 * Main panel of the ArchiveBrowser
 * 
 * @author nm
 *
 */
public class ArchivePanel extends JPanel implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    ProgressMonitor progressMonitor;

    ArchiveBrowser archiveBrowser;
    JLabel totalRangeLabel;
    JLabel statusInfoLabel;
    JLabel instanceLabel;

    private LinkedHashMap<String, NavigatorItem> itemsByName = new LinkedHashMap<>();

    SideNavigator sideNavigator;
    JToolBar archiveToolbar;
    protected PrefsToolbar prefs;

    private JPanel insetPanel; // Contains switchable navigator insets (depends on open item)
    private JPanel navigatorItemPanel; // Contains switchable items
    private NavigatorItem activeItem; // Currently opened item from SideNav
    public ReplayPanel replayPanel;

    int loadCount, recCount;
    boolean passiveUpdate = false;

    private TimeInterval receivedDataInterval = new TimeInterval();

    volatile boolean lowOnMemoryReported = false;

    // used to check for out of memory errors that may happen when receiving too many archive records
    MemoryPoolMXBean heapMemoryPoolBean = null;

    public ArchivePanel(ArchiveBrowser archiveBrowser, boolean replayEnabled) {
        super(new BorderLayout());
        this.archiveBrowser = archiveBrowser;

        /*
         * Upper fixed content
         */
        Box fixedTop = Box.createVerticalBox();
        fixedTop.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UiColors.BORDER_COLOR));
        prefs = new PrefsToolbar();
        prefs.setAlignmentX(Component.LEFT_ALIGNMENT);
        fixedTop.add(prefs);

        archiveToolbar = new JToolBar();
        archiveToolbar.setFloatable(false);
        archiveToolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        fixedTop.add(archiveToolbar);

        //
        // transport control panel (only enabled when a HRDP data channel is selected)
        //
        if (replayEnabled) {
            replayPanel = new ReplayPanel();
            replayPanel
                    .setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Replay Control"));
            replayPanel.setToolTipText("Doubleclick between the start/stop locators to reposition the replay.");
            replayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            fixedTop.add(replayPanel);
        }

        // This is a bit clumsy right now with the inner classes, but will make more
        // sense once we add custom components to different DataViewers.
        DataViewer allViewer = new DataViewer(archiveBrowser.yconnector, archiveBrowser.indexReceiver, this,
                replayEnabled) {
            @Override
            public String getLabelName() {
                return "Archive";
            }

            @Override
            public JComponent createContentPanel() {
                JComponent component = super.createContentPanel();
                addIndex("completeness", "completeness index");
                addIndex("tm", "tm histogram", 1000);
                addIndex("pp", "pp histogram", 1000);
                addIndex("cmdhist", "cmdhist histogram", 1000);
                addVerticalGlue();
                return component;
            }
        };
        itemsByName.put(allViewer.getLabelName(), allViewer);

        DataViewer completenessViewer = new DataViewer(archiveBrowser.yconnector, archiveBrowser.indexReceiver, this,
                false) {
            @Override
            public String getLabelName() {
                return "Telemetry Completeness";
            }

            @Override
            public int getIndent() {
                return 1;
            }

            @Override
            public JComponent createContentPanel() {
                JComponent component = super.createContentPanel();
                addIndex("completeness", "completeness index");
                addVerticalGlue();
                return component;
            }
        };
        itemsByName.put(completenessViewer.getLabelName(), completenessViewer);

        DataViewer tmViewer = new DataViewer(archiveBrowser.yconnector, archiveBrowser.indexReceiver, this,
                replayEnabled) {
            @Override
            public String getLabelName() {
                return "Telemetry";
            }

            @Override
            public int getIndent() {
                return 1;
            }

            @Override
            public JComponent createContentPanel() {
                JComponent component = super.createContentPanel();
                addIndex("tm", "tm histogram", 1000);
                addVerticalGlue();
                return component;
            }
        };
        itemsByName.put(tmViewer.getLabelName(), tmViewer);

        DataViewer ppViewer = new DataViewer(archiveBrowser.yconnector, archiveBrowser.indexReceiver, this,
                replayEnabled) {
            @Override
            public String getLabelName() {
                return "Processed Parameters";
            }

            @Override
            public int getIndent() {
                return 1;
            }

            @Override
            public JComponent createContentPanel() {
                JComponent component = super.createContentPanel();
                addIndex("pp", "pp histogram", 1000);
                addVerticalGlue();
                return component;
            }
        };
        itemsByName.put(ppViewer.getLabelName(), ppViewer);

        DataViewer cmdViewer = new DataViewer(archiveBrowser.yconnector, archiveBrowser.indexReceiver, this, false) {
            @Override
            public String getLabelName() {
                return "Command History";
            }

            @Override
            public int getIndent() {
                return 1;
            }

            @Override
            public JComponent createContentPanel() {
                JComponent component = super.createContentPanel();
                addIndex("cmdhist", "cmdhist histogram", 1000);
                addVerticalGlue();
                return component;
            }
        };
        itemsByName.put(cmdViewer.getLabelName(), cmdViewer);

        add(fixedTop, BorderLayout.NORTH);
        add(createStatusBar(), BorderLayout.SOUTH);

        sideNavigator = new SideNavigator(this);
        add(sideNavigator, BorderLayout.WEST);

        navigatorItemPanel = new JPanel(new CardLayout());
        add(navigatorItemPanel, BorderLayout.CENTER);

        insetPanel = new JPanel(new CardLayout());
        insetPanel.setVisible(false);
        sideNavigator.add(insetPanel, BorderLayout.SOUTH);

        for (Map.Entry<String, NavigatorItem> entry : itemsByName.entrySet()) {
            String name = entry.getKey();
            NavigatorItem navigatorItem = entry.getValue();
            navigatorItemPanel.add(navigatorItem.getContentPanel(), name);
            sideNavigator.addItem(name, navigatorItem.getIndent(), navigatorItem);
            JComponent navInset = navigatorItem.getNavigatorInset();
            if (navInset != null) {
                insetPanel.add(navInset, name);
            }
        }

        openItem("Archive");

        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP && pool.isCollectionUsageThresholdSupported()) {
                heapMemoryPoolBean = pool;
                heapMemoryPoolBean
                        .setCollectionUsageThreshold((int) Math.floor(heapMemoryPoolBean.getUsage().getMax() * 0.95));
            }
        }

        // Catch mouse events globally, to deal more easily with events on child components
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> { // EDT
            if (activeItem instanceof DataViewer) {
                DataView dataView = ((DataViewer) activeItem).getDataView();
                if (!(event.getSource() instanceof JScrollBar)
                        && !(event.getSource() instanceof TagTimeline)
                        && SwingUtilities.isDescendingFrom((Component) event.getSource(), dataView)) {
                    MouseEvent me = SwingUtilities.convertMouseEvent((Component) event.getSource(), (MouseEvent) event,
                            dataView.indexPanel);
                    if (event.getID() == MouseEvent.MOUSE_DRAGGED) {
                        dataView.doMouseDragged(me);
                    } else if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                        dataView.doMousePressed(me);
                    } else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
                        dataView.doMouseReleased(me);
                    } else if (event.getID() == MouseEvent.MOUSE_MOVED) {
                        dataView.doMouseMoved(me);
                    } else if (event.getID() == MouseEvent.MOUSE_EXITED) {
                        dataView.doMouseExited(me);
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void openItem(String name) {
        NavigatorItem item = getItemByName(name);
        fireIntentionToSwitchActiveItem(item);
    }

    public NavigatorItem getItemByName(String name) {
        return itemsByName.get(name);
    }

    void fireIntentionToSwitchActiveItem(NavigatorItem sourceItem) {
        if (activeItem == sourceItem) {
            return;
        }
        sideNavigator.updateActiveItem(sourceItem); // UI-only

        CardLayout ncl = (CardLayout) navigatorItemPanel.getLayout();
        ncl.show(navigatorItemPanel, sourceItem.getLabelName());

        if (sourceItem.getNavigatorInset() != null) {
            CardLayout icl = (CardLayout) insetPanel.getLayout();
            icl.show(insetPanel, sourceItem.getLabelName());
            insetPanel.setVisible(true);
        } else {
            insetPanel.setVisible(false);
        }

        if (activeItem != null) {
            activeItem.onClose();
        }
        sourceItem.onOpen();
        activeItem = sourceItem;
    }

    private Box createStatusBar() {
        Box bar = Box.createHorizontalBox();

        Border outsideBorder = BorderFactory.createMatteBorder(2, 0, 0, 0, UiColors.BORDER_COLOR);
        Border insideBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        bar.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));

        bar.add(Box.createHorizontalGlue());

        bar.add(createLabelForStatusBar(" Instance: ")); // Front space serves as left padding
        instanceLabel = createLabelForStatusBar(null);
        bar.add(instanceLabel);

        bar.add(createLabelForStatusBar(", Data Range: "));
        totalRangeLabel = createLabelForStatusBar(null);
        bar.add(totalRangeLabel);

        bar.add(Box.createHorizontalGlue());

        statusInfoLabel = createLabelForStatusBar(null);
        bar.add(statusInfoLabel);
        return bar;
    }

    private JLabel createLabelForStatusBar(String text) {
        JLabel lbl = new JLabel();
        if (text != null) {
            lbl.setText(text);
        }
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, lbl.getFont().getSize2D() - 2));
        return lbl;
    }

    void updateStatusBar() {
        passiveUpdate = true;

        if (loadCount == 0) {
            statusInfoLabel.setText("(no data loaded) ");
        } else {
            statusInfoLabel.setText("Loading Data ... " + loadCount + " ");
            statusInfoLabel.repaint();
        }

        totalRangeLabel.setText(receivedDataInterval.toStringEncoded());
        totalRangeLabel.repaint();

        passiveUpdate = false;
    }

    public synchronized void startReloading() {
        recCount = 0;
        archiveBrowser.setInstance(prefs.getInstance());

        setBusyPointer();
        SwingUtilities.invokeLater(() -> {
            prefs.reloadButton.setEnabled(false);
            instanceLabel.setText(archiveBrowser.getInstance());
        });

        for (NavigatorItem item : itemsByName.values()) {
            item.startReloading();
        }

        if (lowOnMemoryReported) {
            System.gc();
            lowOnMemoryReported = false;
        }
        receivedDataInterval = new TimeInterval();
    }

    public static ImageIcon getIcon(String imagename) {
        return new ImageIcon(ArchivePanel.class.getResource("/org/yamcs/images/" + imagename));
    }

    static protected void debugLog(String s) {
        System.out.println(s);
    }

    static protected void debugLogComponent(String name, JComponent c) {
        Insets in = c.getInsets();
        debugLog("component " + name + ": "
                + "min(" + c.getMinimumSize().width + "," + c.getMinimumSize().height + ") "
                + "pref(" + c.getPreferredSize().width + "," + c.getPreferredSize().height + ") "
                + "max(" + c.getMaximumSize().width + "," + c.getMaximumSize().height + ") "
                + "size(" + c.getSize().width + "," + c.getSize().height + ") "
                + "insets(" + in.top + "," + in.left + "," + in.bottom + "," + in.right + ")");
    }

    void playOrStopPressed() {
        // to be reimplemented by subclass ArchiveReplay in YamcsMonitor
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        debugLog(e.getPropertyName() + "/" + e.getOldValue() + "/" + e.getNewValue());
    }

    /**
     * Called when the connection to yamcs is (re)established, (re)populates the list of hrdp instances
     * 
     * @param archiveInstances
     */
    public void setInstances(final List<String> archiveInstances) {
        prefs.setInstances(archiveInstances);
    }

    static class IndexChunkSpec implements Comparable<IndexChunkSpec> {
        long startInstant, stopInstant;
        int tmcount;
        String info;

        IndexChunkSpec(long start, long stop, int tmcount, String info) {
            this.startInstant = start;
            this.stopInstant = stop;
            this.tmcount = tmcount;
            this.info = info;
        }

        /**
         * 
         * @return frequency in Hz
         */
        float getFrequency() {
            float freq = (float) (tmcount - 1) / ((stopInstant - startInstant) / 1000.0f);
            freq = Math.round(freq * 1000) / 1000.0f;
            return freq;
        }

        @Override
        public int compareTo(IndexChunkSpec a) {
            return Long.signum(startInstant - a.startInstant);
        }

        // merge two records if close enough to eachother
        public boolean merge(IndexChunkSpec t, long mergeTime) {
            boolean merge = false;
            if (tmcount == 1) {
                if (t.startInstant - stopInstant < mergeTime) {
                    merge = true;
                }
            } else {
                float dist = (stopInstant - startInstant) / ((float) (tmcount - 1));
                if (t.startInstant - stopInstant < dist + mergeTime) {
                    merge = true;
                }
            }
            if (merge) {
                stopInstant = t.stopInstant;
                tmcount += t.tmcount;
            }
            return merge;
        }

        @Override
        public String toString() {
            return "start: " + startInstant + " stop: " + stopInstant + " count:" + tmcount;
        }
    }

    public void connected() {
        prefs.reloadButton.setEnabled(true);
    }

    public void disconnected() {
        prefs.reloadButton.setEnabled(false);
    }

    public void setBusyPointer() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setNormalPointer() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public synchronized TimeInterval getRequestedDataInterval() {
        return prefs.getInterval();
    }

    public synchronized void receiveArchiveRecords(IndexResult ir) {
        if ((heapMemoryPoolBean != null) && (heapMemoryPoolBean.isCollectionUsageThresholdExceeded())) {
            if (!lowOnMemoryReported) {
                lowOnMemoryReported = true;
                receiveArchiveRecordsError(
                        "The memory is almost exhausted, ignoring received Archive Records. Consider increasing the maximum heap size -Xmx parameter");
            }
            return;
        }

        for (NavigatorItem navigatorItem : itemsByName.values()) {
            navigatorItem.receiveArchiveRecords(ir);
        }

        long start, stop;
        for (ArchiveRecord r : ir.getRecordsList()) {
            // debugLog(r.packet+"\t"+r.num+"\t"+new Date(r.first)+"\t"+new Date(r.last));
            start = r.getFirst();
            stop = r.getLast();

            if ((!receivedDataInterval.hasStart()) || (start < receivedDataInterval.getStart())) {
                receivedDataInterval.setStart(start);
            }
            if ((!receivedDataInterval.hasEnd()) || (stop > receivedDataInterval.getEnd())) {
                receivedDataInterval.setEnd(stop);
            }

            recCount++;
            loadCount++;
            updateStatusBar();
        }
    }

    public synchronized void receiveArchiveRecordsError(final String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            for (NavigatorItem navigatorItem : itemsByName.values()) {
                navigatorItem.receiveArchiveRecordsError(errorMessage);
            }
            JOptionPane.showMessageDialog(ArchivePanel.this, "Error when receiving archive records: " + errorMessage,
                    "error receiving archive records", JOptionPane.ERROR_MESSAGE);
            prefs.reloadButton.setEnabled(true);
            setNormalPointer();
        });
    }

    void seekReplay(long newPosition) {
        replayPanel.seekReplay(newPosition);
    }

    public synchronized void archiveLoadFinished() {
        loadCount = 0;
        if (receivedDataInterval.hasStart() && receivedDataInterval.hasEnd()) {
            for (NavigatorItem item : itemsByName.values()) {
                item.archiveLoadFinished();
            }
            prefs.savePreferences();
        }

        SwingUtilities.invokeLater(() -> {
            statusInfoLabel.setText("");
            prefs.reloadButton.setEnabled(true);
            setNormalPointer();
        });
    }

    public void tagAdded(ArchiveTag ntag) {
        for (NavigatorItem navigatorItem : itemsByName.values()) {
            navigatorItem.tagAdded(ntag);
        }
    }

    public void tagRemoved(ArchiveTag rtag) {
        for (NavigatorItem navigatorItem : itemsByName.values()) {
            navigatorItem.tagRemoved(rtag);
        }
    }

    public void tagChanged(ArchiveTag oldTag, ArchiveTag newTag) {
        for (NavigatorItem navigatorItem : itemsByName.values()) {
            navigatorItem.tagChanged(oldTag, newTag);
        }
    }

    public void tagsAdded(List<ArchiveTag> tagList) {
        for (NavigatorItem navigatorItem : itemsByName.values()) {
            navigatorItem.receiveTags(tagList);
        }
    }

    // TODO only used by selector. Rework maybe in custom replay launcher
    public Selection getSelection() {
        DataViewer dataViewer = (DataViewer) activeItem;
        return dataViewer.getDataView().getSelection();
    }

    // TODO only used by selector. Rework maybe in custom replay launcher
    public List<String> getSelectedPackets(String tableName) {
        DataViewer dataViewer = (DataViewer) activeItem;
        if (dataViewer.getDataView().indexBoxes.containsKey(tableName)) {
            return dataViewer.getDataView().getSelectedPackets(tableName);
        }
        return Collections.emptyList();
    }

    public void onWindowResizing() {
        activeItem.windowResized();
    }

    public void onWindowResized() {
        for (NavigatorItem navigatorItem : itemsByName.values()) {
            navigatorItem.windowResized();
        }
    }

    public synchronized TimeInterval getReceivedDataInterval() {
        return receivedDataInterval;
    }
}
