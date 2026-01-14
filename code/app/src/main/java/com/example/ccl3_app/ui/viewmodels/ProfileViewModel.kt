package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Profile
import com.example.ccl3_app.data.ProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {


    // Which profile is currently being shown (for now default to 1)
    private val selectedProfileId = MutableStateFlow(1)

    // Profile the screen should display
    val currentProfile: StateFlow<Profile?> =
        selectedProfileId
            .flatMapLatest { id -> profileRepository.getProfileById(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(value: String) {
        _searchQuery.value = value
    }

    fun setSelectedProfile(id: Int) {
        selectedProfileId.value = id
    }

    fun addProfile(
        name: String,
        username: String,
        email: String,
        password: String,
        profileImage: String
    ) {
        viewModelScope.launch {
            profileRepository.addProfile(name, username, email, password, profileImage)
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }
    fun ensureDefault() {
        viewModelScope.launch {
            profileRepository.ensureDefaultProfile()
        }
    }
}
