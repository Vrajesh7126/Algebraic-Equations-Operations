package com.example.Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.DTO.EquationEvaluationResponse;
import com.example.Exception.EvaluationException;
import com.example.Exception.InvalidEquationException;
import com.example.Manager.EquationManager;

@ExtendWith(MockitoExtension.class)
class EquationServiceTest {

    // Use real EquationManager
    private EquationManager equationManager = new EquationManager();

    // Inject real EquationManager into service
    private EquationService equationService = new EquationService(equationManager);

    @Test
    void testStoreEquation() {
        String infixExpression = "3x + 2y - z";
        int equationId = equationService.storeEquation(infixExpression);
        assertThat(equationId).isEqualTo(1);

        infixExpression = "x^2 + y^2 = z^2";
        equationId = equationService.storeEquation(infixExpression);
        assertThat(equationId).isEqualTo(2);
    }

    @Test
    void testInvalidEquations() {
        String[] invalidExpressions = {
                // Consecutive operators
                "3x ++ 2y", "4a -- 5b", "x ** y", "a // b", "3x + * y",

                // Starts/ends with operator
                "+ x - y", "- 3x + 2", "x + y -", "x *",

                // Mismatched/unbalanced parentheses
                "(3x + 2y", "3x + 2y)", "((x + y)", "(x + y))",

                // Empty parentheses
                "3x + () - y", "( ) + 2x",

                // Invalid characters
                "3x + 2y # z", "x @ y", "y $ z", "z % 3", "2x ~ y",

                // Missing operators
                "3x 2y + z", "2x (y + z)", "(x + y)(x - y)",

                // Multiple equal signs
                "x^2 + y^2 = z^2 = w^2", "a = b = c",

                // Standalone operators
                "*", "+", "=", "^",

                // Empty/whitespace
                "", " ",

                // Bad power usage
                "x ^^ 2", "^2 + x",
        };

        for (String expr : invalidExpressions) {
            assertThatThrownBy(() -> equationService.storeEquation(expr))
                    .isInstanceOf(InvalidEquationException.class)
                    .hasMessageContaining("Invalid equation");
        }
    }

    @Test
    void testValidEquations() {
        String[] validExpressions = {
                "3x + 2y - z",
                "x ^ 2 + y ^ 2 = z ^ 2",
                "a + b * c",
                "m / n + p - q",
                "x ^ 3 + y ^ 3 = z ^ 3",
                "x + y * z",
                "x + y * z - w",
                "a * b + c / d - e ^ f",
                "u + v - w * x / y ^ z"
        };

        int expectedId = 1;
        for (String expr : validExpressions) {
            int equationId = equationService.storeEquation(expr);
            assertThat(equationId).isEqualTo(expectedId);
            expectedId++;
        }

        // Optional: verify all stored equations
        List<Map<String, String>> allEquations = equationService.getAllEquations();
        assertThat(allEquations).hasSize(validExpressions.length);
        for (int i = 0; i < validExpressions.length; i++) {
            assertThat(allEquations.get(i).get("equation")).isEqualTo(validExpressions[i]);
        }
    }

    @Test
    void testGetAllEquations() {
        String infixExpression1 = "3x + 2y - z";
        String infixExpression2 = "x^2 + y^2 = z^2";

        equationService.storeEquation(infixExpression1);
        equationService.storeEquation(infixExpression2);

        List<Map<String, String>> allEquations = equationService.getAllEquations();

        assertThat(allEquations).hasSize(2);
        assertThat(allEquations.get(0).get("equation")).isEqualTo("3x + 2y - z");
        assertThat(allEquations.get(1).get("equation")).isEqualTo("x ^ 2 + y ^ 2 = z ^ 2");
    }

    @Test
    void testEvaluateEquation() {
        String infixExpression = "3x + 2y - z";
        int equationId = equationService.storeEquation(infixExpression);
        EquationEvaluationResponse response = equationService.evaluateEquation(equationId,
                Map.of("x", 1.0, "y", 2.0, "z", 3.0));
        assertThat(response.getResult()).isEqualTo(4.0);

        infixExpression = "x^2 + y^2 = z^2";
        equationId = equationService.storeEquation(infixExpression);
        response = equationService.evaluateEquation(equationId,
                Map.of("x", 3.0, "y", 4.0, "z", 5.0));
        assertThat(response.getResult()).isEqualTo(0.0); // 3^2 + 4^2 - 5^2 = 0

        response = equationService.evaluateEquation(equationId,
                Map.of("x", 1.0, "y", 1.0, "z", 1.0));
        assertThat(response.getResult()).isEqualTo(1.0); // 1^2 + 1^2 - 1^2 = 1
    }

    @Test
    void testEvaluateNonExistentEquation() {
        try {
            equationService.evaluateEquation(999, Map.of("x", 1.0));
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Equation not found for ID: 999");
        }
    }

    @Test
    void testEvaluateWithMissingVariables() {
        String infixExpression = "3x + 2y - z";
        int equationId = equationService.storeEquation(infixExpression);

        // Missing variable 'z'
        Map<String, Double> variables = Map.of("x", 1.0, "y", 2.0);

        assertThatThrownBy(() -> equationService.evaluateEquation(equationId, variables))
                .isInstanceOf(EvaluationException.class)
                .hasMessageContaining("Failed to evaluate equation");
    }

    @Test
    void testEvaluateWithNoVariables() {
        String infixExpression = "5 + 3 - 2";
        int equationId = equationService.storeEquation(infixExpression);

        EquationEvaluationResponse response = equationService.evaluateEquation(equationId, Map.of());

        // 5 + 3 - 2 = 6
        assertThat(response.getResult()).isEqualTo(6.0);
    }

    @Test
    void testStoreEquationWithWhitespace() {
        String infixExpression = " 3x + 2y - z ";
        int equationId = equationService.storeEquation(infixExpression);
        assertThat(equationId).isEqualTo(1);

        List<Map<String, String>> allEquations = equationService.getAllEquations();
        assertThat(allEquations).hasSize(1);
        assertThat(allEquations.get(0).get("equation")).isEqualTo("3x + 2y - z");
    }

    @Test
    void testStoreEquationWithComplexVariables() {
        String infixExpression = "3var1 + 2var_2 - var3";
        int equationId = equationService.storeEquation(infixExpression);
        assertThat(equationId).isEqualTo(1);
        List<Map<String, String>> allEquations = equationService.getAllEquations();
        assertThat(allEquations).hasSize(1);
        assertThat(allEquations.get(0).get("equation")).isEqualTo("3var1 + 2var_2 - var3");

        EquationEvaluationResponse response = equationService.evaluateEquation(equationId,
                Map.of("var1", 1.0, "var_2", 2.0, "var3", 3.0));
        assertThat(response.getResult()).isEqualTo(4.0); // 3*1 + 2*2 - 3 = 4
    }

    @Test
    void testStoreEquationWithNestedParentheses() {
        String infixExpression = "3 * (x + (2y - z))";
        int equationId = equationService.storeEquation(infixExpression);
        assertThat(equationId).isEqualTo(1);

        List<Map<String, String>> allEquations = equationService.getAllEquations();
        assertThat(allEquations).hasSize(1);
        // assertThat(allEquations.get(0).get("equation")).isEqualTo("3 * (x + (2y -
        // z))");

        EquationEvaluationResponse response = equationService.evaluateEquation(equationId,
                Map.of("x", 1.0, "y", 2.0, "z", 3.0));
        assertThat(response.getResult()).isEqualTo(6.0); // 3 * (1 + (4 - 3)) = 6
    }
}
