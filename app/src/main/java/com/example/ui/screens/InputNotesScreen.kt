package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NoteSample
import com.example.data.SampleNotesData
import com.example.engine.DocumentParser
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.NoteFlixBgDark
import com.example.ui.theme.NoteFlixBorder
import com.example.ui.theme.NoteFlixSurfaceCard
import com.example.ui.theme.NoteFlixSurfaceDark
import com.example.ui.theme.NoteFlixSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary

@Composable
fun InputNotesScreen(
    isGenerating: Boolean,
    onGenerate: (title: String, content: String) -> Unit,
    onOpenLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember {
        mutableStateOf(SampleNotesData.samples.first().content)
    }
    var selectedSampleId by remember {
        mutableStateOf(SampleNotesData.samples.first().id)
    }
    var uploadedFileName by remember { mutableStateOf<String?>(null) }

    // Android Document Picker for PDFs and Docs
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val parsed = DocumentParser.parseFromUri(context, it)
            uploadedFileName = parsed.filename
            noteTitle = parsed.filename.substringBeforeLast(".")
            noteContent = parsed.extractedText
            selectedSampleId = ""
        }
    }

    // Zero-permission Android Photo Picker for Handwritten Notebook Photos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val filename = "Handwritten_Note_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
            val text = DocumentParser.parseHandwrittenImageText(filename)
            uploadedFileName = filename
            noteTitle = "Handwritten Notebook Notes"
            noteContent = text
            selectedSampleId = ""
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NoteFlixBgDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header Banner
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(listOf(CrimsonPrimary, VioletSecondary))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NOTE",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "FLIX",
                                color = CrimsonPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .background(VioletSecondary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "AI STUDIO",
                                    color = CyanAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Text(
                            text = "The Note-to-Video Engine",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Library / Saved projects button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoteFlixSurfaceElevated)
                        .border(1.dp, NoteFlixBorder, RoundedCornerShape(10.dp))
                        .clickable { onOpenLibrary() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("open_library_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Saved Projects",
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Projects",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Multi-Format Ingestion Action Buttons
        item {
            Text(
                text = "1. INGEST CONTENT",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PDF / Doc Upload Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NoteFlixSurfaceElevated)
                        .border(1.dp, NoteFlixBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            docPickerLauncher.launch("*/*")
                        }
                        .padding(12.dp)
                        .testTag("upload_pdf_doc_button")
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Upload PDF",
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PDF / Docs",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PDF, Word, or Markdown",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Handwritten Page Photo Picker
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NoteFlixSurfaceElevated)
                        .border(1.dp, NoteFlixBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(12.dp)
                        .testTag("upload_photo_notes_button")
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Handwritten Photo",
                                tint = AmberAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Photo Notes",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Handwritten OCR Scan",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            if (uploadedFileName != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldAccent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, EmeraldAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "✓ Loaded: $uploadedFileName",
                        color = EmeraldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Quick Preset Note Templates
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "OR CHOOSE A CURATED TOPIC",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tap to load",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SampleNotesData.samples) { sample ->
                    val isSelected = selectedSampleId == sample.id
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF2F1535) else NoteFlixSurfaceCard)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CrimsonPrimary else NoteFlixBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedSampleId = sample.id
                                noteTitle = sample.title
                                noteContent = sample.content
                                uploadedFileName = null
                            }
                            .padding(10.dp)
                            .testTag("sample_topic_${sample.id}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = sample.emoji, fontSize = 18.sp)
                                Box(
                                    modifier = Modifier
                                        .background(VioletSecondary.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = sample.category.take(8),
                                        color = CyanAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = sample.title,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = sample.tag,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Note Editor Card
        item {
            Text(
                text = "2. NOTE CONTENT & SCRIPT SOURCE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NoteFlixSurfaceElevated, RoundedCornerShape(14.dp))
                    .border(1.dp, NoteFlixBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Reel Title (Optional)", color = TextMuted) },
                    placeholder = { Text("e.g. Quantum Computing Explained", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = NoteFlixBorder,
                        focusedContainerColor = NoteFlixSurfaceDark,
                        unfocusedContainerColor = NoteFlixSurfaceDark
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteContent,
                    onValueChange = {
                        noteContent = it
                        selectedSampleId = ""
                    },
                    label = { Text("Source Notes / Document Text", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("note_content_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = NoteFlixBorder,
                        focusedContainerColor = NoteFlixSurfaceDark,
                        unfocusedContainerColor = NoteFlixSurfaceDark
                    ),
                    maxLines = 12
                )
            }
        }

        // Primary Generate Button
        item {
            Button(
                onClick = {
                    onGenerate(noteTitle, noteContent)
                },
                enabled = noteContent.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("generate_video_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonPrimary,
                    disabledContainerColor = CrimsonDark.copy(alpha = 0.5f)
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Synthesizing Script & Visuals...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate NoteFlix Video",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
