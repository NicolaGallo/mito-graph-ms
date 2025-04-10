package com.mito.graphms.application.api;

import com.mito.graphms.domain.entity.GraphNode;
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
@RequestMapping("/api/v1/nodes")
@Tag(name = "Gestione Nodi Graph", description = "Operazioni per la gestione dei nodi in Neo4j")
public class GraphNodeController {

    private final Neo4jDataService neo4jDataService;

    @Autowired
    public GraphNodeController(Neo4jDataService neo4jDataService) {
        this.neo4jDataService = neo4jDataService;
    }

    @GetMapping
    @Operation(summary = "Recupera tutti i nodi")
    public ResponseEntity<List<GraphNode>> getAllNodes() {
        return ResponseEntity.ok(neo4jDataService.findAllNodes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera un nodo per ID interno")
    public ResponseEntity<GraphNode> getNodeById(@PathVariable String id) {
        return neo4jDataService.findNodeById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cbdb/{cbdbId}")
    @Operation(summary = "Recupera un nodo per CBDB ID")
    public ResponseEntity<GraphNode> getNodeByCbdbId(@PathVariable String cbdbId) {
        return neo4jDataService.findNodeByCbdbId(cbdbId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crea un nuovo nodo")
    public ResponseEntity<GraphNode> createNode(@Valid @RequestBody GraphNode node) {
        return ResponseEntity.ok(neo4jDataService.createNode(node));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aggiorna un nodo esistente")
    public ResponseEntity<GraphNode> updateNode(
        @PathVariable String id, 
        @Valid @RequestBody GraphNode node
    ) {
        node.setId(id);
        return ResponseEntity.ok(neo4jDataService.updateNode(node));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un nodo per ID")
    public ResponseEntity<Void> deleteNode(@PathVariable String id) {
        neo4jDataService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cbdb/{cbdbId}")
    @Operation(summary = "Elimina un nodo per CBDB ID")
    public ResponseEntity<Void> deleteNodeByCbdbId(@PathVariable String cbdbId) {
        neo4jDataService.deleteNodeByCbdbId(cbdbId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    @Operation(summary = "Esegue una query Neo4j personalizzata")
    public ResponseEntity<List<Map<String, Object>>> executeCustomQuery(
        @RequestBody String cypherQuery
    ) {
        return ResponseEntity.ok(neo4jDataService.executeCustomQuery(cypherQuery));
    }

    @PostMapping("/relation")
    @Operation(summary = "Crea una relazione tra due nodi")
    public ResponseEntity<Void> createRelation(
        @RequestParam String sourceCmdbId, 
        @RequestParam String targetCmdbId, 
        @RequestParam String relationType
    ) {
        neo4jDataService.createRelation(sourceCmdbId, targetCmdbId, relationType);
        return ResponseEntity.ok().build();
    }
}
