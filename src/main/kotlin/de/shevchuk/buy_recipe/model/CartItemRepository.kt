package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.CartItem
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository {
    suspend fun findByCartIdAndProductId(cartId: Long, productId: Long): CartItem?
    suspend fun create(cartId: Long, productId: Long, quantity: Int): CartItem
    suspend fun updateQuantity(id: Long, quantity: Int)
    suspend fun findByCartId(cartId: Long): List<CartItem>
    suspend fun deleteById(itemId: Long)
}
