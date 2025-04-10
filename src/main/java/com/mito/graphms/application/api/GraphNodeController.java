package com.mito.graphms.application.api;

import com.mito.graphms.domain.entity.GraphNode;
import com.mito.graphms.domain.entity.GraphRelationship;
import com.mito.graphms.dto.QueryRequest;
import com.mito.graphms.entity.service.Neo4jDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class GraphNodeController {

    private final Neo4jDataService neo4jDataService;

    @Autowired
    public GraphNodeController(Neo4jDataService neo4jDataService) {
        this.neo4jDataService = neo4jDataService;
    }

    @Tag(name = "Node Management")
    @GetMapping("/nodes")
    @Operation(summary = "Retrieve all nodes")
    public ResponseEntity<List<GraphNode>> getAllNodes() {
        return ResponseEntity.ok(neo4jDataService.findAllNodes());
    }

    @Tag(name = "Node Management")
    @GetMapping("/nodes/{id}")
    @Operation(summary = "Retrieve a node by its internal ID")
    public ResponseEntity<GraphNode> getNodeById(@PathVariable String id) {
        return neo4jDataService.findNodeById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Tag(name = "Node Management")
    @GetMapping("/nodes/cbdb/{cbdbId}")
    @Operation(summary = "Retrieve a node by its CBDB ID")
    public ResponseEntity<GraphNode> getNodeByCbdbId(@PathVariable String cbdbId) {
        return neo4jDataService.findNodeByCbdbId(cbdbId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Tag(name = "Node Management")
    @PostMapping("/nodes")
    @Operation(summary = "Create a new node")
    public ResponseEntity<GraphNode> createNode(@Valid @RequestBody GraphNode node) {
        return ResponseEntity.ok(neo4jDataService.createNode(node));
    }

    @Tag(name = "Node Management")
    @PutMapping("/nodes/{id}")
    @Operation(summary = "Update an existing node")
    public ResponseEntity<GraphNode> updateNode(
        @PathVariable String id, 
        @Valid @RequestBody GraphNode node
    ) {
        node.setId(id);
        return ResponseEntity.ok(neo4jDataService.updateNode(node));
    }

    @Tag(name = "Node Management")
    @DeleteMapping("/nodes/{id}")
    @Operation(summary = "Delete a node by its internal ID")
    public ResponseEntity<Void> deleteNode(@PathVariable String id) {
        neo4jDataService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

    @Tag(name = "Node Management")
    @DeleteMapping("/nodes/cbdb/{cbdbId}")
    @Operation(summary = "Delete a node by its CBDB ID")
    public ResponseEntity<Void> deleteNodeByCbdbId(@PathVariable String cbdbId) {
        neo4jDataService.deleteNodeByCbdbId(cbdbId);
        return ResponseEntity.noContent().build();
    }

    @Tag(name = "Query Management")
    @PostMapping("/query")
    @Operation(summary = "Execute a custom Cypher query with parameters")
    public ResponseEntity<List<Map<String, Object>>> executeCustomQuery(
        @RequestBody QueryRequest queryRequest
    ) {
        return ResponseEntity.ok(neo4jDataService.executeCustomQueryWithParams(queryRequest));
    }

    @Tag(name = "Query Management")
    @PostMapping("/query/text")
    @Operation(summary = "Execute a custom Cypher query as plain text (Legacy)")
    public ResponseEntity<List<Map<String, Object>>> executeCustomQueryPlainText(
        @RequestBody String cypherQuery
    ) {
        return ResponseEntity.ok(neo4jDataService.executeCustomQuery(cypherQuery));
    }

    @Tag(name = "Relationship Management")
    @PostMapping("/relationships")
    @Operation(summary = "Create a relationship between two nodes")
    public ResponseEntity<GraphRelationship> createRelationship(
        @RequestParam String sourceCbdbId, 
        @RequestParam String targetCbdbId, 
        @RequestParam String type
    ) {
        return ResponseEntity.ok(neo4jDataService.createRelationship(sourceCbdbId, targetCbdbId, type));
    }

    @Tag(name = "Relationship Management")
    @PostMapping("/relationships/with-properties")
    @Operation(summary = "Create a relationship with properties")
    public ResponseEntity<GraphRelationship> createRelationshipWithProperties(
        @RequestParam String sourceCbdbId, 
        @RequestParam String targetCbdbId, 
        @RequestParam String type,
        @RequestBody Map<String, Object> properties
    ) {
        return ResponseEntity.ok(neo4jDataService.createRelationshipWithProperties(
            sourceCbdbId, targetCbdbId, type, properties));
    }

    @Tag(name = "Relationship Management")
    @GetMapping("/relationships")
    @Operation(summary = "Get all relationships")
    public ResponseEntity<List<GraphRelationship>> getAllRelationships() {
        return ResponseEntity.ok(neo4jDataService.findAllRelationships());
    }

    @Tag(name = "Relationship Management")
    @GetMapping("/relationships/{id}")
    @Operation(summary = "Get a relationship by ID")
    public ResponseEntity<GraphRelationship> getRelationshipById(@PathVariable Long id) {
        return neo4jDataService.findRelationshipById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Tag(name = "Relationship Management")
    @GetMapping("/relationships/by-type/{type}")
    @Operation(summary = "Get relationships by type")
    public ResponseEntity<List<GraphRelationship>> getRelationshipsByType(@PathVariable String type) {
        return ResponseEntity.ok(neo4jDataService.findRelationshipsByType(type));
    }

    @Tag(name = "Relationship Management")
    @GetMapping("/relationships/from/{sourceCbdbId}")
    @Operation(summary = "Get relationships from a source node")
    public ResponseEntity<List<GraphRelationship>> getRelationshipsFromNode(@PathVariable String sourceCbdbId) {
        return ResponseEntity.ok(neo4jDataService.findRelationshipsFromNode(sourceCbdbId));
    }

    @Tag(name = "Relationship Management")
    @GetMapping("/relationships/to/{targetCbdbId}")
    @Operation(summary = "Get relationships to a target node")
    public ResponseEntity<List<GraphRelationship>> getRelationshipsToNode(@PathVariable String targetCbdbId) {
        return ResponseEntity.ok(neo4jDataService.findRelationshipsToNode(targetCbdbId));
    }

    @Tag(name = "Relationship Management")
    @PutMapping("/relationships/{id}")
    @Operation(summary = "Update a relationship")
    public ResponseEntity<GraphRelationship> updateRelationship(
        @PathVariable Long id,
        @RequestBody GraphRelationship relationship
    ) {
        relationship.setId(id);
        return ResponseEntity.ok(neo4jDataService.updateRelationship(relationship));
    }

    @Tag(name = "Relationship Management")
    @DeleteMapping("/relationships/{id}")
    @Operation(summary = "Delete a relationship by ID")
    public ResponseEntity<Void> deleteRelationship(@PathVariable Long id) {
        neo4jDataService.deleteRelationship(id);
        return ResponseEntity.noContent().build();
    }

    @Tag(name = "Relationship Management")
    @DeleteMapping("/relationships")
    @Operation(summary = "Delete a relationship between nodes")
    public ResponseEntity<Void> deleteRelationshipByNodes(
        @RequestParam String sourceCbdbId,
        @RequestParam String targetCbdbId,
        @RequestParam String type
    ) {
        neo4jDataService.deleteRelationshipByNodes(sourceCbdbId, targetCbdbId, type);
        return ResponseEntity.noContent().build();
    }

    @Tag(name = "Relationship Management")
    @PostMapping("/nodes/relation") 
    @Operation(summary = "Create a relationship between two nodes (Legacy)")
    public ResponseEntity<Void> createRelation(
        @RequestParam String sourceCmdbId, 
        @RequestParam String targetCmdbId, 
        @RequestParam String relationType
    ) {
        neo4jDataService.createRelation(sourceCmdbId, targetCmdbId, relationType);
        return ResponseEntity.ok().build();
    }
}