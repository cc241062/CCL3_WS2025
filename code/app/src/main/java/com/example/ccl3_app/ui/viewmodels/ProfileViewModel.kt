package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Profile
import com.example.ccl3_app.data.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val profiles = profileRepository.getAllProfiles()

    fun addProfile(
        name: String,
        username: String,
        email: String,
        password: String,
        profileImage: String
    ) {
        viewModelScope.launch {
            profileRepository.addProfile(
                name,
                username,
                email,
                password,
                profileImage
            )
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }
}
