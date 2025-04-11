package com.mito.graphms.domain.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RelationshipProperties
public class GraphRelationship implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    
    private String type;
    
    @TargetNode
    @JsonIgnoreProperties({"outgoingRelations", "incomingRelations"})
    private GraphNode targetNode;
    
    @JsonIgnoreProperties({"outgoingRelations", "incomingRelations"})
    private GraphNode sourceNode;
    
    // Singular properties for the relationship
    private Integer weight;
    private String description;
    private Boolean active;
    private String priority;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public GraphRelationship() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public GraphRelationship(GraphNode sourceNode, GraphNode targetNode, String type) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GraphNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(GraphNode targetNode) {
        this.targetNode = targetNode;
    }

    public GraphNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(GraphNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Convenience method to access properties as a map (for compatibility)
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        if (weight != null) properties.put("weight", weight);
        if (description != null) properties.put("description", description);
        if (active != null) properties.put("active", active);
        if (priority != null) properties.put("priority", priority);
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphRelationship that = (GraphRelationship) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GraphRelationship{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", source='" + (sourceNode != null ? sourceNode.getCbdbId() : "null") + '\'' +
                ", target='" + (targetNode != null ? targetNode.getCbdbId() : "null") + '\'' +
                ", weight=" + weight +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}