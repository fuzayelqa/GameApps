package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val score: Int,
    val level: Int,
    val snakeLength: Int,
    val foodCollected: Int,
    val gameDurationSeconds: Int,
    val date: Long = System.currentTimeMillis(),
    val gameMode: String,
    val isSynced: Boolean = false
)
