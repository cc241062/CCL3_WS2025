package com.example.ccl3_app.data

import com.example.ccl3_app.database.ProfileDao
import com.example.ccl3_app.database.ProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(private val profileDao: ProfileDao) {

    fun getAllProfiles(): Flow<List<Profile>> =
        profileDao.getAllProfiles().map { list -> list.map { it.toDomain() } }

    // NEW: observe one profile for compose screens
    fun getProfileById(profileId: Int): Flow<Profile?> =
        profileDao.getProfileById(profileId).map { it?.toDomain() }

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

    // keep if you still want one-shot fetch
    suspend fun findProfileById(profileId: Int): Profile {
        return profileDao.findProfileById(profileId).toDomain()
    }

    suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(profile.toEntity())
    }
}

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
