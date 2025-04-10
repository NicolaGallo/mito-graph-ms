package com.mito.graphms;

import com.mito.graphms.domain.entity.GraphNode;
import com.mito.graphms.entity.service.Neo4jDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GraphNodeServiceTest {

    @Autowired
    private Neo4jDataService neo4jDataService;

    private GraphNode testNode;

    @BeforeEach
    public void setUp() {
        // Preparazione di un nodo di test standard
        testNode = new GraphNode();
        testNode.setCbdbId("TEST_NODE_001");
        testNode.setName("Nodo di Test");
        testNode.setItemType("TEST:TYPE");
        testNode.setImportance("MEDIUM");
        testNode.setStatus("ACTIVE");
        testNode.setLocationLat(45.4642);
        testNode.setLocationLon(9.1900);
    }

    @Test
    @DisplayName("Creazione e Recupero di un Nodo")
    public void testCreateAndFindNode() {
        // Crea un nodo
        GraphNode createdNode = neo4jDataService.createNode(testNode);
        
        assertNotNull(createdNode, "Il nodo creato non dovrebbe essere nullo");
        assertEquals("TEST_NODE_001", createdNode.getCbdbId(), "CBDB ID dovrebbe corrispondere");
        assertTrue(createdNode.getId() != null, "Dovrebbe essere generato un ID interno");
    }

    @Test
    @DisplayName("Recupero Nodo per CBDB ID")
    public void testFindNodeByCbdbId() {
        // Crea un nodo
        neo4jDataService.createNode(testNode);
        
        // Recupera il nodo
        Optional<GraphNode> foundNode = neo4jDataService.findNodeByCbdbId("TEST_NODE_001");
        
        assertTrue(foundNode.isPresent(), "Nodo dovrebbe essere trovato");
        assertEquals("Nodo di Test", foundNode.get().getName(), "Nome del nodo dovrebbe corrispondere");
    }

    @Test
    @DisplayName("Aggiornamento Nodo")
    public void testUpdateNode() {
        // Crea un nodo
        GraphNode createdNode = neo4jDataService.createNode(testNode);
        
        // Modifica alcuni attributi
        createdNode.setName("Nodo Aggiornato");
        createdNode.setImportance("HIGH");
        
        // Aggiorna il nodo
        GraphNode updatedNode = neo4jDataService.updateNode(createdNode);
        
        assertEquals("Nodo Aggiornato", updatedNode.getName(), "Nome dovrebbe essere aggiornato");
        assertEquals("HIGH", updatedNode.getImportance(), "Importanza dovrebbe essere aggiornata");
    }

    @Test
    @DisplayName("Eliminazione Nodo")
    public void testDeleteNode() {
        // Crea un nodo
        GraphNode createdNode = neo4jDataService.createNode(testNode);
        
        // Elimina il nodo
        neo4jDataService.deleteNodeByCbdbId("TEST_NODE_001");
        
        // Verifica eliminazione
        Optional<GraphNode> foundNode = neo4jDataService.findNodeByCbdbId("TEST_NODE_001");
        assertTrue(foundNode.isEmpty(), "Nodo dovrebbe essere eliminato");
    }

    @Test
    @DisplayName("Recupero di Tutti i Nodi")
    public void testFindAllNodes() {
        // Crea alcuni nodi di test
        neo4jDataService.createNode(testNode);
        
        GraphNode anotherNode = new GraphNode();
        anotherNode.setCbdbId("TEST_NODE_002");
        anotherNode.setName("Altro Nodo di Test");
        anotherNode.setItemType("TEST:ANOTHER_TYPE");
        neo4jDataService.createNode(anotherNode);
        
        // Recupera tutti i nodi
        List<GraphNode> allNodes = neo4jDataService.findAllNodes();
        
        assertTrue(allNodes.size() >= 2, "Dovrebbero essere presenti almeno due nodi");
    }

    @Test
    @DisplayName("Esecuzione Query Personalizzata")
    public void testExecuteCustomQuery() {
        // Crea un nodo di test
        neo4jDataService.createNode(testNode);
        
        // Esegui una query Cypher di esempio
        String cypherQuery = "MATCH (n:ITEM {cbdb_id: 'TEST_NODE_001'}) RETURN n.name as name, n.cbdb_id as cbdbId";
        
        List<Map<String, Object>> queryResults = neo4jDataService.executeCustomQuery(cypherQuery);
        
        assertFalse(queryResults.isEmpty(), "La query dovrebbe restituire risultati");
        assertEquals("Nodo di Test", queryResults.get(0).get("name"), "Nome del nodo dovrebbe corrispondere");
        assertEquals("TEST_NODE_001", queryResults.get(0).get("cbdbId"), "CBDB ID dovrebbe corrispondere");
    }

    @Test
    @DisplayName("Creazione Relazione tra Nodi")
    public void testCreateRelation() {
        // Crea due nodi
        GraphNode sourceNode = new GraphNode();
        sourceNode.setCbdbId("SOURCE_NODE_001");
        sourceNode.setName("Nodo Sorgente");
        neo4jDataService.createNode(sourceNode);
        
        GraphNode targetNode = new GraphNode();
        targetNode.setCbdbId("TARGET_NODE_001");
        targetNode.setName("Nodo Destinazione");
        neo4jDataService.createNode(targetNode);
        
        // Crea una relazione
        neo4jDataService.createRelation(
            "SOURCE_NODE_001", 
            "TARGET_NODE_001", 
            "CONNECTS_TO"
        );
        
        // Verifica la relazione (potrebbe richiedere una query Cypher specifica)
        String verifyQuery = "MATCH (a:ITEM {cmdb_id: 'SOURCE_NODE_001'})-[r:CONNECTS_TO]->(b:ITEM {cmdb_id: 'TARGET_NODE_001'}) RETURN count(r) as relationCount";
        
        List<Map<String, Object>> relationResults = neo4jDataService.executeCustomQuery(verifyQuery);
        
        assertFalse(relationResults.isEmpty(), "Dovrebbe esistere una relazione");
        assertEquals(1L, relationResults.get(0).get("relationCount"), "Dovrebbe esistere esattamente una relazione");
    }
}