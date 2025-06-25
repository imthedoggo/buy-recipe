package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.Product
import de.shevchuk.buy_recipe.dto.Tag
import de.shevchuk.buy_recipe.entitiy.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {
    fun findByIdIn(ids: List<Long>): List<ProductEntity>
}

@Service
open class ProductRepository(
    private val productJpaRepository: ProductJpaRepository
) {
    fun findByIds(ids: List<Long>): List<Product> {
        return productJpaRepository.findByIdIn(ids).map { toProduct(it) }
    }

    fun findById(id: Long): Product? {
        return productJpaRepository.findById(id).orElse(null)?.let { toProduct(it) }
    }

    private fun toProduct(entity: ProductEntity): Product {
        val tags = entity.tags.mapNotNull { tagStr ->
            try { Tag.valueOf(tagStr) } catch (_: Exception) { null }
        }
        return Product(
            id = entity.id,
            name = entity.name,
            priceInCents = entity.priceInCents,
            tags = tags
        )
    }
}
