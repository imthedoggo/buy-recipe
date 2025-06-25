package de.shevchuk.buy_recipe.model

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.entitiy.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface CartJpaRepository : JpaRepository<CartEntity, Long>

@Service
open class ShoppingCartRepository(
    private val cartJpaRepository: CartJpaRepository,
    private val cartItemJpaRepository: CartItemJpaRepository,
    private val productRepository: ProductRepository
) {
    fun findById(cartId: Long): ShoppingCart? {
        val cart = cartJpaRepository.findById(cartId).orElse(null) ?: return null
        return ShoppingCart(
            id = cart.id,
            totalInCents = cart.totalInCents
        )
    }

    fun findByIdWithItems(id: Long): ShoppingCart? {
        val cart = cartJpaRepository.findById(id).orElse(null) ?: return null
        val items = cartItemJpaRepository.findByCartId(id).map { toCartItem(it) }
        return ShoppingCart(
            id = cart.id,
            totalInCents = cart.totalInCents,
            items = items
        )
    }

    fun save(cart: ShoppingCart): ShoppingCart {
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

    fun updateTotal(cartId: Long, newTotal: Int) {
        val cart = cartJpaRepository.findById(cartId).orElse(null) ?: return
        val updated = cart.copy(totalInCents = newTotal)
        cartJpaRepository.save(updated)
    }

    private fun toCartItem(entity: CartItemEntity): CartItem {
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