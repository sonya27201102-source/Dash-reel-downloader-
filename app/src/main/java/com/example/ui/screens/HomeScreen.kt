package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.downloader.SampleReel
import com.example.data.downloader.SampleReelsData
import com.example.data.model.DownloadStatus
import com.example.data.model.ReelEntity
import com.example.ui.theme.DashCoralAccent
import com.example.ui.theme.DashCyanAccent
import com.example.ui.theme.DashDarkBg
import com.example.ui.theme.DashDarkBorder
import com.example.ui.theme.DashDarkSurface
import com.example.ui.theme.DashDarkSurfaceVariant
import com.example.ui.theme.DashGreenSuccess
import com.example.ui.theme.DashTextMuted
import com.example.ui.theme.DashTextPrimary
import com.example.ui.theme.DashTextSecondary
import com.example.ui.theme.DashVioletDark
import com.example.ui.theme.DashVioletLight
import com.example.ui.theme.DashVioletPrimary
import com.example.ui.viewmodel.ReelViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: ReelViewModel,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val parsedInfo by viewModel.parsedInfo.collectAsStateWithLifecycle()
    val activeProgress by viewModel.activeDownloadProgress.collectAsStateWithLifecycle()
    val downloadingTitle by viewModel.activeDownloadingTitle.collectAsStateWithLifecycle()
    val clipboardDetectedUrl by viewModel.clipboardDetectedUrl.collectAsStateWithLifecycle()
    val selectedQuality by viewModel.selectedQuality.collectAsStateWithLifecycle()
    val totalStorageUsed by viewModel.totalStorageUsed.collectAsStateWithLifecycle()
    val reels by viewModel.reels.collectAsStateWithLifecycle()

    // Periodically verify clipboard
    LaunchedEffect(Unit) {
        viewModel.checkClipboardForReels(context)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DashDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header with Branding & Stats
        item {
            HeaderSection(
                reelsCount = reels.size,
                totalStorageUsed = totalStorageUsed
            )
        }

        // Clipboard Quick Action Banner
        if (clipboardDetectedUrl != null) {
            item {
                ClipboardDetectionBanner(
                    url = clipboardDetectedUrl!!,
                    onPasteAndDownload = {
                        viewModel.onUrlChange(clipboardDetectedUrl!!)
                        viewModel.startDownload(
                            urlToDownload = clipboardDetectedUrl!!,
                            title = "Reel Clip ${System.currentTimeMillis() % 10000}",
                            author = "@creator"
                        )
                        viewModel.dismissClipboardPrompt()
                    },
                    onDismiss = { viewModel.dismissClipboardPrompt() }
                )
            }
        }

        // Hero Graphic Card
        item {
            HeroBannerCard()
        }

        // URL Input & Download Card
        item {
            DownloadInputCard(
                urlInput = urlInput,
                onUrlChange = { viewModel.onUrlChange(it) },
                onPasteClicked = { viewModel.pasteFromClipboard(context) },
                onClearClicked = { viewModel.clearUrlInput() },
                selectedQuality = selectedQuality,
                onQualitySelected = { viewModel.setQuality(it) },
                onDownloadClicked = {
                    val targetUrl = urlInput.trim()
                    val title = parsedInfo?.title ?: "Dash Reel Clip"
                    val author = parsedInfo?.author ?: "@creator"
                    val category = parsedInfo?.category ?: "Viral"
                    viewModel.startDownload(
                        urlToDownload = targetUrl,
                        title = title,
                        author = author,
                        category = category
                    )
                },
                canDownload = urlInput.isNotBlank() && downloadingTitle == null
            )
        }

        // Active Download Status Card
        if (downloadingTitle != null && activeProgress != null) {
            item {
                ActiveDownloadCard(
                    title = downloadingTitle!!,
                    progress = activeProgress!!
                )
            }
        }

        // Inspected Reel Preview
        if (parsedInfo != null && downloadingTitle == null) {
            item {
                InspectedReelCard(
                    parsedInfo = parsedInfo!!,
                    onStartDownload = {
                        viewModel.startDownload(
                            urlToDownload = parsedInfo!!.directVideoUrl,
                            title = parsedInfo!!.title,
                            author = parsedInfo!!.author,
                            category = parsedInfo!!.category,
                            thumbUrl = parsedInfo!!.thumbnailUrl
                        )
                    }
                )
            }
        }

        // Sample Trending Reels Carousel (1-Tap Test)
        item {
            SampleReelsSection(
                onDownloadSample = { sample ->
                    viewModel.downloadSample(sample)
                },
                onPlaySample = { sample ->
                    viewModel.playReel(
                        ReelEntity(
                            title = sample.title,
                            author = sample.author,
                            originalUrl = sample.videoUrl,
                            downloadUrl = sample.videoUrl,
                            thumbnailUrl = sample.thumbnailUrl,
                            durationSeconds = sample.durationSeconds,
                            category = sample.category,
                            format = "MP4 • 1080p HD"
                        )
                    )
                }
            )
        }

        // Quick 3-Step Guide
        item {
            HowToGuideSection()
        }
    }
}

@Composable
private fun HeaderSection(
    reelsCount: Int,
    totalStorageUsed: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(listOf(DashVioletPrimary, DashCoralAccent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Dash Logo",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Dash",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = " Reel",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DashCoralAccent
                    )
                }
                Text(
                    text = "High-Speed Reel Downloader",
                    fontSize = 11.sp,
                    color = DashTextSecondary
                )
            }
        }

        // Stats badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DashDarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = DashCyanAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$reelsCount Saved",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashTextPrimary
                )
            }
        }
    }
}

