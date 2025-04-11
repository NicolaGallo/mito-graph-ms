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
        // Assicuriamoci di restituire un solo risultato
        List<GraphNode> nodes = nodeRepository.findAll()
            .stream()
            .filter(n -> cbdbId.equals(n.getCbdbId()))
            .collect(Collectors.toList());
        
        if (nodes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(nodes.get(0));
        }
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
        return nodeRepository.save(node);
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
                node.setLink(node.getItemType() != null && node.getItemType().contains(":LINK:"));
                return nodeRepository.save(node);
            })
            .orElseThrow(() -> new RuntimeException("Node not found"));
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
        // Utilizziamo direttamente il driver Cypher
        try (Session session = neo4jDriver.session()) {
            // Creiamo la relazione
            String cypher = "MATCH (source:ITEM {cbdb_id: $sourceCbdbId}), " +
                            "(target:ITEM {cbdb_id: $targetCbdbId}) " +
                            "CREATE (source)-[r:" + type + "]->(target) " +
                            "RETURN id(r) as relId";
            
            Map<String, Object> params = new HashMap<>();
            params.put("sourceCbdbId", sourceCbdbId);
            params.put("targetCbdbId", targetCbdbId);
            
            Result result = session.run(cypher, params);
            if (result.hasNext()) {
                GraphNode sourceNode = nodeRepository.findByCbdbId(sourceCbdbId).orElseThrow();
                GraphNode targetNode = nodeRepository.findByCbdbId(targetCbdbId).orElseThrow();
                
                GraphRelationship relationship = new GraphRelationship(sourceNode, targetNode, type);
                return relationship;  // Non salviamo nel repository, ma restituiamo solo l'oggetto
            }
            
            throw new RuntimeException("Failed to create relationship");
        }
    }
    
    /**
     * Create a new relationship with properties
     * 
     * @param sourceCbdbId CBDB ID of the source node
     * @param targetCbdbId CBDB ID of the target node
     * @param type Relationship type
     * @param properties Map of properties
     * @return Created relationship
     */
    @Transactional
    public GraphRelationship createRelationshipWithProperties(
        String sourceCbdbId, 
        String targetCbdbId, 
        String type,
        Map<String, Object> properties
    ) {
        try (Session session = neo4jDriver.session()) {
            // Prima assicuriamoci che i nodi esistano
            boolean sourceExists = nodeRepository.existsByCbdbId(sourceCbdbId);
            boolean targetExists = nodeRepository.existsByCbdbId(targetCbdbId);
            
            if (!sourceExists || !targetExists) {
                throw new RuntimeException("Source or target node not found");
            }
            
            // Costruiamo la query Cypher
            StringBuilder cypher = new StringBuilder();
            cypher.append("MATCH (source:ITEM {cbdb_id: $sourceCbdbId}), ");
            cypher.append("(target:ITEM {cbdb_id: $targetCbdbId}) ");
            cypher.append("CREATE (source)-[r:" + type + " {");
            
            // Aggiungiamo le proprietà una ad una
            int i = 0;
            for (String key : properties.keySet()) {
                if (i > 0) cypher.append(", ");
                cypher.append(key).append(": $").append(key);
                i++;
            }
            
            cypher.append("}]->(target) ");
            cypher.append("RETURN id(r) as relId");
            
            // Eseguiamo la query con i parametri
            Map<String, Object> params = new HashMap<>();
            params.put("sourceCbdbId", sourceCbdbId);
            params.put("targetCbdbId", targetCbdbId);
            
            // Aggiungiamo le proprietà ai parametri
            params.putAll(properties);
            
            // Eseguiamo la query
            Result result = session.run(cypher.toString(), params);
            
            if (result.hasNext()) {
                GraphNode sourceNode = nodeRepository.findByCbdbId(sourceCbdbId).orElseThrow();
                GraphNode targetNode = nodeRepository.findByCbdbId(targetCbdbId).orElseThrow();
                
                GraphRelationship relationship = new GraphRelationship(sourceNode, targetNode, type);
                
                // Settiamo le proprietà singolarmente
                if (properties.containsKey("weight")) {
                    relationship.setWeight(((Number) properties.get("weight")).intValue());
                }
                if (properties.containsKey("description")) {
                    relationship.setDescription((String) properties.get("description"));
                }
                if (properties.containsKey("active")) {
                    relationship.setActive((Boolean) properties.get("active"));
                }
                if (properties.containsKey("priority")) {
                    relationship.setPriority((String) properties.get("priority"));
                }
                
                return relationship;  // Non salviamo nel repository, ma restituiamo solo l'oggetto
            }
            
            throw new RuntimeException("Failed to create relationship with properties");
        }
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
                
                return relationshipRepository.save(relationship);
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
        try (Session session = neo4jDriver.session()) {
            Map<String, Object> params = new HashMap<>();
            params.put("sourceCbdbId", sourceCbdbId);
            params.put("targetCbdbId", targetCbdbId);
            
            session.run(
                "MATCH (s:ITEM {cbdb_id: $sourceCbdbId})-[r:" + type + "]->(t:ITEM {cbdb_id: $targetCbdbId}) DELETE r",
                params
            );
        }
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
        try (Session session = neo4jDriver.session()) {
            session.run(
                "MATCH (a:ITEM {cbdb_id: $sourceCmdbId}) " +
                "MATCH (b:ITEM {cbdb_id: $targetCmdbId}) " +
                "MERGE (a)-[r:" + relationType + "]->(b) " +
                "RETURN count(*)",
                Map.of(
                    "sourceCmdbId", sourceCmdbId, 
                    "targetCmdbId", targetCmdbId
                )
            );
        }
    }
}