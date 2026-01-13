package com.fellowship.gandalfd.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Error response class, should look like this
 */
public class ErrorResponse {
    @JsonProperty
    final ArrayList<String> errors = new ArrayList<>();

    public ErrorResponse() {   }
    public ErrorResponse(String message) {
        errors.add(message);
    }
    public ErrorResponse(Collection<String> error) {
        this.errors.addAll(error);
    }
}
