package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Profile
import com.example.ccl3_app.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    fun loadProfile() {
        viewModelScope.launch {
            profileRepository.ensureDefaultProfile()
            _profile.value = profileRepository.getSingleProfile()
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.updateProfile(profile)
            _profile.value = profileRepository.getSingleProfile()
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
            _profile.value = null
        }
    }

}




