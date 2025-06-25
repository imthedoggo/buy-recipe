package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.Recipe
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository {
    // GET recipe
    suspend fun findById(id: Long): Recipe?
    // GET all recipes
    suspend fun findAll(page: Int, size: Int, tags: List<String>? = null): List<Recipe>
    suspend fun findByIdWithIngredients(id: Long): Recipe?
    suspend fun existsByName(name: String): Boolean
    // POST recipe
    suspend fun save(recipe: Recipe): Recipe

    suspend fun countAll(): Int
}
