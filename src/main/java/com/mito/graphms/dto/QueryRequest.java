package com.mito.graphms.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for custom Cypher query requests with parameters
 */
public class QueryRequest {
    
    private String query;
    private Map<String, Object> parameters = new HashMap<>();
    
    public QueryRequest() {
        // Default constructor
    }
    
    public QueryRequest(String query) {
        this.query = query;
    }
    
    public QueryRequest(String query, Map<String, Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
}