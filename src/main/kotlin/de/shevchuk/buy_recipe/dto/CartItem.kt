package de.shevchuk.buy_recipe.dto

data class CartItem(
    val id: Long,
    val cartId: Long,
    val productId: Long,
    val quantity: Int,
    val product: Product? = null
)