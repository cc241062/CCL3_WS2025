package com.example.ccl3_app.data

import com.example.ccl3_app.database.ProfileDao
import com.example.ccl3_app.database.ProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(private val profileDao: ProfileDao) {


    fun getProfileById(profileId: Int): Flow<Profile?> =
        profileDao.getProfileById(profileId).map { it?.toDomain() }


    fun observeSingleProfile(): Flow<Profile?> =
        profileDao.observeSingleProfile().map { it?.toDomain() }


    suspend fun ensureDefaultProfile() {
        if (profileDao.countProfiles() == 0) {
            addProfile(
                name = "Flippy Wendler",
                username = "Flippy_flips69",
                email = "Flippy@ustp-students.at",
                password = "password123",
                profileImage = ""
            )
        }
    }

    // ✅ One-shot fetch for detail screen
    suspend fun getSingleProfile(): Profile? =
        profileDao.getSingleProfile()?.toDomain()

    suspend fun addProfile(
        name: String,
        username: String,
        email: String,
        password: String,
        profileImage: String
    ) {
        profileDao.addProfile(
            ProfileEntity(
                id = 0,
                name = name,
                username = username,
                email = email,
                password = password,
                profileImage = profileImage
            )
        )
    }

    suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile.toEntity())
    }

    suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(profile.toEntity())
    }
}

// ---- MAPPERS ----
private fun ProfileEntity.toDomain() = Profile(
    id = id,
    name = name,
    username = username,
    email = email,
    password = password,
    profileImage = profileImage
)

private fun Profile.toEntity() = ProfileEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    password = password,
    profileImage = profileImage
)
