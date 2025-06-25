package de.shevchuk.buy_recipe.dto

data class RecipeListResponse(
    val recipes: List<RecipeSummary>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int
)

data class RecipeSummary(
    val id: Long,
    val name: String,
    val ingredientCount: Int,
    val estimatedCostInCents: Int
)

data class RecipeDetailResponse(
    val id: Long,
    val name: String,
    val ingredients: List<RecipeIngredientDetail>,
    val totalCostInCents: Int
)

data class RecipeIngredientDetail(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val priceInCents: Int,
    val totalPriceInCents: Int
)

data class AddRecipeToCartRequest(
    val recipeId: Long,
    val cartId: Long,
    val includeIngredients: List<Long>? = null // Optional: specific product IDs to include
)

data class AddRecipeToCartResponse(
    val success: Boolean,
    val addedItems: List<CartItemAdded>,
    val updatedCartTotal: Int,
    val message: String
)

data class CartItemAdded(
    val productId: Long,
    val productName: String,
    val quantityAdded: Int,
    val isNewItem: Boolean // true if new item, false if quantity was updated
)

data class RemoveRecipeFromCartRequest(
    val recipeId: Long,
    val cartId: Long,
    val removeIngredients: List<Long>? = null // Optional: specific product IDs to remove
)

data class RemoveRecipeFromCartResponse(
    val success: Boolean,
    val removedItems: List<CartItemRemoved>,
    val updatedCartTotal: Int,
    val message: String
)

data class CartItemRemoved(
    val productId: Long,
    val productName: String,
    val quantityRemoved: Int,
    val remainingQuantity: Int,
    val itemCompletelyRemoved: Boolean // true if item was completely removed from cart
)

data class CreateRecipeRequest(
    val name: String,
    val ingredients: List<CreateRecipeIngredient>
)

data class CreateRecipeIngredient(
    val productId: Long,
    val quantity: Int
)

data class CreateRecipeResponse(
    val success: Boolean,
    val recipeId: Long?,
    val recipe: RecipeDetailResponse?,
    val message: String,
    val errors: List<String> = emptyList()
)