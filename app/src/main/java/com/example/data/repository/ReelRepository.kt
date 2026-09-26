package com.example.data.repository

import com.example.data.downloader.SampleReelsData
import com.example.data.local.ReelDao
import com.example.data.model.DownloadStatus
import com.example.data.model.ReelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class ReelRepository(private val reelDao: ReelDao) {

    val allReels: Flow<List<ReelEntity>> = reelDao.getAllReels()
    val favoriteReels: Flow<List<ReelEntity>> = reelDao.getFavoriteReels()
    val totalStorageUsed: Flow<Long?> = reelDao.getTotalStorageUsed()

    fun searchReels(query: String): Flow<List<ReelEntity>> {
        return if (query.isBlank()) {
            reelDao.getAllReels()
        } else {
            reelDao.searchReels(query)
        }
    }

    fun getReelsByCategory(category: String): Flow<List<ReelEntity>> {
        return if (category == "All") {
            reelDao.getAllReels()
        } else {
            reelDao.getReelsByCategory(category)
        }
    }

    suspend fun insertReel(reel: ReelEntity): Long = withContext(Dispatchers.IO) {
        reelDao.insertReel(reel)
    }

    suspend fun updateReel(reel: ReelEntity) = withContext(Dispatchers.IO) {
        reelDao.updateReel(reel)
    }

    suspend fun updateProgress(id: Long, progress: Int, status: DownloadStatus) = withContext(Dispatchers.IO) {
        reelDao.updateProgress(id, progress, status.name)
    }

    suspend fun toggleFavorite(id: Long, current: Boolean) = withContext(Dispatchers.IO) {
        reelDao.updateFavorite(id, !current)
    }

    suspend fun deleteReel(reel: ReelEntity) = withContext(Dispatchers.IO) {
        // Delete local video file if present
        if (reel.localFilePath.isNotEmpty()) {
            try {
                val file = File(reel.localFilePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // Ignore file deletion error
            }
        }
        reelDao.deleteReel(reel)
    }

    suspend fun populateInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (reelDao.getReelCount() == 0) {
            // Seed a few demo saved reels so user can immediately experience playback & library
            val initial = listOf(
                ReelEntity(
                    title = "Cyber City Neon Night Lights",
                    author = "@tokyo_vibes",
                    originalUrl = "https://instagram.com/reel/C8xK9mP_cyber",
                    downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600&q=80",
                    fileSizeBytes = 15_420_000L,
                    durationSeconds = 15,
                    status = DownloadStatus.COMPLETED.name,
                    progress = 100,
                    category = "Cinematic",
                    isFavorite = true,
                    format = "MP4 • 1080p HD"
                ),
                ReelEntity(
                    title = "Supercar Drift & Turbo Spool",
                    author = "@speed_beasts",
                    originalUrl = "https://tiktok.com/@speed/video/7234567890",
                    downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?w=600&q=80",
                    fileSizeBytes = 12_890_000L,
                    durationSeconds = 15,
                    status = DownloadStatus.COMPLETED.name,
                    progress = 100,
                    category = "Motorsport",
                    isFavorite = false,
                    format = "MP4 • 1080p HD"
                ),
                ReelEntity(
                    title = "Sizzling Gourmet Street Food",
                    author = "@chef_master",
                    originalUrl = "https://instagram.com/reel/C9yN8qF_streetfood",
                    downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80",
                    fileSizeBytes = 18_100_000L,
                    durationSeconds = 15,
                    status = DownloadStatus.COMPLETED.name,
                    progress = 100,
                    category = "Food",
                    isFavorite = true,
                    format = "MP4 • 1080p HD"
                )
            )
            for (item in initial) {
                reelDao.insertReel(item)
            }
        }
    }
}
