package com.example.ccl3_app.data

import com.example.ccl3_app.db.ProfileDao
import com.example.ccl3_app.db.ProfileEntity
import kotlinx.coroutines.flow.map

class ProfileRepository(private val profileDao: ProfileDao) {

    // get all profiles ???????????????????????????????????????????????????????????????????????????????????????????
    fun getAllProfiles() =
        profileDao.getAllProfiles().map { profileList ->
            profileList.map { entity ->
                Profile(
                    id = entity.id,
                    name = entity.name,
                    username = entity.username,
                    email = entity.email,
                    password = entity.password,
                    profileImage = entity.profileImage
                )
            }
        }

    // create profile
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

    // update profile
    suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(
            ProfileEntity(
                id = profile.id,
                name = profile.name,
                username = profile.username,
                email = profile.email,
                password = profile.password,
                profileImage = profile.profileImage
            )
        )
    }

    // get single profile
    suspend fun findProfileById(profileId: Int): Profile {
        val entity = profileDao.findProfileById(profileId)
        return Profile(
            id = entity.id,
            name = entity.name,
            username = entity.username,
            email = entity.email,
            password = entity.password,
            profileImage = entity.profileImage
        )
    }

    // delete profile
    suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(
            ProfileEntity(
                id = profile.id,
                name = profile.name,
                username = profile.username,
                email = profile.email,
                password = profile.password,
                profileImage = profile.profileImage
            )
        )
    }
}
