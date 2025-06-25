package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.entitiy.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface CartJpaRepository : JpaRepository<CartEntity, Long>

@Repository
interface CartItemJpaRepository : JpaRepository<CartItemEntity, Long> {
    fun findByCartId(cartId: Long): List<CartItemEntity>
}

@Service
open class ShoppingCartRepository(
    private val cartJpaRepository: CartJpaRepository,
    private val cartItemJpaRepository: CartItemJpaRepository,
    private val productRepository: ProductRepository
) {
    suspend fun findById(cartId: Long): ShoppingCart? {
        val cart = cartJpaRepository.findById(cartId).orElse(null) ?: return null
        return ShoppingCart(
            id = cart.id,
            totalInCents = cart.totalInCents
        )
    }

    suspend fun findByIdWithItems(id: Long): ShoppingCart? {
        val cart = cartJpaRepository.findById(id).orElse(null) ?: return null
        val items = cartItemJpaRepository.findByCartId(id).map { toCartItem(it) }
        return ShoppingCart(
            id = cart.id,
            totalInCents = cart.totalInCents,
            items = items
        )
    }

    suspend fun save(cart: ShoppingCart): ShoppingCart {
        val entity = CartEntity(
            id = cart.id,
            totalInCents = cart.totalInCents
        )
        val saved = cartJpaRepository.save(entity)
        return ShoppingCart(
            id = saved.id,
            totalInCents = saved.totalInCents
        )
    }

    suspend fun updateTotal(cartId: Long, newTotal: Int) {
        val cart = cartJpaRepository.findById(cartId).orElse(null) ?: return
        val updated = cart.copy(totalInCents = newTotal)
        cartJpaRepository.save(updated)
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