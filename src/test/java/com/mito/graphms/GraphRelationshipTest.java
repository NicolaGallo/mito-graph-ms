package com.mito.graphms;

import com.mito.graphms.domain.entity.GraphNode;
import com.mito.graphms.domain.entity.GraphRelationship;
import com.mito.graphms.entity.service.Neo4jDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GraphRelationshipTest {

    @Autowired
    private Neo4jDataService neo4jDataService;

    private GraphNode sourceNode;
    private GraphNode targetNode;
    private String relationshipType = "TEST_RELATIONSHIP";

    @BeforeEach
    public void setUp() {
        // Create source node
        sourceNode = new GraphNode();
        sourceNode.setCbdbId("SOURCE_TEST_NODE_001");
        sourceNode.setName("Source Test Node");
        sourceNode.setItemType("SOURCE:TYPE");
        sourceNode.setImportance("MEDIUM");
        sourceNode.setStatus("ACTIVE");

        // Create target node
        targetNode = new GraphNode();
        targetNode.setCbdbId("TARGET_TEST_NODE_001");
        targetNode.setName("Target Test Node");
        targetNode.setItemType("TARGET:TYPE");
        targetNode.setImportance("LOW");
        targetNode.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("Create and Find Relationship")
    public void testCreateAndFindRelationship() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create relationship
        GraphRelationship relationship = neo4jDataService.createRelationship(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            relationshipType
        );
        
        assertNotNull(relationship, "Relationship should not be null");
        assertNotNull(relationship.getId(), "Relationship ID should not be null");
        assertEquals(relationshipType, relationship.getType(), "Relationship type should match");
        assertEquals(savedSourceNode.getCbdbId(), relationship.getSourceNode().getCbdbId(), "Source node should match");
        assertEquals(savedTargetNode.getCbdbId(), relationship.getTargetNode().getCbdbId(), "Target node should match");
        
        // Find by ID
        Optional<GraphRelationship> foundRelationship = neo4jDataService.findRelationshipById(relationship.getId());
        assertTrue(foundRelationship.isPresent(), "Should find the relationship by ID");
        
        // Cleanup
        neo4jDataService.deleteRelationship(relationship.getId());
        neo4jDataService.deleteNodeByCbdbId(savedSourceNode.getCbdbId());
        neo4jDataService.deleteNodeByCbdbId(savedTargetNode.getCbdbId());
    }
    
    @Test
    @DisplayName("Create Relationship with Properties")
    public void testCreateRelationshipWithProperties() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("weight", 5);
        properties.put("description", "Test relationship");
        properties.put("active", true);
        
        // Create relationship with properties
        GraphRelationship relationship = neo4jDataService.createRelationshipWithProperties(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            relationshipType,
            properties
        );
        
        assertNotNull(relationship, "Relationship should not be null");
        assertEquals(3, relationship.getProperties().size(), "Should have 3 properties");
        assertEquals(5, relationship.getProperty("weight"), "Weight property should match");
        assertEquals("Test relationship", relationship.getProperty("description"), "Description property should match");
        assertEquals(true, relationship.getProperty("active"), "Active property should match");
        
        // Cleanup
        neo4jDataService.deleteRelationship(relationship.getId());
        neo4jDataService.deleteNodeByCbdbId(savedSourceNode.getCbdbId());
        neo4jDataService.deleteNodeByCbdbId(savedTargetNode.getCbdbId());
    }
    
    @Test
    @DisplayName("Find Relationships by Type")
    public void testFindRelationshipsByType() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create relationships with different types
        String typeA = "TYPE_A";
        String typeB = "TYPE_B";
        
        GraphRelationship relationshipA = neo4jDataService.createRelationship(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            typeA
        );
        
        GraphRelationship relationshipB = neo4jDataService.createRelationship(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            typeB
        );
        
        // Find by type
        List<GraphRelationship> typeARelationships = neo4jDataService.findRelationshipsByType(typeA);
        List<GraphRelationship> typeBRelationships = neo4jDataService.findRelationshipsByType(typeB);
        
        assertFalse(typeARelationships.isEmpty(), "Should find type A relationships");
        assertFalse(typeBRelationships.isEmpty(), "Should find type B relationships");
        
        // Cleanup
        neo4jDataService.deleteRelationship(relationshipA.getId());
        neo4jDataService.deleteRelationship(relationshipB.getId());
        neo4jDataService.deleteNodeByCbdbId(savedSourceNode.getCbdbId());
        neo4jDataService.deleteNodeByCbdbId(savedTargetNode.getCbdbId());
    }
    
    @Test
    @DisplayName("Find Relationships From/To Node")
    public void testFindRelationshipsFromToNode() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create relationship
        GraphRelationship relationship = neo4jDataService.createRelationship(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            relationshipType
        );
        
        // Find relationships from source node
        List<GraphRelationship> fromRelationships = neo4jDataService.findRelationshipsFromNode(
            savedSourceNode.getCbdbId()
        );
        
        // Find relationships to target node
        List<GraphRelationship> toRelationships = neo4jDataService.findRelationshipsToNode(
            savedTargetNode.getCbdbId()
        );
        
        assertFalse(fromRelationships.isEmpty(), "Should find relationships from source node");
        assertFalse(toRelationships.isEmpty(), "Should find relationships to target node");
        
        // Cleanup
        neo4jDataService.deleteRelationship(relationship.getId());
        neo4jDataService.deleteNodeByCbdbId(savedSourceNode.getCbdbId());
        neo4jDataService.deleteNodeByCbdbId(savedTargetNode.getCbdbId());
    }
    
    @Test
    @DisplayName("Delete Relationship Between Nodes")
    public void testDeleteRelationshipBetweenNodes() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create relationship
        GraphRelationship relationship = neo4jDataService.createRelationship(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            relationshipType
        );
        
        // Delete relationship
        neo4jDataService.deleteRelationshipByNodes(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            relationshipType
        );
        
        // Try to find the deleted relationship
        Optional<GraphRelationship> foundRelationship = neo4jDataService.findRelationshipById(relationship.getId());
        
        assertFalse(foundRelationship.isPresent(), "Relationship should be deleted");
        
        // Cleanup
        neo4jDataService.deleteNodeByCbdbId(savedSourceNode.getCbdbId());
        neo4jDataService.deleteNodeByCbdbId(savedTargetNode.getCbdbId());
    }
}