package com.mito.graphms.domain.repository;

import com.mito.graphms.domain.entity.GraphRelationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraphRelationshipRepository extends Neo4jRepository<GraphRelationship, Long> {
    
    /**
     * Find all relationships of a specific type
     * 
     * @param type The relationship type
     * @return List of relationships
     */
    List<GraphRelationship> findByType(String type);
    
    /**
     * Find relationships where the specified node is the source
     * 
     * @param sourceCbdbId The CBDB ID of the source node
     * @return List of relationships
     */
    @Query("MATCH (s:ITEM {cbdb_id: $sourceCbdbId})-[r]->(t:ITEM) RETURN s, r, t")
    List<GraphRelationship> findBySourceNodeCbdbId(@Param("sourceCbdbId") String sourceCbdbId);
    
    /**
     * Find relationships where the specified node is the target
     * 
     * @param targetCbdbId The CBDB ID of the target node
     * @return List of relationships
     */
    @Query("MATCH (s:ITEM)-[r]->(t:ITEM {cbdb_id: $targetCbdbId}) RETURN s, r, t")
    List<GraphRelationship> findByTargetNodeCbdbId(@Param("targetCbdbId") String targetCbdbId);
    
    /**
     * Find a relationship between two specific nodes
     * 
     * @param sourceCbdbId The CBDB ID of the source node
     * @param targetCbdbId The CBDB ID of the target node
     * @param type The relationship type
     * @return Optional containing the relationship if found
     */
    @Query("MATCH (s:ITEM {cbdb_id: $sourceCbdbId})-[r:`${type}`]->(t:ITEM {cbdb_id: $targetCbdbId}) RETURN s, r, t LIMIT 1")
    Optional<GraphRelationship> findBySourceAndTargetAndType(
        @Param("sourceCbdbId") String sourceCbdbId, 
        @Param("targetCbdbId") String targetCbdbId, 
        @Param("type") String type
    );
    
    /**
     * Delete a relationship between two specific nodes
     * 
     * @param sourceCbdbId The CBDB ID of the source node
     * @param targetCbdbId The CBDB ID of the target node
     * @param type The relationship type
     */
    @Query("MATCH (s:ITEM {cbdb_id: $sourceCbdbId})-[r:`${type}`]->(t:ITEM {cbdb_id: $targetCbdbId}) DELETE r")
    void deleteBySourceAndTargetAndType(
        @Param("sourceCbdbId") String sourceCbdbId, 
        @Param("targetCbdbId") String targetCbdbId, 
        @Param("type") String type
    );
}