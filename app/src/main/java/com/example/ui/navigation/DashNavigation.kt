package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DashCoralAccent
import com.example.ui.theme.DashDarkBg
import com.example.ui.theme.DashDarkSurface
import com.example.ui.theme.DashTextMuted
import com.example.ui.theme.DashTextPrimary
import com.example.ui.theme.DashVioletPrimary

enum class DashTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME("Downloader", Icons.Filled.Download, Icons.Outlined.Download, "tab_downloader"),
    LIBRARY("Saved Reels", Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "tab_library"),
    SETTINGS("Tools", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

@Composable
fun DashBottomNavigationBar(
    currentTab: DashTab,
    onTabSelected: (DashTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("dash_bottom_nav_bar"),
        containerColor = DashDarkSurface,
        tonalElevation = 8.dp
    ) {
        DashTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 12.sp,
                        color = if (isSelected) DashTextPrimary else DashTextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = DashTextPrimary,
                    indicatorColor = DashVioletPrimary,
                    unselectedIconColor = DashTextMuted,
                    unselectedTextColor = DashTextMuted
                ),
                modifier = Modifier.testTag(tab.tag)
            )
        }
    }
}
