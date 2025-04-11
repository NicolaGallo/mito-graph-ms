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
        // Prima puliamo i dati di eventuali test precedenti
        cleanupTestData();
        
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
    
    /**
     * Helper method to clean up test data
     */
    private void cleanupTestData() {
        try {
            // Prima eliminiamo eventuali relazioni esistenti
            String cleanupQuery = "MATCH (s:ITEM)-[r]-(t:ITEM) WHERE s.cbdb_id IN ['SOURCE_TEST_NODE_001', 'TARGET_TEST_NODE_001'] DELETE r";
            neo4jDataService.executeCustomQuery(cleanupQuery);
            
            // Poi eliminiamo i nodi
            neo4jDataService.deleteNodeByCbdbId("SOURCE_TEST_NODE_001");
            neo4jDataService.deleteNodeByCbdbId("TARGET_TEST_NODE_001");
        } catch (Exception e) {
            // Ignoriamo le eccezioni durante la pulizia
        }
    }
    
    @Test
    @DisplayName("Create and Find Relationship")
    public void testCreateAndFindRelationship() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Test direct relationship creation using Cypher (bypassing repositories)
        String createRelCypher = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'}), (t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                               "CREATE (s)-[r:" + relationshipType + "]->(t)";
        neo4jDataService.executeCustomQuery(createRelCypher);
        
        // Verify relationship exists using a query
        String verifyQuery = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r:" + relationshipType + 
                            "]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) RETURN count(r) as relationCount";
        List<Map<String, Object>> result = neo4jDataService.executeCustomQuery(verifyQuery);
        
        assertFalse(result.isEmpty(), "Should have relationship results");
        assertEquals(1L, result.get(0).get("relationCount"), "Should have exactly one relationship");
        
        // Cleanup
        cleanupTestData();
    }
    
    @Test
    @DisplayName("Create Relationship with Properties")
    public void testCreateRelationshipWithProperties() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Test direct relationship creation with properties using Cypher
        String createRelCypher = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'}), (t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                               "CREATE (s)-[r:" + relationshipType + " {weight: 5, description: 'Test relationship', active: true}]->(t)";
        neo4jDataService.executeCustomQuery(createRelCypher);
        
        // Verify relationship exists with properties
        String verifyQuery = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r:" + relationshipType + 
                            "]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                            "RETURN r.weight as weight, r.description as description, r.active as active";
        List<Map<String, Object>> result = neo4jDataService.executeCustomQuery(verifyQuery);
        
        assertFalse(result.isEmpty(), "Should have relationship results");
        assertEquals(5L, result.get(0).get("weight"), "Weight property should match");
        assertEquals("Test relationship", result.get(0).get("description"), "Description property should match");
        assertEquals(true, result.get(0).get("active"), "Active property should match");
        
        // Cleanup
        cleanupTestData();
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
        
        // Create relationships using direct Cypher
        String createRelA = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'}), (t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                           "CREATE (s)-[r:" + typeA + "]->(t)";
        String createRelB = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'}), (t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                           "CREATE (s)-[r:" + typeB + "]->(t)";
        
        neo4jDataService.executeCustomQuery(createRelA);
        neo4jDataService.executeCustomQuery(createRelB);
        
        // Verify different relationship types
        String verifyQuery = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                           "RETURN type(r) as relType";
        List<Map<String, Object>> results = neo4jDataService.executeCustomQuery(verifyQuery);
        
        assertEquals(2, results.size(), "Should have two relationships");
        assertTrue(results.stream().anyMatch(r -> typeA.equals(r.get("relType"))), "Should have TYPE_A relationship");
        assertTrue(results.stream().anyMatch(r -> typeB.equals(r.get("relType"))), "Should have TYPE_B relationship");
        
        // Cleanup
        cleanupTestData();
    }
    
    @Test
    @DisplayName("Find Relationships From/To Node")
    public void testFindRelationshipsFromToNode() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create relationship directly with Cypher
        String createRel = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'}), (t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                          "CREATE (s)-[r:" + relationshipType + "]->(t)";
        neo4jDataService.executeCustomQuery(createRel);
        
        // Verify outgoing relationships
        String outgoingQuery = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r]->() RETURN count(r) as outCount";
        List<Map<String, Object>> outResults = neo4jDataService.executeCustomQuery(outgoingQuery);
        
        // Verify incoming relationships
        String incomingQuery = "MATCH ()-[r]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) RETURN count(r) as inCount";
        List<Map<String, Object>> inResults = neo4jDataService.executeCustomQuery(incomingQuery);
        
        assertEquals(1L, outResults.get(0).get("outCount"), "Should have one outgoing relationship");
        assertEquals(1L, inResults.get(0).get("inCount"), "Should have one incoming relationship");
        
        // Cleanup
        cleanupTestData();
    }
    
    @Test
    @DisplayName("Delete Relationship Between Nodes")
    public void testDeleteRelationshipBetweenNodes() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Create relationship directly with Cypher
        String createRel = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'}), (t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                          "CREATE (s)-[r:" + relationshipType + "]->(t)";
        neo4jDataService.executeCustomQuery(createRel);
        
        // Verify relationship exists
        String verifyQueryBefore = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r:" + relationshipType + 
                                  "]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) RETURN count(r) as relationCount";
        List<Map<String, Object>> resultBefore = neo4jDataService.executeCustomQuery(verifyQueryBefore);
        assertEquals(1L, resultBefore.get(0).get("relationCount"), "Should have one relationship before delete");
        
        // Delete relationship using direct Cypher
        String deleteRel = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r:" + relationshipType + 
                         "]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) DELETE r";
        neo4jDataService.executeCustomQuery(deleteRel);
        
        // Verify relationship no longer exists
        List<Map<String, Object>> resultAfter = neo4jDataService.executeCustomQuery(verifyQueryBefore);
        assertEquals(0L, resultAfter.get(0).get("relationCount"), "Should have no relationships after delete");
        
        // Cleanup
        cleanupTestData();
    }
    
    @Test
    @DisplayName("Test Create Relation (Legacy Method)")
    public void testCreateRelation() {
        // Create the nodes first
        GraphNode savedSourceNode = neo4jDataService.createNode(sourceNode);
        GraphNode savedTargetNode = neo4jDataService.createNode(targetNode);
        
        // Use the legacy method
        neo4jDataService.createRelation(
            savedSourceNode.getCbdbId(),
            savedTargetNode.getCbdbId(),
            "LEGACY_RELATION"
        );
        
        // Verify relationship exists using a query
        String verifyQuery = "MATCH (s:ITEM {cbdb_id: 'SOURCE_TEST_NODE_001'})-[r:LEGACY_RELATION]->(t:ITEM {cbdb_id: 'TARGET_TEST_NODE_001'}) " +
                           "RETURN count(r) as relationCount";
        List<Map<String, Object>> result = neo4jDataService.executeCustomQuery(verifyQuery);
        
        assertEquals(1L, result.get(0).get("relationCount"), "Should have exactly one relationship");
        
        // Cleanup
        cleanupTestData();
    }
}