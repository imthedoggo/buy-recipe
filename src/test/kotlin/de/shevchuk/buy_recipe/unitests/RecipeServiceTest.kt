package de.shevchuk.buy_recipe.unitests

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.model.ProductRepository
import de.shevchuk.buy_recipe.model.RecipeRepository
import de.shevchuk.buy_recipe.service.RecipeService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
//import org.mockito.kotlin.any
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.whenever
//import kotlinx.coroutines.runBlocking

class RecipeServiceTest {
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var recipeService: RecipeService

    @BeforeEach
    fun setUp() {
        recipeRepository = mock()
        productRepository = mock()
        recipeService = RecipeService(recipeRepository, productRepository)
    }

    @Test
    fun `getRecipes returns paginated recipes`() = runBlocking {
        val recipe = Recipe(1, "Test", listOf(Tag.VEGAN), emptyList())
        whenever(recipeRepository.findAll(any(), any(), any())).thenReturn(listOf(recipe))
        whenever(recipeRepository.countAll()).thenReturn(1)
        whenever(recipeRepository.findByIdWithIngredients(1)).thenReturn(recipe)

        val result = recipeService.getRecipes()
        assertEquals(1, result.totalCount)
        assertEquals(1, result.recipes.size)
        assertEquals("Test", result.recipes[0].name)
    }

    @Test
    fun `getRecipeDetail returns details if found`() = runBlocking {
        val product = Product(1, "Apple", 100, listOf(Tag.VEGAN))
        val ingredient = RecipeIngredient(product, 2)
        val recipe = Recipe(1, "Test", listOf(Tag.VEGAN), listOf(ingredient))
        whenever(recipeRepository.findByIdWithIngredients(1)).thenReturn(recipe)

        val result = recipeService.getRecipeDetail(1)
        assertNotNull(result)
        assertEquals("Test", result?.name)
        assertEquals(1, result?.ingredients?.size)
        assertEquals(200, result?.totalCostInCents)
    }

    @Test
    fun `createRecipe returns error for duplicate name`() = runBlocking {
        val request = CreateRecipeRequest("Test", listOf(Tag.VEGAN), listOf(CreateRecipeIngredient(1, 2)))
        whenever(recipeRepository.existsByName("Test")).thenReturn(true)
        whenever(productRepository.findByIds(any())).thenReturn(listOf(Product(1, "Apple", 100, listOf(Tag.VEGAN))))

        val result = recipeService.createRecipe(request)
        assertFalse(result.success)
        assertTrue(result.errors.any { it.contains("already exists") })
    }
} 