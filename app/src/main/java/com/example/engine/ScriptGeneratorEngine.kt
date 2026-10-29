package com.example.engine

import com.example.model.MascotPose
import com.example.model.VideoProject
import com.example.model.VideoScene
import com.example.model.VisualType
import java.util.UUID

object ScriptGeneratorEngine {

    fun generateVideoFromNotes(
        title: String,
        rawNotes: String
    ): VideoProject {
        val cleanNotes = rawNotes.trim()
        val generatedTitle = if (title.isNotBlank()) title else extractTitleFromContent(cleanNotes)
        val scenes = parseContentIntoScenes(cleanNotes, generatedTitle)

        return VideoProject(
            id = UUID.randomUUID().toString(),
            title = generatedTitle,
            sourceNoteTitle = generatedTitle,
            originalNoteText = cleanNotes,
            scenes = scenes
        )
    }

    private fun extractTitleFromContent(content: String): String {
        val firstLine = content.lines().firstOrNull { it.isNotBlank() } ?: "NoteFlix Video"
        return firstLine.replace(Regex("^#+\\s*"), "").take(45).trim()
    }

    private fun parseContentIntoScenes(content: String, projectTitle: String): List<VideoScene> {
        val rawSections = splitIntoSemanticSections(content)
        val scenes = mutableListOf<VideoScene>()

        rawSections.forEachIndexed { index, section ->
            val sceneNumber = index + 1
            val lines = section.lines().filter { it.isNotBlank() }
            val headerLine = lines.firstOrNull() ?: "Scene $sceneNumber"
            val bodyLines = lines.drop(1).ifEmpty { lines }
            val combinedBody = bodyLines.joinToString(" ")

            val sceneTitle = headerLine
                .replace(Regex("^#+\\s*"), "")
                .replace(Regex("^\\d+\\.\\s*"), "")
                .replace(Regex("[-*]\\s*"), "")
                .trim()
                .take(40)

            val narration = createStudioNarration(sceneTitle, combinedBody, sceneNumber, rawSections.size)
            val visualType = inferVisualType(section, index, rawSections.size)
            val mascotPose = inferMascotPose(visualType, index, rawSections.size)
            val keyPoints = extractKeyPoints(bodyLines)
            val (graphValues, graphLabels) = generateChartData(section, index)
            val codeSnippet = generateCodeSnippet(section, sceneTitle)
            val keyword = extractHighlightKeyword(section, sceneTitle)
            val durationSeconds = (narration.split("\\s+".toRegex()).size / 2.5f).toInt().coerceIn(7, 16)

            scenes.add(
                VideoScene(
                    id = UUID.randomUUID().toString(),
                    sceneNumber = sceneNumber,
                    title = sceneTitle,
                    narration = narration,
                    durationSeconds = durationSeconds,
                    visualType = visualType,
                    mascotPose = mascotPose,
                    keyPoints = keyPoints,
                    graphValues = graphValues,
                    graphLabels = graphLabels,
                    codeSnippet = codeSnippet,
                    highlightKeyword = keyword
                )
            )
        }

        return scenes.ifEmpty {
            listOf(
                VideoScene(
                    id = UUID.randomUUID().toString(),
                    sceneNumber = 1,
                    title = projectTitle,
                    narration = "Welcome to NoteFlix AI. Here is your synthesized note summary.",
                    durationSeconds = 8,
                    visualType = VisualType.ANIMATED_CARTOON,
                    mascotPose = MascotPose.EXPLAINING,
                    keyPoints = listOf("Synthesized with NoteFlix", "Studio Narration", "Animated Visuals"),
                    highlightKeyword = "Knowledge Reel"
                )
            )
        }
    }

    private fun splitIntoSemanticSections(content: String): List<String> {
        val headerSplit = content.split(Regex("(?m)^(?=#{1,3}\\s+|[A-Z0-9][a-zA-Z0-9\\s]{3,30}:)"))
            .map { it.trim() }
            .filter { it.length > 20 }

        if (headerSplit.size >= 3) {
            return headerSplit.take(6)
        }

        val paragraphSplit = content.split(Regex("\n\n+"))
            .map { it.trim() }
            .filter { it.length > 25 }

        if (paragraphSplit.size >= 3) {
            return paragraphSplit.take(6)
        }

        // Chunk by lines if short or unformatted
        val lines = content.lines().filter { it.isNotBlank() }
        val chunkSize = (lines.size / 3).coerceAtLeast(2)
        return lines.chunked(chunkSize).map { it.joinToString("\n") }.take(5)
    }

