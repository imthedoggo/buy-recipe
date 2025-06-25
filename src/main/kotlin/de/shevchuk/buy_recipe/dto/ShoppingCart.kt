package de.shevchuk.buy_recipe.dto

import de.shevchuk.buy_recipe.CartItem

data class ShoppingCart(
    val id: Long,
    val totalInCents: Long,
    val items: List<CartItem> = emptyList()
//    val items: MutableList<CartItem> = mutableListOf(),
//    val createdAt: Instant,
//    val updatedAt: Instant
)