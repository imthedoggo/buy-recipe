package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.entitiy.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface RecipeJpaRepository : JpaRepository<RecipeEntity, Long> {
    fun existsByNameIgnoreCase(name: String): Boolean
}

@Repository
interface RecipeProductJpaRepository : JpaRepository<RecipeProductEntity, RecipeProductId> {
    fun findByIdRecipeId(recipeId: Long): List<RecipeProductEntity>
}

@Service
open class RecipeRepository(
    private val recipeJpaRepository: RecipeJpaRepository,
    private val recipeProductJpaRepository: RecipeProductJpaRepository,
    private val productRepository: ProductRepository
) {
    // GET recipe by id
    suspend fun findById(id: Long): Recipe? {
        val entity = recipeJpaRepository.findById(id).orElse(null) ?: return null
        return toRecipe(entity)
    }

    // GET all recipes with pagination and optional tags filter
    suspend fun findAll(page: Int, size: Int, tags: List<String>? = null): List<Recipe> {
        val pageRequest = PageRequest.of(page, size)
        val entities = if (tags.isNullOrEmpty()) {
            recipeJpaRepository.findAll(pageRequest).content
        } else {
            // Filter in-memory for now, or implement a custom query if needed
            recipeJpaRepository.findAll().filter { it.tags.any { tag -> tags.contains(tag) } }
                .drop(page * size).take(size)
        }
        return entities.map { toRecipe(it) }
    }

    suspend fun findByIdWithIngredients(id: Long): Recipe? {
        val entity = recipeJpaRepository.findById(id).orElse(null) ?: return null
        return toRecipe(entity, withIngredients = true)
    }

    suspend fun existsByName(name: String): Boolean = recipeJpaRepository.existsByNameIgnoreCase(name)

    suspend fun save(recipe: Recipe): Recipe {
        val entity = RecipeEntity(
            id = recipe.id,
            name = recipe.name,
            tags = recipe.tags.map { it.name }
        )
        val saved = recipeJpaRepository.save(entity)
        // Ingredients saving logic would go here if needed
        return toRecipe(saved, withIngredients = true)
    }

    suspend fun countAll(): Int = recipeJpaRepository.count().toInt()

    // Helper to map RecipeEntity to Recipe DTO
    private suspend fun toRecipe(entity: RecipeEntity, withIngredients: Boolean = false): Recipe {
        val tags = entity.tags.mapNotNull { tagStr ->
            try { Tag.valueOf(tagStr) } catch (_: Exception) { null }
        }
        val ingredients = if (withIngredients) {
            val recipeProducts = recipeProductJpaRepository.findByIdRecipeId(entity.id)
            recipeProducts.mapNotNull { rp ->
                val product = productRepository.findById(rp.id.productId)
                product?.let { RecipeIngredient(it, rp.quantity) }
            }
        } else emptyList()
        return Recipe(
            id = entity.id,
            name = entity.name,
            tags = tags,
            ingredients = ingredients
        )
    }

    suspend fun addRecipe(request: CreateRecipeRequest): Recipe {
        // Save the recipe entity first (id will be generated)
        val recipeEntity = RecipeEntity(
            name = request.name,
            tags = request.tags.map { it.name }
        )
        val savedRecipe = recipeJpaRepository.save(recipeEntity)

        // Save ingredients in the join table
        request.ingredients.forEach { ingredient ->
            val recipeProduct = RecipeProductEntity(
                recipeId = savedRecipe.id,
                productId = ingredient.productId,
                quantity = ingredient.quantity
            )
            recipeProductJpaRepository.save(recipeProduct)
        }

        // Return the full recipe with ingredients
        return toRecipe(savedRecipe, withIngredients = true)
    }
}

