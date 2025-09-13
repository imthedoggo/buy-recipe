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
    fun addRecipeToCart(@RequestBody request: AddRecipeToCartRequest): ResponseEntity<AddRecipeToCartResponse> {
        val response = cartService.addRecipeToCart(request)
        return if (response.success) {
            ResponseEntity.status(201).body(response)
        } else {
            ResponseEntity.status(404).body(response)
        }
    }

    @DeleteMapping("/delete-recipe")
    fun deleteRecipeFromCart(@RequestBody request: RemoveRecipeFromCartRequest): ResponseEntity<RemoveRecipeFromCartResponse> {
        val response = cartService.removeRecipeFromCart(request)
        return if (response.success) {
            ResponseEntity.status(200).body(response)
        } else {
            ResponseEntity.status(404).body(response)
        }
    }
}
