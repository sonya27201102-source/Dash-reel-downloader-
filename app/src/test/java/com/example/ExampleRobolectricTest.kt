package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.downloader.ReelParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dash Reel", appName)
  }

  @Test
  fun `test reel parser for instagram link`() {
    val parsed = ReelParser.parse("https://www.instagram.com/reel/C8xK9mP_cyber/?igsh=abc123xyz")
    assertEquals("Instagram Reel", parsed.platform)
    assertTrue(parsed.title.contains("C8xK9mP_cyber"))
    assertNotNull(parsed.thumbnailUrl)
  }

  @Test
  fun `test reel parser for mp4 video stream`() {
    val parsed = ReelParser.parse("https://example.com/videos/trending_clip.mp4")
    assertEquals("Direct Video Stream", parsed.platform)
    assertTrue(parsed.isDirectStream)
  }
}
