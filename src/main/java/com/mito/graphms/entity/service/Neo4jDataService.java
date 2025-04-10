package com.mito.graphms.entity.service;

import com.mito.graphms.domain.entity.GraphNode;
import com.mito.graphms.domain.repository.GraphNodeRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Neo4jDataService {

    private final GraphNodeRepository nodeRepository;
    private final Driver neo4jDriver;

    @Autowired
    public Neo4jDataService(
        GraphNodeRepository nodeRepository, 
        Driver neo4jDriver
    ) {
        this.nodeRepository = nodeRepository;
        this.neo4jDriver = neo4jDriver;
    }

    /**
     * Recupera tutti i nodi
     * 
     * @return Lista di tutti i nodi
     */
    @Transactional(readOnly = true)
    public List<GraphNode> findAllNodes() {
        return nodeRepository.findAll();
    }

    /**
     * Trova un nodo per ID interno
     * 
     * @param id ID interno del nodo
     * @return Optional del nodo
     */
    @Transactional(readOnly = true)
    public Optional<GraphNode> findNodeById(String id) {
        return nodeRepository.findById(id);
    }

    /**
     * Trova un nodo per CBDB ID
     * 
     * @param cbdbId CBDB ID del nodo
     * @return Optional del nodo
     */
    @Transactional(readOnly = true)
    public Optional<GraphNode> findNodeByCbdbId(String cbdbId) {
        return nodeRepository.findByCbdbId(cbdbId);
    }

    /**
     * Crea un nuovo nodo
     * 
     * @param node Nodo da creare
     * @return Nodo creato
     */
    @Transactional
    public GraphNode createNode(GraphNode node) {
        // Imposta isLink basato sul tipo di item
        node.setLink(node.getItemType() != null && node.getItemType().contains(":LINK:"));
        return nodeRepository.save(node);
    }

    /**
     * Aggiorna un nodo esistente
     * 
     * @param node Nodo da aggiornare
     * @return Nodo aggiornato
     */
    @Transactional
    public GraphNode updateNode(GraphNode node) {
        // Verifica l'esistenza del nodo prima di aggiornare
        return nodeRepository.findById(node.getId())
            .map(existingNode -> {
                node.setLink(node.getItemType() != null && node.getItemType().contains(":LINK:"));
                return nodeRepository.save(node);
            })
            .orElseThrow(() -> new RuntimeException("Nodo non trovato"));
    }

    /**
     * Elimina un nodo per ID
     * 
     * @param id ID del nodo da eliminare
     */
    @Transactional
    public void deleteNode(String id) {
        nodeRepository.deleteById(id);
    }

    /**
     * Elimina un nodo per CBDB ID
     * 
     * @param cbdbId CBDB ID del nodo da eliminare
     */
    @Transactional
    public void deleteNodeByCbdbId(String cbdbId) {
        nodeRepository.deleteByCbdbId(cbdbId);
    }

    /**
     * Esegue una query Cypher personalizzata
     * 
     * @param cypherQuery Query Cypher da eseguire
     * @return Lista di mappe contenenti i risultati
     */
    @Transactional(readOnly = true)
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
     * Crea una relazione tra due nodi
     * 
     * @param sourceCmdbId ID del nodo sorgente
     * @param targetCmdbId ID del nodo destinazione
     * @param relationType Tipo di relazione
     */
    @Transactional
    public void createRelation(String sourceCmdbId, String targetCmdbId, String relationType) {
        try (Session session = neo4jDriver.session()) {
            session.run(
                "MATCH (a:ITEM {cmdb_id: $sourceCmdbId}) " +
                "MATCH (b:ITEM {cmdb_id: $targetCmdbId}) " +
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
