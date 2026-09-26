package com.example.ui.screens

import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.theme.DashVioletLight
import com.example.ui.theme.DashVioletPrimary
import com.example.ui.viewmodel.ReelViewModel
import java.io.File
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: ReelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalStorageUsed by viewModel.totalStorageUsed.collectAsStateWithLifecycle()

    var autoDetectClipboard by remember { mutableStateOf(true) }
    var wifiOnly by remember { mutableStateOf(false) }
    var autoLoop by remember { mutableStateOf(true) }
    var defaultQuality by remember { mutableStateOf("1080p HD") }

    val statFs = remember {
        try {
            StatFs(Environment.getDataDirectory().path)
        } catch (e: Exception) {
            null
        }
    }
    val freeBytes = statFs?.availableBytes ?: (50L * 1024 * 1024 * 1024)
    val totalBytes = statFs?.totalBytes ?: (128L * 1024 * 1024 * 1024)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DashDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Storage",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Manage storage, download preferences & app engine",
                fontSize = 12.sp,
                color = DashTextSecondary
            )
        }

        // Storage Analyzer Card
        item {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DashVioletPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = DashVioletLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Reels Storage Usage",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = formatStorage(totalStorageUsed),
                            color = DashCoralAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val usedPercent = ((totalStorageUsed.toFloat() / (totalBytes.coerceAtLeast(1))) * 100f).coerceIn(1f, 100f)
                    LinearProgressIndicator(
                        progress = { usedPercent / 100f },
                        color = DashVioletPrimary,
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
                            text = "Dash reels: ${formatStorage(totalStorageUsed)}",
                            color = DashTextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Free: ${formatStorage(freeBytes)}",
                            color = DashCyanAccent,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clean Cache Button
                    Button(
                        onClick = { viewModel.clearStorageCache(context) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DashDarkSurfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_clean_cache")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = DashTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clean Temporary Cache",
                            color = DashTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Storage Directory Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = DashCyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Storage Location",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "App Downloads / DashReels (Zero Storage Permission)",
                            color = DashTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Preferences Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Download Preferences",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Auto detect clipboard switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Detect Reel Links",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Prompt to download immediately when a video link is copied",
                                color = DashTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = autoDetectClipboard,
                            onCheckedChange = { autoDetectClipboard = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DashVioletPrimary,
                                uncheckedThumbColor = DashTextMuted,
                                uncheckedTrackColor = DashDarkBg
                            )
                        )
                    }

                    // Download over Wi-Fi only
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Download on Wi-Fi Only",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Save mobile data plan while streaming or downloading",
                                color = DashTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = wifiOnly,
                            onCheckedChange = { wifiOnly = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DashVioletPrimary,
                                uncheckedThumbColor = DashTextMuted,
                                uncheckedTrackColor = DashDarkBg
                            )
                        )
                    }

                    // Auto loop reels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Loop Videos",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Continuously repeat reel in built-in offline player",
                                color = DashTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = autoLoop,
                            onCheckedChange = { autoLoop = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DashCoralAccent,
                                uncheckedThumbColor = DashTextMuted,
                                uncheckedTrackColor = DashDarkBg
                            )
                        )
                    }
                }
            }
        }

        // Privacy & About
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DashDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashDarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = DashGreenSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy & Security Guarantee",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Dash Reel Downloader operates 100% locally on your device. No user tracking, no third-party account required, and downloaded reels stay securely in your personal app storage.",
                        color = DashTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Version 1.0 (Dash Engine)",
                            color = DashTextMuted,
                            fontSize = 11.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DashGreenSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "READY",
                                color = DashGreenSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatStorage(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1024) {
        val gb = mb / 1024.0
        String.format(Locale.getDefault(), "%.2f GB", gb)
    } else {
        String.format(Locale.getDefault(), "%.1f MB", mb)
    }
}
