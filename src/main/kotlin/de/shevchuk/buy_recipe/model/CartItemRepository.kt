package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.CartItem
import de.shevchuk.buy_recipe.entitiy.CartItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface CartItemJpaRepository : JpaRepository<CartItemEntity, Long> {
    fun findByCartIdAndProductId(cartId: Long, productId: Long): CartItemEntity?
    fun findByCartId(cartId: Long): List<CartItemEntity>
}

@Service
open class CartItemRepository(
    private val cartItemJpaRepository: CartItemJpaRepository,
    private val productRepository: ProductRepository
) {
    suspend fun findByCartIdAndProductId(cartId: Long, productId: Long): CartItem? {
        val entity = cartItemJpaRepository.findByCartIdAndProductId(cartId, productId) ?: return null
        return toCartItem(entity)
    }

    suspend fun create(cartId: Long, productId: Long, quantity: Int): CartItem {
        val entity = CartItemEntity(
            cartId = cartId,
            productId = productId,
            quantity = quantity
        )
        val saved = cartItemJpaRepository.save(entity)
        return toCartItem(saved)
    }

    suspend fun updateQuantity(id: Long, quantity: Int) {
        val entity = cartItemJpaRepository.findById(id).orElse(null) ?: return
        val updated = entity.copy(quantity = quantity)
        cartItemJpaRepository.save(updated)
    }

    suspend fun findByCartId(cartId: Long): List<CartItem> {
        return cartItemJpaRepository.findByCartId(cartId).map { toCartItem(it) }
    }

    suspend fun deleteById(itemId: Long) {
        cartItemJpaRepository.deleteById(itemId)
    }

    private suspend fun toCartItem(entity: CartItemEntity): CartItem {
        val product = productRepository.findById(entity.productId)
        return CartItem(
            id = entity.id,
            cartId = entity.cartId,
            productId = entity.productId,
            quantity = entity.quantity,
            product = product
        )
    }
}
