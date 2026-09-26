package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
    IDLE,
    FETCHING_METADATA,
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    PAUSED
}

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String = "Creator",
    val originalUrl: String,
    val downloadUrl: String,
    val thumbnailUrl: String = "",
    val localFilePath: String = "",
    val fileSizeBytes: Long = 0L,
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = DownloadStatus.COMPLETED.name,
    val progress: Int = 100,
    val category: String = "General",
    val isFavorite: Boolean = false,
    val format: String = "MP4 • 1080p"
)
