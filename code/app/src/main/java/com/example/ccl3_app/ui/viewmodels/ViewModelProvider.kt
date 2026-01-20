package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccl3_app.OopsApplication

object ViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            HomeViewModel(
                oopsApplication.recipeRepository,
                oopsApplication.stackRepository
            )
        }


        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            ProfileViewModel(oopsApplication.profileRepository)
        }

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            ProfileDetailViewModel(oopsApplication.profileRepository)
        }

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            RecipeFormsViewModel(oopsApplication.recipeRepository)
        }

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            RecipeDetailViewModel(oopsApplication.recipeRepository)
        }

        /*
        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            QuestViewModel(oopsApplication.questRepository)
        }
        */
    }
}