@Composable
private fun HeroBannerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DashDarkSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_dash_hero_banner_1790344914942),
                contentDescription = "Dash Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                DashDarkBg.copy(alpha = 0.92f),
                                DashDarkBg.copy(alpha = 0.65f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DashCoralAccent.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "⚡ ULTRA FAST EXTRACTION",
                        color = DashCoralAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Save Reels in 1080p HD",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "No watermark • 100% On-device • Offline Player",
                    color = DashTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ClipboardDetectionBanner(
    url: String,
    onPasteAndDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clipboard_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DashVioletDark.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DashVioletPrimary.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null,
                    tint = DashCyanAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Link detected from clipboard",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = url,
                        fontSize = 11.sp,
                        color = DashTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPasteAndDownload,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DashVioletPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_paste_and_download")
                ) {
                    Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Dismiss",
                        tint = DashTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadInputCard(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onPasteClicked: () -> Unit,
    onClearClicked: () -> Unit,
    selectedQuality: String,
    onQualitySelected: (String) -> Unit,
    onDownloadClicked: () -> Unit,
    canDownload: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("download_input_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "Enter Video / Reel URL",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Text input box
            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                placeholder = {
                    Text(
                        text = "Paste Instagram, TikTok or short link…",
                        color = DashTextMuted,
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    if (urlInput.isNotEmpty()) {
                        IconButton(onClick = onClearClicked) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear input",
                                tint = DashTextSecondary
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onPasteClicked,
                            modifier = Modifier.testTag("btn_paste_input")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard",
                                tint = DashCoralAccent
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DashVioletPrimary,
                    unfocusedBorderColor = DashDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = DashCoralAccent,
                    focusedContainerColor = DashDarkBg,
                    unfocusedContainerColor = DashDarkBg
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("url_text_field")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quality Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quality:",
                    fontSize = 12.sp,
                    color = DashTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1080p HD", "720p", "MP3 Audio").forEach { quality ->
                        val isSelected = selectedQuality == quality
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) DashVioletPrimary.copy(alpha = 0.3f) else DashDarkBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) DashVioletPrimary else DashDarkBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onQualitySelected(quality) }
                        ) {
                            Text(
                                text = quality,
                                color = if (isSelected) Color.White else DashTextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Download Button
            Button(
                onClick = onDownloadClicked,
                enabled = canDownload,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DashCoralAccent,
                    disabledContainerColor = DashDarkSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_download_reel")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = if (canDownload) Color.White else DashTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download Reel",
                        color = if (canDownload) Color.White else DashTextMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveDownloadCard(
    title: String,
    progress: com.example.data.downloader.ReelDownloader.DownloadProgress
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_download_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DashDarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DashVioletPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = DashCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Downloading Reel...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${progress.progressPercent}%",
                    color = DashCoralAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = DashTextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress.progressPercent / 100f },
                color = DashCoralAccent,
                trackColor = DashDarkBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (progress.speedKbps > 0) "${progress.speedKbps} KB/s" else "Extracting stream...",
                    color = DashCyanAccent,
                    fontSize = 11.sp
                )
                Text(
                    text = if (progress.totalBytes > 0) {
                        "${progress.downloadedBytes / (1024 * 1024)}MB / ${progress.totalBytes / (1024 * 1024)}MB"
                    } else {
                        "Streaming"
                    },
                    color = DashTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun InspectedReelCard(
    parsedInfo: com.example.data.downloader.ParsedReelInfo,
    onStartDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = parsedInfo.thumbnailUrl,
                contentDescription = parsedInfo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DashDarkBg)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DashVioletPrimary.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = parsedInfo.platform,
                        color = DashVioletLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = parsedInfo.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = parsedInfo.author,
                    color = DashTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onStartDownload,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DashCoralAccent),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SampleReelsSection(
    onDownloadSample: (SampleReel) -> Unit,
    onPlaySample: (SampleReel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ Trending Demo Reels (1-Tap Test)",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ready to Save",
                color = DashCyanAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(SampleReelsData.list) { sample ->
                SampleReelCard(
                    sample = sample,
                    onDownload = { onDownloadSample(sample) },
                    onPlay = { onPlaySample(sample) }
                )
            }
        }
    }
}

@Composable
private fun SampleReelCard(
    sample: SampleReel,
    onDownload: () -> Unit,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onPlay() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = sample.thumbnailUrl,
                    contentDescription = sample.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark vignette overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Play icon pill
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Preview",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Category Tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DashDarkBg.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = sample.category,
                        color = DashCoralAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Duration badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${sample.durationSeconds}s",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = sample.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sample.author,
                    color = DashTextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDownload,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DashVioletPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HowToGuideSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "💡 How to Download Reels",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            val steps = listOf(
                Triple("1", "Copy Reel Link", "Open Instagram, TikTok, or Shorts & click Share -> Copy Link."),
                Triple("2", "Paste in Dash Reel", "Tap 'Paste from Clipboard' or auto-detect will detect it instantly."),
                Triple("3", "Watch Offline Anytime", "Tap 'Download Reel' and play it offline in our built-in video player!")
            )

            steps.forEachIndexed { index, (num, title, desc) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DashVioletPrimary.copy(alpha = 0.25f))
                            .border(1.dp, DashVioletPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num,
                            color = DashVioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = desc,
                            color = DashTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                if (index < steps.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
