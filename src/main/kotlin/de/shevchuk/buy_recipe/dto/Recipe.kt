package de.shevchuk.buy_recipe.dto

data class Recipe(
    val id: Long,
    val name: String,
    val tags: List<Tag> = emptyList(),
    val ingredients: List<RecipeIngredient> = emptyList()
)

data class RecipeIngredient(
    val product: Product,
    val quantity: Int
)

enum class Tag { VEGAN, VEGETARIAN, KETO,  }
