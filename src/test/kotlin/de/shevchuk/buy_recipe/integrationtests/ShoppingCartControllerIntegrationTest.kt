package de.shevchuk.buy_recipe.integrationtests

import de.shevchuk.buy_recipe.dto.AddRecipeToCartRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerIntegrationTest(@Autowired val webTestClient: WebTestClient) {

    @Test
    fun `get cart not found`() {
        webTestClient.get().uri("/api/carts/999999")
            .exchange()
            .expectStatus().isNotFound
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
        webTestClient.post().uri("/api/carts/$cartId/add-recipe")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(addRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.addedItems").isArray

        // Remove recipe from cart
        webTestClient.delete().uri("/api/carts/delete-recipe")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.removedItems").isArray
    }
} 