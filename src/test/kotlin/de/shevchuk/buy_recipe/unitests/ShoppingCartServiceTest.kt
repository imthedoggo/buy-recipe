package de.shevchuk.buy_recipe.unitests

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.model.CartItemRepository
import de.shevchuk.buy_recipe.model.ProductRepository
import de.shevchuk.buy_recipe.model.ShoppingCartRepository
import de.shevchuk.buy_recipe.service.RecipeService
import de.shevchuk.buy_recipe.service.ShoppingCartService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlinx.coroutines.runBlocking

class ShoppingCartServiceTest {
    private lateinit var cartRepository: ShoppingCartRepository
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var recipeService: RecipeService
    private lateinit var shoppingCartService: ShoppingCartService

    @BeforeEach
    fun setUp() {
        cartRepository = mock()
        cartItemRepository = mock()
        productRepository = mock()
        recipeService = mock()
        shoppingCartService = ShoppingCartService(cartRepository, cartItemRepository, productRepository, recipeService)
    }

    @Test
    fun `addRecipeToCart returns error if recipe not found`() = runBlocking {
        whenever(recipeService.getRecipeDetail(any())).thenReturn(null)
        val request = AddRecipeToCartRequest(1, 1)
        val result = shoppingCartService.addRecipeToCart(request)
        assertFalse(result.success)
        assertEquals("Recipe not found", result.message)
    }

    @Test
    fun `addRecipeToCart returns error if cart not found`() = runBlocking {
        val recipeDetail = RecipeDetailResponse(1, "Test", emptyList(), emptyList(), 0)
        whenever(recipeService.getRecipeDetail(any())).thenReturn(recipeDetail)
        whenever(cartRepository.findById(any())).thenReturn(null)
        val request = AddRecipeToCartRequest(1, 1)
        val result = shoppingCartService.addRecipeToCart(request)
        assertFalse(result.success)
        assertEquals("Cart not found", result.message)
    }

    @Test
    fun `getCart returns cart if found`() = runBlocking {
        val cart = ShoppingCart(1, 100)
        whenever(cartRepository.findByIdWithItems(1)).thenReturn(cart)
        val result = shoppingCartService.getCart(1)
        assertNotNull(result)
        assertEquals(1, result?.id)
    }

    @Test
    fun `removeRecipeFromCart returns error if recipe not found`() = runBlocking {
        whenever(recipeService.getRecipeDetail(any())).thenReturn(null)
        val request = RemoveRecipeFromCartRequest(1, 1)
        val result = shoppingCartService.removeRecipeFromCart(request)
        assertFalse(result.success)
        assertEquals("Recipe not found", result.message)
    }

    @Test
    fun `removeRecipeFromCart returns error if cart not found`() = runBlocking {
        val recipeDetail = RecipeDetailResponse(1, "Test", emptyList(), emptyList(), 0)
        whenever(recipeService.getRecipeDetail(any())).thenReturn(recipeDetail)
        whenever(cartRepository.findById(any())).thenReturn(null)
        val request = RemoveRecipeFromCartRequest(1, 1)
        val result = shoppingCartService.removeRecipeFromCart(request)
        assertFalse(result.success)
        assertEquals("Cart not found", result.message)
    }
} 