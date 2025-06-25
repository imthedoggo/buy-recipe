package de.shevchuk.buy_recipe.unitests

import de.shevchuk.buy_recipe.dto.*
import de.shevchuk.buy_recipe.model.ProductRepository
import de.shevchuk.buy_recipe.model.RecipeRepository
import de.shevchuk.buy_recipe.service.RecipeService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class RecipeServiceTest {
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var recipeService: RecipeService

    @BeforeEach
    fun setUp() {
        recipeRepository = mock(RecipeRepository::class.java)
        productRepository = mock()
        recipeService = RecipeService(recipeRepository, productRepository)
    }

    @Test
    fun `getRecipes returns paginated recipes`(): Unit = runBlocking {
        val recipe = Recipe(1, "Test", listOf(Tag.VEGAN), emptyList())

        runBlocking {
            whenever(recipeRepository.findAll(any(), any(), any())).doReturn(listOf(recipe))
            whenever(recipeRepository.countAll()).doReturn(1)
            whenever(recipeRepository.findByIdWithIngredients(1)).doReturn(recipe)
        }

        val result = recipeService.getRecipes()
        assertEquals(1, result.totalCount)
        assertEquals(1, result.recipes.size)
        assertEquals("Test", result.recipes[0].name)
    }

    @Test
    fun `getRecipeDetail returns details if found`(): Unit = runBlocking {
        val product = Product(1, "Apple", 100, listOf(Tag.VEGAN))
        val ingredient = RecipeIngredient(product, 2)
        val recipe = Recipe(1, "Test", listOf(Tag.VEGAN), listOf(ingredient))

        runBlocking {
            whenever(recipeRepository.findByIdWithIngredients(1)).doReturn(recipe)
        }

        val result = recipeService.getRecipeDetail(1)
        assertNotNull(result)
        assertEquals("Test", result?.name)
        assertEquals(1, result?.ingredients?.size)
        assertEquals(200, result?.totalCostInCents)
    }

    @Test
    fun `createRecipe returns error for duplicate name`(): Unit = runBlocking {
        val request = CreateRecipeRequest("Test", listOf(Tag.VEGAN), listOf(CreateRecipeIngredient(1, 2)))
        val product = Product(1, "Apple", 100, listOf(Tag.VEGAN))

        runBlocking {
            whenever(recipeRepository.existsByName("Test")).doReturn(true)
            whenever(productRepository.findByIds(any())).doReturn(listOf(product))
        }

        val result = recipeService.createRecipe(request)
        assertFalse(result.success)
        assertTrue(result.errors.any { it.contains("already exists") })
    }

    @Test
    fun `createRecipe returns success for valid request`(): Unit = runBlocking {
        val request = CreateRecipeRequest("Test", listOf(Tag.VEGAN), listOf(CreateRecipeIngredient(1, 2)))
        val product = Product(1, "Apple", 100, listOf(Tag.VEGAN))
        val ingredient = RecipeIngredient(product, 2)
        val recipe = Recipe(1, "Test", listOf(Tag.VEGAN), listOf(ingredient))
        val recipeDetail = RecipeDetailResponse(
            id = 1,
            name = "Test",
            tags = listOf(Tag.VEGAN),
            ingredients = listOf(RecipeIngredientDetail(1, "Apple", 2, 100, 200)),
            totalCostInCents = 200
        )

        runBlocking {
            whenever(recipeRepository.existsByName("Test")).thenReturn(false)
            whenever(productRepository.findByIds(any())).thenReturn(listOf(product))
            whenever(recipeRepository.addRecipe(request)).thenReturn(recipe)
            whenever(recipeRepository.findByIdWithIngredients(1)).thenReturn(recipe)
        }

        val result = recipeService.createRecipe(request)
        assertTrue(result.success)
        assertEquals(1, result.recipeId)
        assertEquals("Test", result.recipe?.name)
        assertEquals(1, result.recipe?.ingredients?.size)
        assertEquals(200, result.recipe?.totalCostInCents)
    }
} 