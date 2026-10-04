package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GameRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<GameRecordEntity>)

    @Query("SELECT * FROM game_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records ORDER BY date DESC LIMIT 20")
    fun getRecentRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT MAX(score) FROM game_records")
    fun getHighScore(): Flow<Int?>

    @Query("SELECT MAX(level) FROM game_records")
    fun getHighestLevel(): Flow<Int?>

    @Query("SELECT MAX(snakeLength) FROM game_records")
    fun getLongestSnake(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM game_records")
    fun getTotalGames(): Flow<Int>

    @Query("SELECT SUM(foodCollected) FROM game_records")
    fun getTotalFoodCollected(): Flow<Int?>

    @Query("SELECT MAX(gameDurationSeconds) FROM game_records")
    fun getBestGameTime(): Flow<Int?>

    @Query("DELETE FROM game_records")
    suspend fun clearAllRecords()
}
