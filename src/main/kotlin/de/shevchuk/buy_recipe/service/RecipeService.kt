package de.shevchuk.buy_recipe.service

import de.shevchuk.buy_recipe.dto.Recipe
import de.shevchuk.buy_recipe.model.ProductRepository
import de.shevchuk.buy_recipe.model.RecipeRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val productRepository: ProductRepository
) {


}