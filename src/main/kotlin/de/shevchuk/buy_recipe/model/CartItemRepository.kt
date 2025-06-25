package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.CartItem

interface CartItemRepository {
    suspend fun findByCartIdAndProductId(cartId: Int, productId: Int): CartItem?
    suspend fun create(cartId: Int, productId: Int, quantity: Int): CartItem
    suspend fun updateQuantity(id: Int, quantity: Int)
    suspend fun findByCartId(cartId: Int): List<CartItem>
}