package org.pdxfinder.dao;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by jmason on 21/07/2017.
 */
@NodeEntity
public class Platform {

    @GraphId
    private Long id;
    private String name;

    @Relationship(type = "ASSOCIATED_WITH", direction = Relationship.INCOMING)
    private ExternalDataSource externalDataSource;

    @Relationship(type = "ASSOCIATED_WITH")
    private Set<PlatformAssociation> platformAssociations;

    public Platform() {
    }

    public Platform(Set<PlatformAssociation> platformAssociations) {
        this.platformAssociations = platformAssociations;
    }

    public Set<PlatformAssociation> getPlatformAssociations() {
        return platformAssociations;
    }

    public void setPlatformAssociations(Set<PlatformAssociation> platformAssociations) {
        this.platformAssociations = platformAssociations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExternalDataSource getExternalDataSource() {
        return externalDataSource;
    }

    public void setExternalDataSource(ExternalDataSource externalDataSource) {
        this.externalDataSource = externalDataSource;
    }
}
