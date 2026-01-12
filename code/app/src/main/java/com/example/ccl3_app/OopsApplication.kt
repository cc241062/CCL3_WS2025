package com.example.ccl3_app

import android.app.Application
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase

class OopsApplication : Application() {

    val database by lazy { OopsDatabase.getDatabase(this) }

    val recipeRepository by lazy {
        val recipeDao = database.RecipeDao()
        RecipeRepository(recipeDao)
    }

    val profileRepository by lazy {
        val profileDao = database.ProfileDao()
        ProfileRepository(profileDao)
    }

    val stackRepository by lazy {
        val stackDao = database.StackDao()
        StackRepository(stackDao)
    }
}
