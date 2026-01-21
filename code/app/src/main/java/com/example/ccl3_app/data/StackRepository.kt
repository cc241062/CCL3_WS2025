package com.example.ccl3_app.data

import com.example.ccl3_app.database.StackEntity
import com.example.ccl3_app.database.StackDao
import kotlinx.coroutines.flow.map

class StackRepository(private val stackDao: StackDao) {

    val stacks = stackDao.getAllStacks().map { list ->
        list.map { entity ->
            Stack(
                id = entity.id,
                name = entity.name,
                color = entity.color,
                emoji = entity.emoji
            )
        }
    }

    fun getAllStacks() = stackDao.getAllStacks().map { list ->
        list.map { entity ->
            Stack(
                id = entity.id,
                name = entity.name,
                color = entity.color,
                emoji = entity.emoji
            )
        }
    }

    suspend fun addStack(name: String, color: String, emoji: String) {
        stackDao.addStack(
            StackEntity(
                id = 0,
                name = name,
                color = color,
                emoji = emoji
            )
        )
    }

    suspend fun updateStack(stack: Stack) {
        stackDao.updateStack(
            StackEntity(
                id = stack.id,
                name = stack.name,
                color = stack.color,
                emoji = stack.emoji
            )
        )
    }

    suspend fun findStackById(stackId: Int): Stack? {
        val entity = stackDao.findStackById(stackId) ?: return null
        return Stack(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            emoji = entity.emoji
        )
    }

    suspend fun deleteStack(stack: Stack) {
        stackDao.deleteStack(
            StackEntity(
                id = stack.id,
                name = stack.name,
                color = stack.color,
                emoji = stack.emoji
            )
        )
    }
    fun observeStack(stackId: Int) =
        stackDao.observeStackById(stackId).map { entity ->
            entity?.let {
                Stack(
                    id = it.id,
                    name = it.name,
                    color = it.color,
                    emoji = it.emoji
                )
            }
        }
}


