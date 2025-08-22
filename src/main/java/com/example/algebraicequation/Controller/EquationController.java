package com.example.algebraicequation.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.algebraicequation.Service.EquationService;

import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/equations")
public class EquationController {
    private final EquationService equationService;

    public EquationController(EquationService equationService) {
        this.equationService = equationService;
    }

    @PostMapping("/store")
    public ResponseEntity<Map<String, Object>> storeEquation(@RequestBody Map<String, String> request) {
        // Extract equation string from request body
        String equationStr = request.get("equation");

        Map<String, Object> response = equationService.storeEquation(equationStr);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEquations() {
        // Call service to get the structured response
        Map<String, Object> response = equationService.getAllEquations();

        // Wrap in ResponseEntity and return
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{equationId}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateEquation(
            @PathVariable int equationId,
            @RequestBody Map<String, Map<String, Double>> request) {

        Map<String, Double> variables = request.get("variables");
        Map<String, Object> response = equationService.evaluateEquation(equationId, variables);
        return ResponseEntity.ok(response);
    }
}