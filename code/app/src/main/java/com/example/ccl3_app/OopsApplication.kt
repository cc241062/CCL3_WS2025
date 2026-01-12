package com.example.ccl3_app

import android.app.Application
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase

class OopsApplication : Application() {

    //by lazy --> only run when accessing the object
    //run only once
    val recipeRepository by lazy {
        val recipeDao = OopsDatabase.getDatabase(this).recipeDao()
        RecipeRepository( recipeDao = recipeDao )
    }

    val profileRepository by lazy {
        val profileRepository = OopsDatabase.getDatabase(this).profileDao()
        ProfileRepository(profileDao = profileDao)
    }

    val stackRepository by lazy {
        val stackRepository = OopsDatabase.getDatabase(this).stackDao()
        StackRepository(stackDao = stackDao)
    }
}