package de.shevchuk.buy_recipe.controller

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.service.ShoppingCartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/carts")
class ShoppingCartController(
    private val cartService: ShoppingCartService
) {
    @GetMapping("/{id}")
    fun getCart(@PathVariable id: Long): ResponseEntity<ShoppingCart> {
        val cart = cartService.getCart(id)
        return if (cart != null) {
            ResponseEntity.ok(cart)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/add-recipe")
    fun addRecipeToCart(@RequestBody request: AddRecipeToCartRequest): AddRecipeToCartResponse {
        return cartService.addRecipeToCart(request)
    }

    @DeleteMapping("/delete-recipe")
    fun deleteRecipeFromCart(@RequestBody request: RemoveRecipeFromCartRequest): RemoveRecipeFromCartResponse {
        return cartService.removeRecipeFromCart(request)
    }
}
