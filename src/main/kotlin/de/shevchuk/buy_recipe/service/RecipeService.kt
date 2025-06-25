package de.shevchuk.buy_recipe.service

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.model.ProductRepository
import de.shevchuk.buy_recipe.model.RecipeRepository
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val productRepository: ProductRepository
) {
    fun getRecipes(
        page: Int = 0,
        pageSize: Int = 20
    ): RecipeListResponse {
        val recipes = recipeRepository.findAll(page, pageSize)
        val totalCount = recipeRepository.countAll()

        val recipeSummaries = recipes.map { recipe ->
            val ingredients = getRecipeIngredients(recipe.id)
            RecipeSummary(
                id = recipe.id,
                name = recipe.name,
                ingredientCount = ingredients.size,
                estimatedCostInCents = ingredients.sumOf { it.product.priceInCents * it.quantity }
            )
        }

        return RecipeListResponse(
            recipes = recipeSummaries,
            totalCount = totalCount,
            page = page,
            pageSize = pageSize
        )
    }

    fun getRecipeDetail(id: Long): RecipeDetailResponse? {
        val recipe = recipeRepository.findByIdWithIngredients(id) ?: return null

        val ingredientDetails = recipe.ingredients.map { ingredient ->
            RecipeIngredientDetail(
                productId = ingredient.product.id,
                productName = ingredient.product.name,
                quantity = ingredient.quantity,
                priceInCents = ingredient.product.priceInCents,
                totalPriceInCents = ingredient.product.priceInCents * ingredient.quantity
            )
        }

        return RecipeDetailResponse(
            id = recipe.id,
            name = recipe.name,
            ingredients = ingredientDetails,
            totalCostInCents = ingredientDetails.sumOf { it.totalPriceInCents }
        )
    }

    private fun getRecipeIngredients(recipeId: Long): List<RecipeIngredient> {
        val recipe = recipeRepository.findByIdWithIngredients(recipeId) ?: return emptyList()
        return recipe.ingredients
    }

    fun createRecipe(request: CreateRecipeRequest): CreateRecipeResponse {
        // validate
        val errors = mutableListOf<String>()

        if (request.name.isBlank()) {
            errors.add("Recipe name cannot be empty")
        }

        if (request.name.length > 255) {
            errors.add("Recipe name cannot exceed 255 characters")
        }

        if (recipeRepository.existsByName(request.name)) {
            errors.add("Recipe with this name already exists")
        }

        if (request.ingredients.isEmpty()) {
            errors.add("Recipe must have at least one ingredient")
        }

        // Validate ingredients
        val productIds = request.ingredients.map { it.productId }.distinct()
        val existingProducts = productRepository.findByIds(productIds)
        val existingProductIds = existingProducts.map { it.id }.toSet()

        val invalidProductIds = productIds.filter { it !in existingProductIds }
        if (invalidProductIds.isNotEmpty()) {
            errors.add("Invalid product IDs: "+ invalidProductIds.joinToString(", "))
        }

        // Check for duplicate ingredients in request
        val duplicateProductIds = request.ingredients.groupBy { it.productId }
            .filter { it.value.size > 1 }
            .keys
        if (duplicateProductIds.isNotEmpty()) {
            errors.add("Duplicate ingredients found: "+ duplicateProductIds.joinToString(", "))
        }

        // Validate quantities
        request.ingredients.forEach { ingredient ->
            if (ingredient.quantity <= 0) {
                errors.add("Quantity must be positive for product ID "+ ingredient.productId)
            }
        }

        if (errors.isNotEmpty()) {
            return CreateRecipeResponse(
                success = false,
                recipeId = null,
                recipe = null,
                message = "Validation failed",
                errors = errors
            )
        }

        try {
            // Create recipe and ingredients in one step
            val recipe = recipeRepository.addRecipe(request)
            val recipeDetail = getRecipeDetail(recipe.id)
            return CreateRecipeResponse(
                success = true,
                recipeId = recipe.id,
                recipe = recipeDetail,
                message = "Recipe created successfully"
            )
        } catch (e: Exception) {
            return CreateRecipeResponse(
                success = false,
                recipeId = null,
                recipe = null,
                message = "Failed to create recipe: ${e.message}"
            )
        }
    }

}
