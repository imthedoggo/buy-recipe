package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.Product

interface ProductRepository {
    suspend fun findByIds(ids: List<Int>): List<Product>
    suspend fun findById(id: Int): Product?
}
