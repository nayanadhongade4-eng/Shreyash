package com.example.model

enum class VisualType(val displayName: String, val description: String) {
    ANIMATED_CARTOON("Cartoon Mascot", "Animated character illustrating the core concept"),
    KINETIC_TYPOGRAPHY("Kinetic Typography", "High-energy kinetic typography & keyword badges"),
    DYNAMIC_GRAPH("Data Graph", "Animated growing bar and trendline chart"),
    MIND_MAP("Mind Map", "Connected knowledge graph with pulsing nodes"),
    CODE_BREAKDOWN("Code Breakdown", "Simulated IDE terminal with syntax-highlighted typing"),
    INFOGRAPHIC_CARD("Infographic Card", "Key stats with radial progress meters and badges")
}

enum class MascotPose {
    EUREKA,
    THINKING,
    EXPLAINING,
    ROCKET,
    CELEBRATION
}

data class VideoScene(
    val id: String,
    val sceneNumber: Int,
    val title: String,
    val narration: String,
    val durationSeconds: Int = 10,
    val visualType: VisualType = VisualType.ANIMATED_CARTOON,
    val mascotPose: MascotPose = MascotPose.EXPLAINING,
    val keyPoints: List<String> = emptyList(),
    val graphValues: List<Float> = listOf(25f, 55f, 85f, 100f),
    val graphLabels: List<String> = listOf("Intro", "Concept", "Peak", "Impact"),
    val codeSnippet: String = "// NoteFlix AI\nval video = engine.render(notes)\nvideo.play()",
    val highlightKeyword: String = "Core Insight"
)
