package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.StackRepository

class HomeViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val stackRepository: StackRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(recipeRepository, stackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
