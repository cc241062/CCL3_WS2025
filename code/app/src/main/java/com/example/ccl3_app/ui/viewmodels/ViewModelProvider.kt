package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccl3_app.OopsApplication

object ViewModelProvider {
    val Factory = viewModelFactory {

        //main viewmodel (all contacts or contact overview)
        initializer {

            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            //to link viewmodel to data layer --> repository as a parameter for viewmodel
            HomeViewModel(OopsApplication.recipeRepository)
        }

        //second viewmodel (detail view)
        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            //to link viewmodel to data layer --> repository as a parameter for viewmodel
            ProfileViewModel(OopsApplication.profileRepository)
        }

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            //to link viewmodel to data layer --> repository as a parameter for viewmodel
            ProfileDetailViewModel(OopsApplication.profileRepository)
        }

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            //to link viewmodel to data layer --> repository as a parameter for viewmodel
            RecipeFormsViewModel(OopsApplication.recipeRepository)
        }

        initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            //to link viewmodel to data layer --> repository as a parameter for viewmodel
            RecipeDetailViewModel(OopsApplication.recipeRepository)
        }

        /*initializer {
            val oopsApplication = this[APPLICATION_KEY] as OopsApplication
            //to link viewmodel to data layer --> repository as a parameter for viewmodel
            QuestViewModel(OopsApplication.questRepository)
        }*/

    }
}