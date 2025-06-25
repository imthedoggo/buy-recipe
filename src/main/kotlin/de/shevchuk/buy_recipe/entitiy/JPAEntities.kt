package de.shevchuk.buy_recipe.entitiy

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "recipes")
data class RecipeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String
)

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(name = "price_in_cents", nullable = false)
    val priceInCents: Int
)

@Entity
@Table(name = "recipes_products")
data class RecipeProductEntity(
    @EmbeddedId
    val id: RecipeProductId,

    @Column(nullable = false)
    val quantity: Int
) {
    constructor(recipeId: Long, productId: Long, quantity: Int) : this(
        RecipeProductId(recipeId, productId),
        quantity
    )
}

@Embeddable
data class RecipeProductId(
    @Column(name = "recipe_id")
    val recipeId: Long,

    @Column(name = "product_id")
    val productId: Long
) : Serializable

@Entity
@Table(name = "carts")
data class CartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "total_in_cents", nullable = false)
    val totalInCents: Int
)

@Entity
@Table(name = "cart_items")
data class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "cart_id", nullable = false)
    val cartId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val quantity: Int
)