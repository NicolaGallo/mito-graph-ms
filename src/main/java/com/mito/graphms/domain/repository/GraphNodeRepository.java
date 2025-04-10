package com.mito.graphms.domain.repository;

import com.mito.graphms.domain.entity.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface GraphNodeRepository extends Neo4jRepository<GraphNode, String> {
    /**
     * Trova un nodo per il suo CBDB ID
     * 
     * @param cbdbId Identificativo CBDB del nodo
     * @return Optional contenente il nodo se trovato
     */
    Optional<GraphNode> findByCbdbId(String cbdbId);

    /**
     * Trova nodi per tipo di item
     * 
     * @param itemType Tipo di item
     * @return Lista di nodi corrispondenti
     */
    List<GraphNode> findByItemType(String itemType);

    /**
     * Verifica l'esistenza di un nodo per CBDB ID
     * 
     * @param cbdbId Identificativo CBDB del nodo
     * @return true se il nodo esiste, false altrimenti
     */
    boolean existsByCbdbId(String cbdbId);

    /**
     * Elimina un nodo per CBDB ID
     * 
     * @param cbdbId Identificativo CBDB del nodo
     */
    void deleteByCbdbId(String cbdbId);
}
