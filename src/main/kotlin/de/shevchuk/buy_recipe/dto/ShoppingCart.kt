package de.shevchuk.buy_recipe.dto


data class ShoppingCart(
    val id: Long,
    val totalInCents: Long,
    val items: List<CartItem> = emptyList()
)
