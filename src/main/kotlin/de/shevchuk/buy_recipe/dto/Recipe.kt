package de.shevchuk.buy_recipe.dto

import de.shevchuk.buy_recipe.Product
import java.time.Instant

data class Recipe(
    val id: Long,
    val name: String,
    val tags: List<Tag>,
    val ingredients: List<RecipeIngredient> = emptyList()

//    val servings: Int,
//    val instructions: List<String>,
//    val instructions: Map<Product, Quantity>,
//    val createdAt: Instant,
//    val updatedAt: Instant
)

data class RecipeIngredient(
    val product: Product,
    val quantity: Int
)


enum class Tag { VEGAN, VEGETARIAN, KETO,  }

