package org.yamcs.ui.yamcsmonitor;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.yamcs.protobuf.Yamcs;

/**
 * Component to be displayed when the matching processor type is selected from within the Yamcs Monitor
 */
public abstract class ProcessorWidget {

    protected String processorType;
    protected JTextComponent nameComponent;

    public ProcessorWidget(String processorType) {
        this.processorType = processorType;
    }

    void setSuggestedNameComponent(JTextComponent nameComponent) {
        this.nameComponent = nameComponent;
    }

    public abstract JComponent createConfigurationPanel();

    /**
     * Invoked when the channel panel is brought to the front
     */
    public abstract void activate();

    /**
     * Returns the replay request forwarded to createProcessor()
     */
    public abstract Yamcs.ReplayRequest getReplayRequest();

    /**
     * Whether this channel type needs an archive browser
     */
    public boolean requiresArchiveBrowser() {
        return false;
    }

    @Override
    public String toString() {
        return processorType;
    }
}
