package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE id = :id LIMIT 1")
    fun getReelById(id: Long): Flow<ReelEntity?>

    @Query("SELECT * FROM reels WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchReels(query: String): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE category = :category ORDER BY timestamp DESC")
    fun getReelsByCategory(category: String): Flow<List<ReelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity): Long

    @Update
    suspend fun updateReel(reel: ReelEntity)

    @Delete
    suspend fun deleteReel(reel: ReelEntity)

    @Query("DELETE FROM reels WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE reels SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE reels SET progress = :progress, status = :status WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int, status: String)

    @Query("SELECT COUNT(*) FROM reels")
    suspend fun getReelCount(): Int

    @Query("SELECT SUM(fileSizeBytes) FROM reels WHERE status = 'COMPLETED'")
    fun getTotalStorageUsed(): Flow<Long?>
}
