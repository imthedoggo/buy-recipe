package de.shevchuk.buy_recipe.service

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.model.CartItemRepository
import de.shevchuk.buy_recipe.model.ProductRepository
import de.shevchuk.buy_recipe.model.ShoppingCartRepository
import org.springframework.stereotype.Service

@Service
class ShoppingCartService(
    private val cartRepository: ShoppingCartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val recipeService: RecipeService
) {
    suspend fun addRecipeToCart(request: AddRecipeToCartRequest): AddRecipeToCartResponse {
        val recipe = recipeService.getRecipeDetail(request.recipeId)
            ?: return AddRecipeToCartResponse(
                success = false,
                addedItems = emptyList(),
                updatedCartTotal = 0,
                message = "Recipe not found"
            )

        val cart = cartRepository.findById(request.cartId)
            ?: return AddRecipeToCartResponse(
                success = false,
                addedItems = emptyList(),
                updatedCartTotal = 0,
                message = "Cart not found"
            )

        val ingredientsToAdd = if (request.includeIngredients != null) {
            recipe.ingredients.filter { it.productId in request.includeIngredients }
        } else {
            recipe.ingredients
        }

        val addedItems = mutableListOf<CartItemAdded>()
        var totalAdded = 0

        for (ingredient in ingredientsToAdd) {
            val existingItem = cartItemRepository.findByCartIdAndProductId(
                request.cartId,
                ingredient.productId
            )

            val product = productRepository.findById(ingredient.productId)!!

            if (existingItem != null) {
                // Update existing item quantity
                val newQuantity = existingItem.quantity + ingredient.quantity
                cartItemRepository.updateQuantity(existingItem.id, newQuantity)
                addedItems.add(
                    CartItemAdded(
                        productId = ingredient.productId,
                        productName = ingredient.productName,
                        quantityAdded = ingredient.quantity,
                        isNewItem = false
                    )
                )
            } else {
                // Create new cart item
                cartItemRepository.create(request.cartId, ingredient.productId, ingredient.quantity)
                addedItems.add(
                    CartItemAdded(
                        productId = ingredient.productId,
                        productName = ingredient.productName,
                        quantityAdded = ingredient.quantity,
                        isNewItem = true
                    )
                )
            }

            totalAdded += product.priceInCents * ingredient.quantity
        }

        // Update cart total
        val newCartTotal = cart.totalInCents + totalAdded
        cartRepository.updateTotal(request.cartId, newCartTotal)

        return AddRecipeToCartResponse(
            success = true,
            addedItems = addedItems,
            updatedCartTotal = newCartTotal,
            message = "Recipe ingredients added to cart successfully"
        )
    }

    suspend fun removeRecipeFromCart(request: RemoveRecipeFromCartRequest): RemoveRecipeFromCartResponse {
        val recipe = recipeService.getRecipeDetail(request.recipeId)
            ?: return RemoveRecipeFromCartResponse(
                success = false,
                removedItems = emptyList(),
                updatedCartTotal = 0,
                message = "Recipe not found"
            )

        val cart = cartRepository.findById(request.cartId)
            ?: return RemoveRecipeFromCartResponse(
                success = false,
                removedItems = emptyList(),
                updatedCartTotal = 0,
                message = "Cart not found"
            )

        val ingredientsToRemove = if (request.removeIngredients != null) {
            recipe.ingredients.filter { it.productId in request.removeIngredients }
        } else {
            recipe.ingredients
        }

        val removedItems = mutableListOf<CartItemRemoved>()
        var totalRemoved = 0

        for (ingredient in ingredientsToRemove) {
            val existingItem = cartItemRepository.findByCartIdAndProductId(
                request.cartId,
                ingredient.productId
            ) ?: continue // Skip if item not in cart

            val product = productRepository.findById(ingredient.productId)!!
            val quantityToRemove = minOf(ingredient.quantity, existingItem.quantity)
            val newQuantity = existingItem.quantity - quantityToRemove

            if (newQuantity <= 0) {
                // Remove item completely
                cartItemRepository.deleteById(existingItem.id)
                removedItems.add(
                    CartItemRemoved(
                        productId = ingredient.productId,
                        productName = ingredient.productName,
                        quantityRemoved = quantityToRemove,
                        remainingQuantity = 0,
                        itemCompletelyRemoved = true
                    )
                )
            } else {
                // Update item quantity
                cartItemRepository.updateQuantity(existingItem.id, newQuantity)
                removedItems.add(
                    CartItemRemoved(
                        productId = ingredient.productId,
                        productName = ingredient.productName,
                        quantityRemoved = quantityToRemove,
                        remainingQuantity = newQuantity,
                        itemCompletelyRemoved = false
                    )
                )
            }

            totalRemoved += product.priceInCents * quantityToRemove
        }

        // Update cart total
        val newCartTotal = cart.totalInCents - totalRemoved
        cartRepository.updateTotal(request.cartId, newCartTotal)

        return RemoveRecipeFromCartResponse(
            success = true,
            removedItems = removedItems,
            updatedCartTotal = newCartTotal,
            message = if (removedItems.isEmpty()) {
                "No matching recipe ingredients found in cart"
            } else {
                "Recipe ingredients removed from cart successfully"
            }
        )
    }

    suspend fun getCart(cartId: Long): ShoppingCart? {
        return cartRepository.findByIdWithItems(cartId)
    }
}