package org.yamcs.xtceproc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.xtce.ContainerEntry;
import org.yamcs.xtce.DynamicIntegerValue;
import org.yamcs.xtce.IndirectParameterRefEntry;
import org.yamcs.xtce.Parameter;
import org.yamcs.xtce.ParameterEntry;
import org.yamcs.xtce.SequenceContainer;
import org.yamcs.xtce.SequenceEntry;
import org.yamcs.xtce.XtceDb;

/**
 * keeps track of the parameters and containers subscribed (because we only want to extract those)
 * 
 * 
 * @author nm
 *
 */
public class Subscription {
    // Maps the packet definitions to the entries we actually need from these packets
    private final Map<SequenceContainer, TreeSet<SequenceEntry>> container2EntryMap = new HashMap<>();

    // For each container list the derived containers which have to be processed also
    private final Map<SequenceContainer, HashSet<SequenceContainer>> container2InheritingContainerMap = new HashMap<>();
    Logger log = LoggerFactory.getLogger(Subscription.class);

    XtceDb xtcedb;

    Subscription(XtceDb xtcedb) {
        this.xtcedb = xtcedb;
    }

    public void addSequenceContainer(SequenceContainer seq) {
        // if there is a base container, add that one to the subscription and the parameters which have to be
        // extracted from the base in order to know if the inheritance condition applies
        if (seq.getBaseContainer() != null) {
            addContainer2InheritingContainer(seq.getBaseContainer(), seq);
            // it can be that the inheritance condition parameters are not from the baseContainer, so we need to add
            // it explicitly
            addSequenceContainer(seq.getBaseContainer());
            for (Parameter p : seq.getRestrictionCriteria().getDependentParameters()) {
                addParameter(p);
            }

        }
        // if this container is part of another containers through aggregation, then add those

        List<ContainerEntry> entries = xtcedb.getContainerEntries(seq);
        if (entries != null) {
            for (ContainerEntry ce : entries) {
                addSequenceEntry(ce);
            }
            if (seq.getSizeInBits() < 0) {
                // desperately add all parameters from this container in order for the parent to know the size
                addAll(seq);
            }
        }
    }

    /**
     * Called in the cases when seq is part of other containers through aggregation.
     * The parent container will need to know the size of this one so we add all entries of seq.
     * 
     * @param seq
     */
    public void addAll(SequenceContainer seq) {
        for (SequenceEntry se : seq.getEntryList()) {
            addContainer2Entry(seq, se);
            if (se instanceof ContainerEntry) {
                addAll(((ContainerEntry) se).getRefContainer());
            }
        }
        List<SequenceContainer> inheriting = xtcedb.getInheritingContainers(seq);
        if (inheriting != null) {
            for (SequenceContainer sc : inheriting) {
                addContainer2InheritingContainer(seq, sc);
                addAll(sc);
            }
        }
    }

    public void addSequenceEntry(SequenceEntry se) {
        boolean containerAlreadyAdded = container2EntryMap.containsKey(se.getSequenceContainer());

        addContainer2Entry(se.getSequenceContainer(), se);
        SequenceContainer sctmp = se.getSequenceContainer();
        // if this entry's location is relative to the previous one, then we have to add also that one in the list
        if (se.getReferenceLocation() == SequenceEntry.ReferenceLocationType.previousEntry) {
            if (se.getIndex() > 0) {
                addSequenceEntry(sctmp.getEntryList().get(se.getIndex() - 1));
            } else { // continue with the basecontainer if we are at the first entry
                sctmp = sctmp.getBaseContainer();
                if(sctmp!=null) {
                    addSequenceEntry(sctmp.getEntryList().get(sctmp.getEntryList().size() - 1));
                }
            }
        }
        if ((se.getRepeatEntry() != null) && (se.getRepeatEntry().getCount() instanceof DynamicIntegerValue)) {
            addParameter(((DynamicIntegerValue) se.getRepeatEntry().getCount())
                    .getParameterInstnaceRef().getParameter());
        }
        if (!containerAlreadyAdded) {
            addSequenceContainer(se.getSequenceContainer());
        }
    }

    /**
     * Add to the subscription all the entries and containers on which this parameter depends
     * 
     * @param parameter
     */
    public void addParameter(Parameter parameter) {
        List<ParameterEntry> tpips = xtcedb.getParameterEntries(parameter);
        if (tpips != null) {
            for (ParameterEntry pe : tpips) {
                addSequenceEntry(pe);
            }
        }
        
        for (String ns : parameter.getAliasSet().getNamespaces()) {
            Collection<IndirectParameterRefEntry> c = xtcedb.getIndirectParameterRefEntries(ns);
            if (c != null) {
                for(IndirectParameterRefEntry ipre: c) {
                    addParameter(ipre.getParameterRef().getParameter());
                    addSequenceEntry(ipre);
                }
            }
        }
    }

    private void addContainer2Entry(SequenceContainer sc, SequenceEntry se) {
        TreeSet<SequenceEntry> ts = container2EntryMap.computeIfAbsent(sc, k-> new TreeSet<SequenceEntry>());
        ts.add(se);
    }

    private void addContainer2InheritingContainer(SequenceContainer container, SequenceContainer inheritedContainer) {
        HashSet<SequenceContainer> hs = container2InheritingContainerMap.computeIfAbsent(container, k -> new HashSet<>());
        hs.add(inheritedContainer);
    }

    public SortedSet<SequenceEntry> getEntries(SequenceContainer container) {
        return container2EntryMap.get(container);
    }

    public Set<SequenceContainer> getInheritingContainers(SequenceContainer container) {
        return container2InheritingContainerMap.get(container);
    }

    /**
     * Get the set of all containers subscribed
     * 
     * @return set of containers subscribed
     */
    public Collection<SequenceContainer> getContainers() {
        Set<SequenceContainer> r = new HashSet<SequenceContainer>();
        r.addAll(container2InheritingContainerMap.keySet());
        for (HashSet<SequenceContainer> hs : container2InheritingContainerMap.values()) {
            r.addAll(hs);
        }
        r.addAll(container2EntryMap.keySet());
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current list of parameter subscribed:\n");
        for (SequenceContainer sc : container2EntryMap.keySet()) {
            sb.append(sc);
            sb.append(" with entries:\n");
            for (SequenceEntry se : container2EntryMap.get(sc)) {
                sb.append("\t").append(se).append("\n");
            }
        }
        sb.append("-----------------------------------\n");
        sb.append("Container inheritance dependency\n");
        for (SequenceContainer sc : container2InheritingContainerMap.keySet()) {
            sb.append(sc.getName());
            sb.append("-->");
            for (SequenceContainer sc1 : container2InheritingContainerMap.get(sc)) {
                sb.append(sc1.getName() + " ");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }

}