    private fun createStudioNarration(
        title: String,
        body: String,
        index: Int,
        total: Int
    ): String {
        val cleanBody = body
            .replace(Regex("[-*•]\\s*"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        val intro = when (index) {
            1 -> "Let's break down $title."
            total -> "Finally, the key takeaway for $title."
            else -> "Moving to $title:"
        }

        val narrative = if (cleanBody.length > 240) {
            cleanBody.take(240).substringBeforeLast(".") + "."
        } else if (cleanBody.isNotBlank()) {
            cleanBody
        } else {
            "Exploring the foundational mechanics of $title."
        }

        return "$intro $narrative"
    }

    private fun inferVisualType(content: String, index: Int, total: Int): VisualType {
        val lower = content.lowercase()
        return when {
            lower.contains("code") || lower.contains("function") || lower.contains("val ") || lower.contains("algorithm") ->
                VisualType.CODE_BREAKDOWN
            lower.contains("percent") || lower.contains("%") || lower.contains("metric") || lower.contains("stat") || lower.contains("market") ->
                VisualType.DYNAMIC_GRAPH
            lower.contains("network") || lower.contains("principle") || lower.contains("cycle") || lower.contains("system") ->
                VisualType.MIND_MAP
            lower.contains("rate") || lower.contains("growth") || lower.contains("impact") ->
                VisualType.INFOGRAPHIC_CARD
            index == 0 ->
                VisualType.ANIMATED_CARTOON
            index == total - 1 ->
                VisualType.KINETIC_TYPOGRAPHY
            else -> {
                val types = listOf(VisualType.ANIMATED_CARTOON, VisualType.KINETIC_TYPOGRAPHY, VisualType.DYNAMIC_GRAPH, VisualType.MIND_MAP)
                types[index % types.size]
            }
        }
    }

    private fun inferMascotPose(visualType: VisualType, index: Int, total: Int): MascotPose {
        return when {
            index == 0 -> MascotPose.EXPLAINING
            index == total - 1 -> MascotPose.CELEBRATION
            visualType == VisualType.ANIMATED_CARTOON -> MascotPose.EUREKA
            visualType == VisualType.CODE_BREAKDOWN -> MascotPose.THINKING
            visualType == VisualType.DYNAMIC_GRAPH -> MascotPose.ROCKET
            else -> MascotPose.EXPLAINING
        }
    }

    private fun extractKeyPoints(lines: List<String>): List<String> {
        val bulletPoints = lines.filter { it.trim().startsWith("-") || it.trim().startsWith("*") || it.trim().matches(Regex("^\\d+\\..*")) }
            .map { it.replace(Regex("^[-*\\d.]+\\s*"), "").trim() }
            .filter { it.isNotBlank() }

        if (bulletPoints.isNotEmpty()) {
            return bulletPoints.take(3)
        }

        return lines.take(3).map { it.take(45).trim() }
    }

    private fun generateChartData(content: String, index: Int): Pair<List<Float>, List<String>> {
        val patterns = listOf(
            Pair(listOf(20f, 45f, 75f, 95f), listOf("Base", "Sprint", "Peak", "Target")),
            Pair(listOf(35f, 60f, 85f, 100f), listOf("Phase 1", "Phase 2", "Phase 3", "Final")),
            Pair(listOf(15f, 35f, 70f, 90f), listOf("Q1", "Q2", "Q3", "Q4")),
            Pair(listOf(50f, 72f, 88f, 98f), listOf("Input", "Compute", "Optimize", "Output"))
        )
        return patterns[index % patterns.size]
    }

    private fun generateCodeSnippet(content: String, title: String): String {
        return """
            // NoteFlix Engine: $title
            val knowledge = parse(input)
            val script = knowledge.generateScenes()
            AudioEngine.synthesize(script.voice)
            Canvas.renderVideo(fps = 60)
        """.trimIndent()
    }

    private fun extractHighlightKeyword(content: String, title: String): String {
        val candidates = listOf("Paradox", "Superposition", "Dopamine", "Alpha Waves", "Entropy", "Quantum", "Bio-Energy", "Neural Net", "Attention", "Retention", "Automation")
        val found = candidates.firstOrNull { content.contains(it, ignoreCase = true) }
        return found ?: title.split(" ").lastOrNull()?.take(16) ?: "Key Concept"
    }
}
