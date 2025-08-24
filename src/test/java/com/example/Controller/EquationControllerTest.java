package com.example.Controller;

import com.example.Service.EquationService;
import com.example.DTO.EquationEvaluationResponse;
import com.example.Exception.EquationNotFoundException;
import com.example.Exception.InvalidEquationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.expression.EvaluationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EquationController.class)
public class EquationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private EquationService equationService; // mock the service

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testStoreEquation() throws Exception {
                // Prepare input
                String equation = "x + y";
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("equation", equation);

                int equationId = 1;

                // Mock service behavior
                when(equationService.storeEquation(equation)).thenReturn(equationId);

                // Perform POST request and assert response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Equation stored successfully"))
                                .andExpect(jsonPath("$.equationId").value(equationId));

                // Test with another equation
                equation = "2*x - 3*y + z";
                requestBody.put("equation", equation);
                equationId = 2;

                // Mock service behavior
                when(equationService.storeEquation(equation)).thenReturn(equationId);
                // Perform POST request and assert response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Equation stored successfully"))
                                .andExpect(jsonPath("$.equationId").value(equationId));
        }

        @Test
        void testStoreEquation_EmptyInput() throws Exception {
                // Prepare empty input
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("equation", "");

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Missing or empty 'equation' field"));
        }

        @Test
        void testStoreEquation_NullInput() throws Exception {
                // Prepare null input (missing "equation" key)
                Map<String, String> requestBody = new HashMap<>();

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Missing or empty 'equation' field"));
        }

        @Test
        void testStoreEquation_BlankInput() throws Exception {
                // Prepare blank input
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("equation", "   ");

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Missing or empty 'equation' field"));
        }

        @Test
        void testStoreEquation_WhitespaceInput() throws Exception {
                // Prepare whitespace input
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("equation", "\n\t ");

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Missing or empty 'equation' field"));
        }

        @Test
        void testStoreEquation_InvalidInput() throws Exception {
                // Prepare invalid input (missing "equation" key)
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("invalidKey", "x + y");

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Missing or empty 'equation' field"));
        }

        @Test
        void testStoreEquation_InvalidEquation() throws Exception {
                // Prepare input with invalid equation
                String equation = "x + ";
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("equation", equation);

                // Mock service behavior to throw InvalidEquationException
                when(equationService.storeEquation(equation))
                                .thenThrow(new InvalidEquationException("Invalid equation syntax"));

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/store")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Invalid equation syntax"));
        }

        @Test
        void testGetAllEquations() throws Exception {
                // Mock service behavior
                when(equationService.getAllEquations()).thenReturn(
                                java.util.List.of(
                                                Map.of("equationId", "1", "equation", "x + y"),
                                                Map.of("equationId", "2", "equation", "2*x - 3*y + z")));

                // Perform GET request and assert response
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/equations")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.equations").isArray())
                                .andExpect(jsonPath("$.equations[0].equationId").value("1"))
                                .andExpect(jsonPath("$.equations[0].equation").value("x + y"))
                                .andExpect(jsonPath("$.equations[1].equationId").value("2"))
                                .andExpect(jsonPath("$.equations[1].equation").value("2*x - 3*y + z"));
        }

        @Test
        void testGetAllEquations_Empty() throws Exception {
                // Mock service behavior to return empty list
                when(equationService.getAllEquations()).thenReturn(java.util.List.of());

                // Perform GET request and assert response
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/equations")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.equations").isArray())
                                .andExpect(jsonPath("$.equations").isEmpty());
        }

        @Test
        void testEvaluateEquation() throws Exception {
                int equationId = 1;
                Map<String, Double> variables = Map.of("x", 2.0, "y", 3.0);

                Map<String, Map<String, Double>> requestBody = Map.of("variables", variables);

                EquationEvaluationResponse response = new EquationEvaluationResponse();
                response.setResult(5.0);

                // Mock service behavior
                when(equationService.evaluateEquation(equationId, variables)).thenReturn(response);

                mockMvc.perform(post("/api/equations/{equationId}/evaluate", equationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").value(5.0));
        }

        @Test
        void testEvaluateEquation_EquationNotFound() throws Exception {
                int equationId = 999; // Non-existent ID
                Map<String, Double> variables = Map.of("x", 2.0, "y", 3.0);
                Map<String, Map<String, Double>> requestBody = Map.of("variables", variables);

                // Mock service behavior to throw EquationNotFoundException
                when(equationService.evaluateEquation(equationId, variables))
                                .thenThrow(new EquationNotFoundException(equationId));

                mockMvc.perform(post("/api/equations/{equationId}/evaluate", equationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Equation not found for ID: " + equationId));
        }

        @Test
        void testEvaluateEquation_EvaluationException() throws Exception {
                int equationId = 1;
                Map<String, Double> variables = Map.of("x", 2.0); // Missing 'y'
                Map<String, Map<String, Double>> requestBody = Map.of("variables", variables);

                // Mock service behavior to throw EvaluationException
                when(equationService.evaluateEquation(equationId, variables))
                                .thenThrow(new EvaluationException("Missing variable: y"));

                mockMvc.perform(post("/api/equations/{equationId}/evaluate", equationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Missing variable: y"));
        }

        @Test
        void testEvaluateEquation_NoVariables() throws Exception {
                int equationId = 1;
                Map<String, Map<String, Double>> requestBody = Map.of(); // No "variables" key

                // Mock service behavior to throw EvaluationException
                when(equationService.evaluateEquation(equationId, null))
                                .thenThrow(new EvaluationException("No variables provided"));

                mockMvc.perform(post("/api/equations/{equationId}/evaluate", equationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("No variables provided"));
        }

        @Test
        void testEvaluateEquation_EmptyVariables() throws Exception {
                int equationId = 1;
                Map<String, Map<String, Double>> requestBody = Map.of("variables", Map.of()); // Empty "variables"

                // Mock service behavior to throw EvaluationException
                when(equationService.evaluateEquation(equationId, Map.of()))
                                .thenThrow(new EvaluationException("No variables provided"));

                mockMvc.perform(post("/api/equations/{equationId}/evaluate", equationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("No variables provided"));
        }

        @Test
        void testEvaluateEquation_InvalidVariablesFormat() throws Exception {
                int equationId = 1;
                // Invalid format: variables should be a map of String to Double
                Map<String, Object> requestBody = Map.of("variables", "invalid_format");

                // Perform POST request and assert bad request response
                mockMvc.perform(post("/api/equations/{equationId}/evaluate", equationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isBadRequest());
        }
}
