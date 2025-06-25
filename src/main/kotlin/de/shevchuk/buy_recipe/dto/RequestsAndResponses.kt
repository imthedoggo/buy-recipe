package de.shevchuk.buy_recipe.dto

data class RecipeListResponse(
    val recipes: List<RecipeSummary>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int
)

data class RecipeSummary(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val ingredientCount: Int,
    val estimatedCostInCents: Int
)

data class RecipeDetailResponse(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val ingredients: List<RecipeIngredientDetail>,
    val totalCostInCents: Int
)

data class RecipeIngredientDetail(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val priceInCents: Int,
    val totalPriceInCents: Int
)

data class AddRecipeToCartRequest(
    val recipeId: Int,
    val cartId: Int,
    val includeIngredients: List<Int>? = null // Optional: specific product IDs to include
)

data class AddRecipeToCartResponse(
    val success: Boolean,
    val addedItems: List<CartItemAdded>,
    val updatedCartTotal: Int,
    val message: String
)

data class CartItemAdded(
    val productId: Int,
    val productName: String,
    val quantityAdded: Int,
    val isNewItem: Boolean // true if new item, false if quantity was updated
)
