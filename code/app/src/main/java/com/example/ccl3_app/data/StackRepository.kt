package com.example.ccl3_app.data

import com.example.ccl3_app.database.StackEntity
import com.example.ccl3_app.database.StackDao
import kotlinx.coroutines.flow.map

class StackRepository(private val stackDao: StackDao) {

    val stacks = stackDao.getAllStacks().map { stackList ->
        stackList.map { entity ->
            Stack(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                color = entity.color
            )
        }
    }

    fun getAllStacks() = stackDao.getAllStacks().map { stackList ->
        stackList.map { entity ->
            Stack(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                color = entity.color
            )
        }
    }

    suspend fun addStack(name: String, description: String, color: String) {
        stackDao.addStack(
            StackEntity(
                id = 0,
                name = name,
                description = description,
                color = color
            )
        )
    }

    suspend fun updateStack(stack: Stack) {
        stackDao.updateStack(
            StackEntity(
                id = stack.id,
                name = stack.name,
                description = stack.description,
                color = stack.color
            )
        )
    }

    suspend fun findStackById(stackId: Int): Stack {
        val stackEntity = stackDao.findStackById(stackId)
        return Stack(
            id = stackEntity.id,
            name = stackEntity.name,
            description = stackEntity.description,
            color = stackEntity.color
        )
    }

    suspend fun deleteStack(stack: Stack) {
        stackDao.deleteStack(
            StackEntity(
                id = stack.id,
                name = stack.name,
                description = stack.description,
                color = stack.color
            )
        )
    }
}
