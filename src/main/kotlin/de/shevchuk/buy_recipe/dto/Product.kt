package de.shevchuk.buy_recipe.dto

data class Product(
    val id: Long,
    val name: String,
    val priceInCents: Int,
    val tags: List<Tag>
)