package com.example.ui.viewmodel

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.downloader.ParsedReelInfo
import com.example.data.downloader.ReelDownloader
import com.example.data.downloader.ReelParser
import com.example.data.downloader.SampleReel
import com.example.data.model.DownloadStatus
import com.example.data.model.ReelEntity
import com.example.data.repository.ReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class ReelViewModel(
    private val repository: ReelRepository,
    private val downloader: ReelDownloader
) : ViewModel() {

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedQuality = MutableStateFlow("1080p HD")
    val selectedQuality: StateFlow<String> = _selectedQuality.asStateFlow()

    private val _isInspecting = MutableStateFlow(false)
    val isInspecting: StateFlow<Boolean> = _isInspecting.asStateFlow()

    private val _parsedInfo = MutableStateFlow<ParsedReelInfo?>(null)
    val parsedInfo: StateFlow<ParsedReelInfo?> = _parsedInfo.asStateFlow()

    private val _activeDownloadProgress = MutableStateFlow<ReelDownloader.DownloadProgress?>(null)
    val activeDownloadProgress: StateFlow<ReelDownloader.DownloadProgress?> = _activeDownloadProgress.asStateFlow()

    private val _activeDownloadingTitle = MutableStateFlow<String?>(null)
    val activeDownloadingTitle: StateFlow<String?> = _activeDownloadingTitle.asStateFlow()

    private val _nowPlayingReel = MutableStateFlow<ReelEntity?>(null)
    val nowPlayingReel: StateFlow<ReelEntity?> = _nowPlayingReel.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _clipboardDetectedUrl = MutableStateFlow<String?>(null)
    val clipboardDetectedUrl: StateFlow<String?> = _clipboardDetectedUrl.asStateFlow()

    val totalStorageUsed: StateFlow<Long> = repository.totalStorageUsed
        .combine(MutableStateFlow(0L)) { used, _ -> used ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val reels: StateFlow<List<ReelEntity>> = combine(
        repository.allReels,
        _searchQuery,
        _selectedCategory
    ) { all, query, category ->
        all.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.author.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                "All" -> true
                "Favorites" -> item.isFavorite
                else -> item.category.equals(category, ignoreCase = true)
            }
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    fun onUrlChange(newUrl: String) {
        _urlInput.value = newUrl
        if (newUrl.isNotBlank() && (newUrl.contains("http://") || newUrl.contains("https://"))) {
            _parsedInfo.value = ReelParser.parse(newUrl)
        } else {
            _parsedInfo.value = null
        }
    }

    fun inspectUrl() {
        val current = _urlInput.value.trim()
        if (current.isBlank()) {
            _snackbarMessage.value = "Please paste or enter a valid reel URL"
            return
        }
        _isInspecting.value = true
        _parsedInfo.value = ReelParser.parse(current)
        _isInspecting.value = false
    }

    fun pasteFromClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val text = item?.text?.toString()?.trim()
            if (!text.isNullOrEmpty() && (text.startsWith("http://") || text.startsWith("https://"))) {
                _urlInput.value = text
                _parsedInfo.value = ReelParser.parse(text)
                _snackbarMessage.value = "Link pasted successfully!"
                _clipboardDetectedUrl.value = null
            } else {
                _snackbarMessage.value = "No valid URL found in clipboard"
            }
        }
    }

    fun checkClipboardForReels(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard != null && clipboard.hasPrimaryClip() &&
            clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
        ) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val text = item?.text?.toString()?.trim()
            if (!text.isNullOrEmpty() &&
                (text.startsWith("http://") || text.startsWith("https://")) &&
                (text.contains("reel") || text.contains("tiktok") || text.contains("short") || text.contains(".mp4"))
            ) {
                if (_urlInput.value != text) {
                    _clipboardDetectedUrl.value = text
                }
            }
        }
    }

    fun dismissClipboardPrompt() {
        _clipboardDetectedUrl.value = null
    }

    fun clearUrlInput() {
        _urlInput.value = ""
        _parsedInfo.value = null
    }

    fun setQuality(quality: String) {
        _selectedQuality.value = quality
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startDownload(
        urlToDownload: String,
        title: String,
        author: String = "@creator",
        category: String = "General",
        thumbUrl: String = ""
    ) {
        if (urlToDownload.isBlank()) {
            _snackbarMessage.value = "Enter a valid URL first!"
            return
        }

        viewModelScope.launch {
            _activeDownloadingTitle.value = title

            // First create queued record in DB
            val newReel = ReelEntity(
                title = title,
                author = author,
                originalUrl = urlToDownload,
                downloadUrl = urlToDownload,
                thumbnailUrl = thumbUrl,
                status = DownloadStatus.DOWNLOADING.name,
                progress = 10,
                category = category,
                format = "MP4 • ${_selectedQuality.value}"
            )
            val insertedId = repository.insertReel(newReel)

            downloader.downloadReel(
                sourceUrl = urlToDownload,
                suggestedTitle = title
            ) { progress ->
                _activeDownloadProgress.value = progress
                repository.updateProgress(insertedId, progress.progressPercent, progress.status)

                if (progress.status == DownloadStatus.COMPLETED) {
                    val updated = newReel.copy(
                        id = insertedId,
                        localFilePath = progress.localFilePath ?: "",
                        thumbnailUrl = progress.localThumbPath ?: thumbUrl,
                        fileSizeBytes = progress.totalBytes,
                        status = DownloadStatus.COMPLETED.name,
                        progress = 100
                    )
                    repository.updateReel(updated)
                    _snackbarMessage.value = "Reel saved to library successfully!"
                    _activeDownloadingTitle.value = null
                    _urlInput.value = ""
                    _parsedInfo.value = null
                } else if (progress.status == DownloadStatus.FAILED) {
                    _snackbarMessage.value = progress.errorMessage ?: "Download failed"
                    _activeDownloadingTitle.value = null
                }
            }
        }
    }

    fun downloadSample(sample: SampleReel) {
        startDownload(
            urlToDownload = sample.videoUrl,
            title = sample.title,
            author = sample.author,
            category = sample.category,
            thumbUrl = sample.thumbnailUrl
        )
    }

    fun toggleFavorite(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(reel.id, reel.isFavorite)
        }
    }

    fun deleteReel(reel: ReelEntity) {
        viewModelScope.launch {
            repository.deleteReel(reel)
            _snackbarMessage.value = "Reel deleted"
        }
    }

    fun playReel(reel: ReelEntity) {
        _nowPlayingReel.value = reel
    }

    fun closePlayer() {
        _nowPlayingReel.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun clearStorageCache(context: Context) {
        viewModelScope.launch {
            try {
                val cacheDir = context.cacheDir
                cacheDir.deleteRecursively()
                _snackbarMessage.value = "Cache cleaned successfully"
            } catch (e: Exception) {
                _snackbarMessage.value = "Unable to clean cache"
            }
        }
    }
}

class ReelViewModelFactory(
    private val repository: ReelRepository,
    private val downloader: ReelDownloader
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReelViewModel::class.java)) {
            return ReelViewModel(repository, downloader) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
