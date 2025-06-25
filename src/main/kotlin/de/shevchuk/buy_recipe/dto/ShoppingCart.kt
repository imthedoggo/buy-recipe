package de.shevchuk.buy_recipe.dto


data class ShoppingCart(
    val id: Long,
    val totalInCents: Int,
    val items: List<CartItem> = emptyList()
)
