package com.github.jhpoelen.fbob;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ValueFactoryFishbaseEstimatePatchTest {

    @Test
    public void unavailableTable() {
        Group group = createTestGroup();
        ValueFactoryFishbaseCache factory = factoryPatch(group);
        assertThat(factory.groupValueFor("plankton.size.max.plk", group), Is.is(nullValue()));
    }

    @Test
    public void predPreyRatio() {
        Group group = createTestGroup();

        ValueFactoryFishbaseCache factory = factoryPatch(group);

        assertThat(factory.groupValueFor("predation.predPrey.sizeRatio.max.sp", group), Is.is(not(nullValue())));
        assertThat(factory.groupValueFor("predation.predPrey.sizeRatio.min.sp", group), Is.is(not(nullValue())));

        ValueFactoryFishbaseCache factoryUnpatched = new ValueFactoryFishbaseCache();
        factoryUnpatched.setGroups(Collections.singletonList(group));

        assertThat(factoryUnpatched.groupValueFor("predation.predPrey.sizeRatio.max.sp", group), Is.is(nullValue()));
        assertThat(factoryUnpatched.groupValueFor("predation.predPrey.sizeRatio.min.sp", group), Is.is(nullValue()));
    }

    private ValueFactoryFishbaseCache factoryPatch(Group group) {
        ValueFactoryFishbaseCache factory = new ValueFactoryFishbaseCache("v0.2.1-patch2");
        factory.setGroups(Collections.singletonList(group));
        return factory;
    }

    private Group createTestGroup() {
        Group group = new Group("someGroupName");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Collections.singletonList(kingMackerel));
        return group;
    }
}
