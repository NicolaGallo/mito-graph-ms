package com.mito.graphms.domain.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RelationshipProperties
public class GraphRelationship implements Serializable {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue
    private Long id;
    
    private String type;
    
    @TargetNode
    @JsonIgnoreProperties({"outgoingRelations", "incomingRelations"})
    private GraphNode targetNode;
    
    @JsonIgnoreProperties({"outgoingRelations", "incomingRelations"})
    private GraphNode sourceNode;
    
    // Questo campo viene serializzato come JSON e memorizzato come stringa in Neo4j
    private String propertiesJson = "{}";
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Questo campo è transiente e non viene salvato in Neo4j
    @JsonIgnore
    private transient Map<String, Object> propertiesCache;
    
    public GraphRelationship() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.propertiesCache = new HashMap<>();
    }
    
    public GraphRelationship(GraphNode sourceNode, GraphNode targetNode, String type) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.propertiesCache = new HashMap<>();
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

    public String getPropertiesJson() {
        return propertiesJson;
    }

    public void setPropertiesJson(String propertiesJson) {
        this.propertiesJson = propertiesJson;
        // Invalida la cache quando si imposta direttamente il JSON
        this.propertiesCache = null;
    }

    @JsonIgnore
    public Map<String, Object> getProperties() {
        if (propertiesCache == null) {
            try {
                propertiesCache = objectMapper.readValue(propertiesJson, 
                    objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
            } catch (JsonProcessingException e) {
                propertiesCache = new HashMap<>();
            }
        }
        return propertiesCache;
    }

    @JsonIgnore
    public void setProperties(Map<String, Object> properties) {
        this.propertiesCache = properties;
        try {
            this.propertiesJson = objectMapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            this.propertiesJson = "{}";
        }
    }
    
    public Object getProperty(String key) {
        return getProperties().get(key);
    }
    
    public void setProperty(String key, Object value) {
        Map<String, Object> props = getProperties();
        props.put(key, value);
        setProperties(props);
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
                ", properties=" + getProperties() +
                '}';
    }
}