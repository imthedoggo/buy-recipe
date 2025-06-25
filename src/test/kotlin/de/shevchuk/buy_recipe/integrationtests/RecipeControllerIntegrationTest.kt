package de.shevchuk.buy_recipe.integrationtests

import de.shevchuk.buy_recipe.dto.CreateRecipeIngredient
import de.shevchuk.buy_recipe.dto.CreateRecipeRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.MvcResult
import com.fasterxml.jackson.databind.ObjectMapper

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeControllerIntegrationTest(
    @Autowired val mvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `create and get recipe`() {
        val createRequest = CreateRecipeRequest(
            name = "Test Recipe",
            ingredients = listOf(CreateRecipeIngredient(productId = 1, quantity = 2))
        )

        // Create recipe
        val createResult: MvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.recipeId").exists())
            .andReturn()

        val responseJson = createResult.response.contentAsString
        val recipeId = com.jayway.jsonpath.JsonPath.read<Long>(responseJson, "$.recipeId")

        // Get recipe by id
        mvc.perform(MockMvcRequestBuilders.get("/api/recipes/$recipeId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(recipeId))
            .andExpect(jsonPath("$.name").value("Test Recipe"))
    }

    @Test
    fun `get recipes paginated`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/api/recipes")
                .param("page", "0")
                .param("pageSize", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.recipes").isArray)
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.pageSize").value(10))
    }

    @Test
    fun `get recipe not found`() {
        mvc.perform(MockMvcRequestBuilders.get("/api/recipes/999999"))
            .andExpect(status().isNotFound)
    }
} 