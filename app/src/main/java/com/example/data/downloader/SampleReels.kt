package com.example.data.downloader

data class SampleReel(
    val title: String,
    val author: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val category: String,
    val description: String
)

object SampleReelsData {
    // Reliable, fast-streaming, public vertical/short video MP4 samples
    val list = listOf(
        SampleReel(
            title = "Cyber City Neon Night Lights",
            author = "@tokyo_vibes",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600&q=80",
            durationSeconds = 15,
            category = "Cinematic",
            description = "High energy urban neon landscape and cinematic night speed."
        ),
        SampleReel(
            title = "Supercar Drift & Turbo Spool",
            author = "@speed_beasts",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?w=600&q=80",
            durationSeconds = 15,
            category = "Motorsport",
            description = "Track testing high performance aerodynamics with roar engine sound."
        ),
        SampleReel(
            title = "Arctic Ocean Glacier Waves",
            author = "@wild_planet",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80",
            durationSeconds = 12,
            category = "Nature",
            description = "Crystal clear glacier blues meeting rough arctic currents."
        ),
        SampleReel(
            title = "Sizzling Gourmet Street Food",
            author = "@chef_master",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80",
            durationSeconds = 15,
            category = "Food",
            description = "Fire wok technique, tossing garlic butter noodles with crunch garnish."
        ),
        SampleReel(
            title = "Urban Breakdance & Flow State",
            author = "@dance_battles",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1535525153412-5a42439a210d?w=600&q=80",
            durationSeconds = 15,
            category = "Dance",
            description = "Electrifying rhythm choreography and smooth gravity-defying freeze."
        ),
        SampleReel(
            title = "Cosmic Nebulae & Star Cluster",
            author = "@astro_wonders",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&q=80",
            durationSeconds = 18,
            category = "Science",
            description = "Deep space telescope imaging of expanding star clouds and galaxies."
        )
    )
}
