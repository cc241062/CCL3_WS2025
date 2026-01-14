package com.example.ccl3_app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    fun getProfileById(id: Int): Flow<ProfileEntity?>

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun countProfiles(): Int

    @Query("SELECT * FROM profiles ORDER BY id ASC LIMIT 1")
    suspend fun getSingleProfile(): ProfileEntity?
    @Query("SELECT * FROM profiles ORDER BY id ASC LIMIT 1")
    fun observeSingleProfile(): Flow<ProfileEntity?>


}
