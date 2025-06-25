package de.shevchuk.buy_recipe.integrationtests

import de.shevchuk.buy_recipe.dto.CreateRecipeIngredient
import de.shevchuk.buy_recipe.dto.CreateRecipeRequest
import de.shevchuk.buy_recipe.dto.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeControllerIntegrationTest(@Autowired val webTestClient: WebTestClient) {

    @Test
    fun `create and get recipe`() {
        val createRequest = CreateRecipeRequest(
            name = "Test Recipe",
            tags = listOf(Tag.VEGAN),
            ingredients = listOf(CreateRecipeIngredient(productId = 1, quantity = 2))
        )

        // Create recipe
        val createResponse = webTestClient.post().uri("/api/recipes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.recipeId").exists()
            .returnResult()

        val recipeId = com.jayway.jsonpath.JsonPath.read<Long>(String(createResponse.responseBody!!), "$.recipeId")

        // Get recipe by id
        webTestClient.get().uri("/api/recipes/$recipeId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(recipeId)
            .jsonPath("$.name").isEqualTo("Test Recipe")
    }

    @Test
    fun `get recipes paginated`() {
        webTestClient.get().uri { builder ->
            builder.path("/api/recipes").queryParam("page", 0).queryParam("pageSize", 10).build()
        }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.recipes").isArray
            .jsonPath("$.page").isEqualTo(0)
            .jsonPath("$.pageSize").isEqualTo(10)
    }

    @Test
    fun `get recipe not found`() {
        webTestClient.get().uri("/api/recipes/999999")
            .exchange()
            .expectStatus().isNotFound
    }
} 