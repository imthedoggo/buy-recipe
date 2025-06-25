package de.shevchuk.buy_recipe.integrationtests

import de.shevchuk.buy_recipe.dto.AddRecipeToCartRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingCartControllerIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `get cart not found`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/carts/999999"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `add and remove recipe from cart`() {
        val cartId = 1L
        val recipeId = 1L

        // Add recipe to cart
        val addRequest = AddRecipeToCartRequest(
            recipeId = recipeId,
            cartId = cartId,
            includeIngredients = null
        )
        val addRequestJson = objectMapper.writeValueAsString(addRequest)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/carts/$cartId/add-recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.addedItems").isArray)

        // Remove recipe from cart
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/carts/delete-recipe"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.removedItems").isArray)
    }
} 