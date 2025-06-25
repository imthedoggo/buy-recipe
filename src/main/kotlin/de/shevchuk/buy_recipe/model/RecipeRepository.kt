package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.Recipe

interface RecipeRepository {
    // GET recipe
    suspend fun findById(id: Long): Recipe?
    // GET all recipes
    suspend fun findAll(page: Int, size: Int, tags: List<String>? = null): List<Recipe>
    // POST recipe
    suspend fun save(recipe: Recipe): Recipe
}
