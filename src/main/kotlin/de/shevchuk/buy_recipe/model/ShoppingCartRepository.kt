package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.ShoppingCart
import org.springframework.stereotype.Repository

@Repository
interface ShoppingCartRepository {
    suspend fun findById(cartId: Long): ShoppingCart?
    suspend fun save(cart: ShoppingCart): ShoppingCart
    suspend fun findByIdWithItems(id: Long): ShoppingCart?
    suspend fun updateTotal(cartId: Long, newTotal: Int)
}