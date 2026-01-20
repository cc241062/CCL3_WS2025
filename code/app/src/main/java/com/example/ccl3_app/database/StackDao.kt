package com.example.ccl3_app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ccl3_app.database.StackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStack(stackEntity: StackEntity)

    @Update
    suspend fun updateStack(stackEntity: StackEntity)

    @Delete
    suspend fun deleteStack(stackEntity: StackEntity)

    @Query("SELECT * FROM stacks WHERE id = :id")
    suspend fun findStackById(id: Int): StackEntity?

    @Query("SELECT * FROM stacks")
    fun getAllStacks(): Flow<List<StackEntity>>


}
