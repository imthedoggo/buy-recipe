package de.shevchuk.buy_recipe.controller

import de.shevchuk.buy_recipe.*
import de.shevchuk.buy_recipe.dto.RecipeDetailResponse
import de.shevchuk.buy_recipe.dto.RecipeListResponse
import de.shevchuk.buy_recipe.service.RecipeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val recipeService: RecipeService,
) {

    @GetMapping("/{id}")
    suspend fun getRecipe(@PathVariable id: Long): ResponseEntity<RecipeDetailResponse> {
        val recipe = recipeService.getRecipeDetail(id)
        return if (recipe != null) {
            ResponseEntity.ok(recipe)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    suspend fun getRecipes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(required = false) tags: List<String>?,
    ): RecipeListResponse {
        return recipeService.getRecipes(page, pageSize, tags)
    }

    @PostMapping("/{id}/create")
    suspend fun createRecipe(): ResponseEntity<AddToCartResponse> {}
}
