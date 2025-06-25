package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.entitiy.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
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
    fun findById(id: Long): Recipe? {
        val entity = recipeJpaRepository.findById(id).orElse(null) ?: return null
        return toRecipe(entity)
    }

    // GET all recipes with pagination
    fun findAll(page: Int, size: Int): List<Recipe> {
        val pageRequest = PageRequest.of(page, size)
        val entities = recipeJpaRepository.findAll(pageRequest).content
        return entities.map { toRecipe(it) }
    }

    fun findByIdWithIngredients(id: Long): Recipe? {
        val entity = recipeJpaRepository.findById(id).orElse(null) ?: return null
        return toRecipe(entity, withIngredients = true)
    }

    fun existsByName(name: String): Boolean = recipeJpaRepository.existsByNameIgnoreCase(name)

    fun save(recipe: Recipe): Recipe {
        val entity = RecipeEntity(
            id = recipe.id,
            name = recipe.name
        )
        val saved = recipeJpaRepository.save(entity)
        return toRecipe(saved, withIngredients = true)
    }

    fun countAll(): Int = recipeJpaRepository.count().toInt()

    // Helper to map RecipeEntity to Recipe DTO
    private fun toRecipe(entity: RecipeEntity, withIngredients: Boolean = false): Recipe {
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
            ingredients = ingredients
        )
    }

    fun addRecipe(request: CreateRecipeRequest): Recipe {
        val recipeEntity = RecipeEntity(
            name = request.name
        )
        val savedRecipe = recipeJpaRepository.save(recipeEntity)
        request.ingredients.forEach { ingredient ->
            val recipeProduct = RecipeProductEntity(
                recipeId = savedRecipe.id,
                productId = ingredient.productId,
                quantity = ingredient.quantity
            )
            recipeProductJpaRepository.save(recipeProduct)
        }
        return toRecipe(savedRecipe, withIngredients = true)
    }
}

