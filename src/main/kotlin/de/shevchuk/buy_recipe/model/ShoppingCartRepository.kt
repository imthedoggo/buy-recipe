package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.ShoppingCart

interface ShoppingCartRepository {
    suspend fun findById(userId: Long): ShoppingCart?
    suspend fun save(cart: ShoppingCart): ShoppingCart
//    suspend fun findByIdWithItems(id: Int): Cart?
//    suspend fun updateTotal(cartId: Int, newTotal: Int)
}