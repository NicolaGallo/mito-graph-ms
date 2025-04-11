package com.mito.graphms.entity.service;

import com.mito.graphms.domain.entity.GraphNode;
import com.mito.graphms.domain.entity.GraphRelationship;
import com.mito.graphms.domain.repository.GraphNodeRepository;
import com.mito.graphms.domain.repository.GraphRelationshipRepository;
import com.mito.graphms.dto.QueryRequest;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Neo4jDataService {

    private final GraphNodeRepository nodeRepository;
    private final GraphRelationshipRepository relationshipRepository;
    private final Driver neo4jDriver;
    private final Neo4jTemplate neo4jTemplate;

    @Autowired
    public Neo4jDataService(
        GraphNodeRepository nodeRepository,
        GraphRelationshipRepository relationshipRepository,
        Driver neo4jDriver,
        Neo4jTemplate neo4jTemplate
    ) {
        this.nodeRepository = nodeRepository;
        this.relationshipRepository = relationshipRepository;
        this.neo4jDriver = neo4jDriver;
        this.neo4jTemplate = neo4jTemplate;
    }

    /**
     * Retrieve all nodes
     * 
     * @return List of all nodes
     */
    @Transactional(readOnly = true)
    public List<GraphNode> findAllNodes() {
        return nodeRepository.findAll();
    }

    /**
     * Find a node by internal ID
     * 
     * @param id Internal ID of the node
     * @return Optional node
     */
    @Transactional(readOnly = true)
    public Optional<GraphNode> findNodeById(String id) {
        return nodeRepository.findById(id);
    }

    /**
     * Find a node by CBDB ID
     * 
     * @param cbdbId CBDB ID of the node
     * @return Optional node
     */
    @Transactional(readOnly = true)
    public Optional<GraphNode> findNodeByCbdbId(String cbdbId) {
        return nodeRepository.findByCbdbId(cbdbId);
    }

    /**
     * Create a new node
     * 
     * @param node Node to create
     * @return Created node
     */
    @Transactional
    public GraphNode createNode(GraphNode node) {
        // Set isLink based on item type
        node.setLink(node.getItemType() != null && node.getItemType().contains(":LINK:"));
        
        // Save the node to get the generated ID
        GraphNode savedNode = nodeRepository.save(node);
        
        // To ensure we return a complete object with all properties and generated ID
        return nodeRepository.findById(savedNode.getId()).orElse(savedNode);
    }

    /**
     * Update an existing node
     * 
     * @param node Node to update
     * @return Updated node
     */
    @Transactional
    public GraphNode updateNode(GraphNode node) {
        // Verify node exists before updating
        return nodeRepository.findById(node.getId())
            .map(existingNode -> {
                // Preserve original ID and set isLink based on item type
                node.setLink(node.getItemType() != null && node.getItemType().contains(":LINK:"));
                
                // Save the node with updates
                GraphNode updatedNode = nodeRepository.save(node);
                
                // Ensure we return the complete updated object
                return nodeRepository.findById(updatedNode.getId()).orElse(updatedNode);
            })
            .orElseThrow(() -> new RuntimeException("Node not found with id: " + node.getId()));
    }

    /**
     * Delete a node by ID
     * 
     * @param id ID of the node to delete
     */
    @Transactional
    public void deleteNode(String id) {
        nodeRepository.deleteById(id);
    }

    /**
     * Delete a node by CBDB ID
     * 
     * @param cbdbId CBDB ID of the node to delete
     */
    @Transactional
    public void deleteNodeByCbdbId(String cbdbId) {
        nodeRepository.deleteByCbdbId(cbdbId);
    }

    /**
     * Execute a custom Cypher query
     * 
     * @param cypherQuery Cypher query to execute
     * @return List of maps containing the results
     */
    public List<Map<String, Object>> executeCustomQuery(String cypherQuery) {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(cypherQuery);
            return result.list(record -> 
                record.keys().stream()
                    .collect(Collectors.toMap(
                        key -> key, 
                        key -> record.get(key).asObject()
                    ))
            );
        }
    }
    
    /**
     * Execute a custom Cypher query with parameters
     * 
     * @param queryRequest Object containing query and parameters
     * @return List of maps containing the results
     */
    public List<Map<String, Object>> executeCustomQueryWithParams(QueryRequest queryRequest) {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(queryRequest.getQuery(), queryRequest.getParameters());
            
            return result.list(record -> {
                Map<String, Object> row = new HashMap<>();
                for (String key : record.keys()) {
                    row.put(key, record.get(key).asObject());
                }
                return row;
            });
        }
    }

    // ----- Relationship CRUD Operations -----
    
    /**
     * Find all relationships
     * 
     * @return List of all relationships
     */
    @Transactional(readOnly = true)
    public List<GraphRelationship> findAllRelationships() {
        return relationshipRepository.findAll();
    }
    
    /**
     * Find a relationship by ID
     * 
     * @param id ID of the relationship
     * @return Optional relationship
     */
    @Transactional(readOnly = true)
    public Optional<GraphRelationship> findRelationshipById(Long id) {
        return relationshipRepository.findById(id);
    }
    
    /**
     * Find relationships by type
     * 
     * @param type Type of relationship
     * @return List of relationships
     */
    @Transactional(readOnly = true)
    public List<GraphRelationship> findRelationshipsByType(String type) {
        return relationshipRepository.findByType(type);
    }
    
    /**
     * Find relationships from a source node
     * 
     * @param sourceCbdbId CBDB ID of the source node
     * @return List of relationships
     */
    @Transactional(readOnly = true)
    public List<GraphRelationship> findRelationshipsFromNode(String sourceCbdbId) {
        return relationshipRepository.findBySourceNodeCbdbId(sourceCbdbId);
    }
    
    /**
     * Find relationships to a target node
     * 
     * @param targetCbdbId CBDB ID of the target node
     * @return List of relationships
     */
    @Transactional(readOnly = true)
    public List<GraphRelationship> findRelationshipsToNode(String targetCbdbId) {
        return relationshipRepository.findByTargetNodeCbdbId(targetCbdbId);
    }
    
    /**
     * Create a new relationship between nodes
     * 
     * @param sourceCbdbId CBDB ID of the source node
     * @param targetCbdbId CBDB ID of the target node
     * @param type Relationship type
     * @return Created relationship
     */
    @Transactional
    public GraphRelationship createRelationship(String sourceCbdbId, String targetCbdbId, String type) {
        GraphNode sourceNode = nodeRepository.findByCbdbId(sourceCbdbId)
            .orElseThrow(() -> new RuntimeException("Source node not found with CBDB ID: " + sourceCbdbId));
        
        GraphNode targetNode = nodeRepository.findByCbdbId(targetCbdbId)
            .orElseThrow(() -> new RuntimeException("Target node not found with CBDB ID: " + targetCbdbId));
        
        GraphRelationship relationship = new GraphRelationship(sourceNode, targetNode, type);
        
        // Save the relationship to generate ID
        GraphRelationship savedRelationship = relationshipRepository.save(relationship);
        
        // Ensure we return the complete saved entity with ID
        return relationshipRepository.findById(savedRelationship.getId())
            .orElse(savedRelationship);
    }

    /**
     * Update a relationship
     * 
     * @param relationship Relationship to update
     * @return Updated relationship
     */
    @Transactional
    public GraphRelationship updateRelationship(GraphRelationship relationship) {
        if (relationship.getId() == null) {
            throw new IllegalArgumentException("Relationship ID cannot be null for update operation");
        }
        
        return relationshipRepository.findById(relationship.getId())
            .map(existingRel -> {
                // Keep the original nodes and update the rest
                relationship.setSourceNode(existingRel.getSourceNode());
                relationship.setTargetNode(existingRel.getTargetNode());
                relationship.updateTimestamp();
                
                GraphRelationship updatedRelationship = relationshipRepository.save(relationship);
                
                // Ensure we return the complete updated entity
                return relationshipRepository.findById(updatedRelationship.getId())
                    .orElse(updatedRelationship);
            })
            .orElseThrow(() -> new RuntimeException("Relationship not found with id: " + relationship.getId()));
    }
    
    /**
     * Delete a relationship by ID
     * 
     * @param id ID of the relationship to delete
     */
    @Transactional
    public void deleteRelationship(Long id) {
        relationshipRepository.deleteById(id);
    }
    
    /**
     * Delete a relationship between two nodes
     * 
     * @param sourceCbdbId CBDB ID of the source node
     * @param targetCbdbId CBDB ID of the target node
     * @param type Relationship type
     */
    @Transactional
    public void deleteRelationshipByNodes(String sourceCbdbId, String targetCbdbId, String type) {
        relationshipRepository.deleteBySourceAndTargetAndType(sourceCbdbId, targetCbdbId, type);
    }
    
    /**
     * Legacy method - creates a relationship between two nodes
     * Use createRelationship instead for new code
     * 
     * @param sourceCmdbId CMDB ID of the source node
     * @param targetCmdbId CMDB ID of the target node
     * @param relationType Relationship type
     */
    @Transactional
    public void createRelation(String sourceCmdbId, String targetCmdbId, String relationType) {
        // Utilizzo l'implementazione basata sul repository
        createRelationship(sourceCmdbId, targetCmdbId, relationType);
    }
}