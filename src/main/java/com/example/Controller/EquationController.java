package com.example.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.expression.EvaluationException;
import org.springframework.http.ResponseEntity;

import com.example.DTO.EquationEvaluationResponse;
import com.example.Exception.EquationNotFoundException;
import com.example.Exception.InvalidEquationException;
import com.example.Service.EquationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing algebraic equations.
 * Provides endpoints to store, retrieve, and evaluate equations.
 */
@RestController
@RequestMapping("/api/equations")
public class EquationController {

    private final EquationService equationService;

    /**
     * Constructor for dependency injection of EquationService.
     *
     * @param equationService the service responsible for equation operations
     */
    public EquationController(EquationService equationService) {
        this.equationService = equationService;
    }

    /**
     * Stores a new algebraic equation.
     *
     * @param requestBody a map containing the equation string with key "equation"
     * @return a response entity with a message and the stored equation's ID
     */
    @PostMapping("/store")
    public ResponseEntity<Map<String, Object>> storeEquation(@RequestBody Map<String, String> requestBody) {
        String equation = requestBody.get("equation");
        if (equation == null || equation.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing or empty 'equation' field"));
        }

        try {
            int equationId = equationService.storeEquation(equation);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Equation stored successfully");
            response.put("equationId", equationId);
            return ResponseEntity.ok(response);
        } catch (InvalidEquationException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Retrieves all stored equations.
     *
     * @return a response entity containing a list of all equations
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEquations() {
        List<Map<String, String>> equationsList = equationService.getAllEquations();

        Map<String, Object> response = new HashMap<>();
        response.put("equations", equationsList);

        return ResponseEntity.ok(response);
    }

    /**
     * Evaluates a specific equation with provided variable values.
     *
     * @param equationId  the ID of the equation to evaluate
     * @param requestBody a map containing variable values under the key "variables"
     * @return a response entity with the evaluation result
     */
    @PostMapping("/{equationId}/evaluate")
    public ResponseEntity<?> evaluateEquation(
            @PathVariable("equationId") int equationId,
            @RequestBody Map<String, Map<String, Double>> requestBody) {

        Map<String, Double> variables = requestBody.get("variables");

        try {
            EquationEvaluationResponse response = equationService.evaluateEquation(equationId, variables);
            return ResponseEntity.ok(response);
        } catch (EquationNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", ex.getMessage()));
        } catch (EvaluationException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}