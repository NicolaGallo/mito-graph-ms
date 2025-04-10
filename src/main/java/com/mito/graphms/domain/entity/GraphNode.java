package com.mito.graphms.domain.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Node("ITEM")
public class GraphNode implements Serializable {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @Property("cbdb_id")
    private String cbdbId;

    @Property("name")
    private String name;

    @Property("itemType")
    private String itemType;

    @Property("importance")
    private String importance;

    @Property("status")
    private String status;

    @Property("isLink")
    private boolean isLink;

    @Property("loc_id")
    private String locId;

    @Property("loc_Lon")
    private Double locationLon;

    @Property("loc_Lat")
    private Double locationLat;

    @Property("itemIcon")
    private String itemIcon;

    @Property("Status")
    private String statusDetail;

    @Property("numberOfIncidents")
    private Integer numberOfIncidents;

    @Property("numberOfEvents")
    private Integer numberOfEvents;

    @Property("numberOfPlanned")
    private Integer numberOfPlanned;
    
    @Relationship(type = "RELATES_TO", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties("sourceNode")
    private Set<GraphRelationship> outgoingRelations = new HashSet<>();
    
    @Relationship(type = "RELATES_TO", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties("targetNode")
    private Set<GraphRelationship> incomingRelations = new HashSet<>();

    // Constructors
    public GraphNode() {}

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCbdbId() {
        return cbdbId;
    }

    public void setCbdbId(String cbdbId) {
        this.cbdbId = cbdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
        // Automatically update isLink when itemType is set
        this.isLink = itemType != null && itemType.contains(":LINK:");
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLink() {
        return isLink;
    }

    public void setLink(boolean link) {
        isLink = link;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public Double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(Double locationLon) {
        this.locationLon = locationLon;
    }

    public Double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public Integer getNumberOfIncidents() {
        return numberOfIncidents;
    }

    public void setNumberOfIncidents(Integer numberOfIncidents) {
        this.numberOfIncidents = numberOfIncidents;
    }

    public Integer getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(Integer numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    public Integer getNumberOfPlanned() {
        return numberOfPlanned;
    }

    public void setNumberOfPlanned(Integer numberOfPlanned) {
        this.numberOfPlanned = numberOfPlanned;
    }
    
    public Set<GraphRelationship> getOutgoingRelations() {
        return outgoingRelations;
    }

    public void setOutgoingRelations(Set<GraphRelationship> outgoingRelations) {
        this.outgoingRelations = outgoingRelations;
    }

    public Set<GraphRelationship> getIncomingRelations() {
        return incomingRelations;
    }

    public void setIncomingRelations(Set<GraphRelationship> incomingRelations) {
        this.incomingRelations = incomingRelations;
    }
    
    public void addOutgoingRelation(GraphRelationship relationship) {
        this.outgoingRelations.add(relationship);
    }
    
    public void addIncomingRelation(GraphRelationship relationship) {
        this.incomingRelations.add(relationship);
    }

    // Equality methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return Objects.equals(id, graphNode.id) || Objects.equals(cbdbId, graphNode.cbdbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cbdbId);
    }

    @Override
    public String toString() {
        return "GraphNode{" +
                "id='" + id + '\'' +
                ", cbdbId='" + cbdbId + '\'' +
                ", name='" + name + '\'' +
                ", itemType='" + itemType + '\'' +
                ", importance='" + importance + '\'' +
                ", status='" + status + '\'' +
                ", isLink=" + isLink +
                '}';
    }
}