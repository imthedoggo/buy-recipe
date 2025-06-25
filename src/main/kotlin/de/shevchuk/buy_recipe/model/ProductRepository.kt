package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.Product
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository {
    suspend fun findByIds(ids: List<Long>): List<Product>
    suspend fun findById(id: Long): Product?
}
