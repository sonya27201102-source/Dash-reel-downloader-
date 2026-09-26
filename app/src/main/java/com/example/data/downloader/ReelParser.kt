package com.example.data.downloader

import android.net.Uri
import java.util.Locale

data class ParsedReelInfo(
    val platform: String,
    val title: String,
    val author: String,
    val directVideoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val category: String,
    val isDirectStream: Boolean,
    val note: String = ""
)

object ReelParser {

    fun parse(rawUrl: String): ParsedReelInfo {
        val trimmed = rawUrl.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        return when {
            lower.contains("instagram.com/reel") || lower.contains("instagram.com/reels") || lower.contains("instagram.com/p/") -> {
                val shortcode = extractInstagramShortcode(trimmed)
                ParsedReelInfo(
                    platform = "Instagram Reel",
                    title = "Instagram Reel #$shortcode",
                    author = "@instagram_creator",
                    directVideoUrl = trimmed,
                    thumbnailUrl = "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=600&q=80",
                    durationSeconds = 15,
                    category = "Viral",
                    isDirectStream = false,
                    note = "Instagram Reel detected. Dash extractor will connect to download stream."
                )
            }

            lower.contains("tiktok.com") -> {
                ParsedReelInfo(
                    platform = "TikTok",
                    title = "TikTok Viral Video",
                    author = "@tiktok_creator",
                    directVideoUrl = trimmed,
                    thumbnailUrl = "https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?w=600&q=80",
                    durationSeconds = 15,
                    category = "Dance",
                    isDirectStream = false,
                    note = "TikTok short video detected. Processing no-watermark stream."
                )
            }

            lower.contains("youtube.com/shorts") || lower.contains("youtu.be/") -> {
                ParsedReelInfo(
                    platform = "YouTube Shorts",
                    title = "YouTube Short Reel",
                    author = "@shorts_creator",
                    directVideoUrl = trimmed,
                    thumbnailUrl = "https://images.unsplash.com/photo-1611162616475-46b635cb6868?w=600&q=80",
                    durationSeconds = 20,
                    category = "Trending",
                    isDirectStream = false,
                    note = "YouTube Short detected."
                )
            }

            lower.contains("facebook.com/reel") || lower.contains("fb.watch") -> {
                ParsedReelInfo(
                    platform = "Facebook Reel",
                    title = "Facebook Reel Video",
                    author = "@fb_creator",
                    directVideoUrl = trimmed,
                    thumbnailUrl = "https://images.unsplash.com/photo-1562577309-4932fdd64cd1?w=600&q=80",
                    durationSeconds = 15,
                    category = "Entertainment",
                    isDirectStream = false,
                    note = "Facebook Reel detected."
                )
            }

            lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mov") || lower.contains(".mp4?") -> {
                val fileName = Uri.parse(trimmed).lastPathSegment?.substringBefore("?") ?: "Reel Video"
                ParsedReelInfo(
                    platform = "Direct Video Stream",
                    title = fileName.replace(".mp4", "").replace("_", " ").replace("-", " "),
                    author = "@media_source",
                    directVideoUrl = trimmed,
                    thumbnailUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=600&q=80",
                    durationSeconds = 15,
                    category = "Media",
                    isDirectStream = true,
                    note = "Direct video stream ready for maximum Dash speed download."
                )
            }

            else -> {
                // Fallback for general web video link
                val host = try {
                    Uri.parse(trimmed).host ?: "web"
                } catch (e: Exception) {
                    "video"
                }
                ParsedReelInfo(
                    platform = host,
                    title = "Video Reel from $host",
                    author = "@web_creator",
                    directVideoUrl = trimmed,
                    thumbnailUrl = "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=600&q=80",
                    durationSeconds = 15,
                    category = "General",
                    isDirectStream = false,
                    note = "Detecting video stream on $host..."
                )
            }
        }
    }

    private fun extractInstagramShortcode(url: String): String {
        return try {
            val segments = Uri.parse(url).pathSegments
            val index = segments.indexOfFirst { it == "reel" || it == "reels" || it == "p" }
            if (index != -1 && index + 1 < segments.size) {
                segments[index + 1]
            } else {
                url.hashCode().toString().takeLast(6)
            }
        } catch (e: Exception) {
            "reel"
        }
    }
}
