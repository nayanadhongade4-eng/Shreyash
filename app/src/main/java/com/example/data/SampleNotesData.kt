package com.example.data

data class NoteSample(
    val id: String,
    val title: String,
    val category: String,
    val tag: String,
    val content: String,
    val emoji: String
)

object SampleNotesData {
    val samples = listOf(
        NoteSample(
            id = "quantum_computing",
            title = "Quantum Computing 101",
            category = "Science & Tech",
            tag = "Physics & AI",
            emoji = "⚛️",
            content = """
                # Quantum Computing 101: Beyond Classical Bits
                
                Classical computers encode information in binary bits: strictly 0 or 1.
                In contrast, quantum computers harness quantum mechanics using qubits.
                
                ## Key Quantum Principles:
                1. Superposition: A qubit exists in a linear combination of states |0⟩ and |1⟩ simultaneously until measured.
                2. Quantum Entanglement: Two or more qubits become correlated such that the quantum state of one instantaneously determines the other, regardless of distance.
                3. Quantum Interference: Quantum algorithms choreograph constructive interference to amplify correct answers while canceling incorrect paths.
                
                ## Real-World Applications:
                - Molecular simulation for drug discovery and material science.
                - Cryptographic security and Shor's algorithm impact.
                - Hyper-dimensional optimization problems in logistics and AI training.
                
                The quantum revolution is transitioning from noisy intermediate-scale quantum (NISQ) systems to fault-tolerant error-corrected supercomputing.
            """.trimIndent()
        ),
        NoteSample(
            id = "lofi_focus_science",
            title = "The Neuroscience of Lo-Fi Beats",
            category = "Brain & Focus",
            tag = "Productivity",
            emoji = "🎧",
            content = """
                # The Neuroscience of Lo-Fi Music & Deep Focus
                
                Why do repetitive, uncompressed, 70-90 BPM beats help humans study and code for hours?
                
                ## Psychological Mechanisms:
                - Predictable Rhythm: Lo-Fi music avoids sudden vocal hooks, which keeps the brain's prefrontal cortex from context-switching.
                - Monaural Alpha Wave Stimulation: The 70-80 BPM tempo synchronizes neural oscillations with the resting alpha wave state (8-12 Hz), associated with relaxed alertness.
                - Pink Noise & Vinyl Crackle: Subtle vintage imperfections mask background sonic clutter, providing an auditory cocoon.
                
                ## The Dopamine Sweetspot:
                - Warm jazzy 7th chords provide harmonic satisfaction without emotional turbulence.
                - Low lyrical density prevents phonological loop interference while reading or writing.
                
                In summary, Lo-Fi music acts as non-invasive neuro-acoustic architecture for uninterrupted deep work.
            """.trimIndent()
        ),
        NoteSample(
            id = "photosynthesis",
            title = "Photosynthesis & Solar Biosynthesis",
            category = "Biology",
            tag = "Life Sciences",
            emoji = "🌱",
            content = """
                # Photosynthesis: Nature's Solar Battery
                
                Photosynthesis is the biochemical process that converts solar photon energy into chemical energy stored in glucose bonds.
                
                ## The Dual Stages:
                1. Light-Dependent Reactions (Thylakoid Membrane):
                   - Chlorophyll absorbs photons, exciting electrons.
                   - Photolysis of water releases O₂ and fuels ATP & NADPH synthesis.
                2. Light-Independent Calvin Cycle (Stroma):
                   - Rubisco catalyzes Carbon Fixation from CO₂.
                   - Reduction and regeneration synthesize high-energy G3P molecules to make glucose.
                
                ## Global Ecological Impact:
                - 100 billion metric tons of biomass created annually.
                - Planetary oxygen generation and planetary carbon sequestration.
            """.trimIndent()
        ),
        NoteSample(
            id = "noteflix_startup_pitch",
            title = "NoteFlix AI: Product Pitch Deck",
            category = "Business & Startup",
            tag = "Founder Pitch",
            emoji = "🚀",
            content = """
                # NoteFlix AI: Revolutionizing Knowledge Retention
                
                ## Problem:
                Humans read 50+ pages of notes, PDFs, and documentation weekly, but forget 80% within 48 hours (Ebbinghaus forgetting curve). Text is static and passive.
                
                ## Solution:
                NoteFlix AI transforms static documents into dynamic, narrated 60-second animated video reels with studio voiceover and Lo-Fi beats.
                
                ## Market & Metrics:
                - Total Addressable Market: $24B EdTech and Executive Learning sector.
                - 10x higher retention rate when visual storytelling pairs with synchronized audio.
                - 0-effort creation: upload a PDF or photo of notes, get a ready-to-watch studio reel in 5 seconds.
                
                ## Vision:
                The Netflix of your personal brain.
            """.trimIndent()
        ),
        NoteSample(
            id = "neural_networks",
            title = "Neural Networks: How AI Learns",
            category = "Artificial Intelligence",
            tag = "Machine Learning",
            emoji = "🧠",
            content = """
                # Demystifying Deep Neural Networks
                
                At their core, artificial neural networks are universal function approximators inspired by biological neural pathways.
                
                ## Core Architecture:
                - Input Layer: Raw feature vectors (pixels, audio frequencies, token embeddings).
                - Hidden Layers: Weighted linear combinations followed by non-linear activations (ReLU, GELU).
                - Output Layer: Softmax probability distribution over prediction classes.
                
                ## How Learning Happens:
                1. Forward Propagation: Data passes through layers to generate a prediction.
                2. Loss Calculation: Cross-entropy error measures distance from ground truth.
                3. Backpropagation: Chain rule computes gradients of loss with respect to each weight.
                4. Optimizer Step: Gradient descent updates parameters to minimize loss.
                
                Modern Transformers scale this architecture using Self-Attention to understand complex global context.
            """.trimIndent()
        )
    )
}
