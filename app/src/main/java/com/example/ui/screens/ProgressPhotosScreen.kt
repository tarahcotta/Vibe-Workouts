package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.LocalPhotoStorageManager
import com.example.data.PhotoPoseCategory
import com.example.data.ProgressPhotoEntity
import com.example.ui.VitalViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

enum class ProgressPhotoViewTab {
    TIMELINE,
    COMPARISON
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressPhotosScreen(
    viewModel: VitalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allPhotos by viewModel.allProgressPhotos.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var activeTab by remember { mutableStateOf(ProgressPhotoViewTab.TIMELINE) }
    var selectedPoseCategory by remember { mutableStateOf(PhotoPoseCategory.ALL) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var viewingPhotoDetail by remember { mutableStateOf<ProgressPhotoEntity?>(null) }
    var comparisonPhotoA by remember { mutableStateOf<ProgressPhotoEntity?>(null) }
    var comparisonPhotoB by remember { mutableStateOf<ProgressPhotoEntity?>(null) }
    var isPrivacyBannerDismissed by remember { mutableStateOf(false) }

    // Seed sample baseline if completely empty on initial load
    LaunchedEffect(allPhotos.size) {
        if (allPhotos.isEmpty()) {
            viewModel.seedSampleProgressPhotosIfEmpty()
        }
    }

    val filteredPhotos = remember(allPhotos, selectedPoseCategory) {
        if (selectedPoseCategory == PhotoPoseCategory.ALL) {
            allPhotos
        } else {
            allPhotos.filter { it.poseTag == selectedPoseCategory.tag }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact Dismissible Privacy Indicator
        if (!isPrivacyBannerDismissed) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Local Only",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "100% On-Device Privacy · Photos never leave this phone",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { isPrivacyBannerDismissed = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Privacy Banner",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Clean Single-Tier Segmented View Toggle
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            SegmentedButton(
                selected = activeTab == ProgressPhotoViewTab.TIMELINE,
                onClick = { activeTab = ProgressPhotoViewTab.TIMELINE },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = "Timeline",
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("tab_photo_timeline")
            ) {
                Text(
                    text = "Timeline (${allPhotos.size})",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            SegmentedButton(
                selected = activeTab == ProgressPhotoViewTab.COMPARISON,
                onClick = {
                    activeTab = ProgressPhotoViewTab.COMPARISON
                    if (comparisonPhotoA == null && allPhotos.isNotEmpty()) {
                        comparisonPhotoA = allPhotos.lastOrNull()
                    }
                    if (comparisonPhotoB == null && allPhotos.isNotEmpty()) {
                        comparisonPhotoB = allPhotos.firstOrNull()
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = {
                    Icon(
                        Icons.Default.Compare,
                        contentDescription = "Compare",
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("tab_photo_compare")
            ) {
                Text(
                    text = "Before & After",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (activeTab == ProgressPhotoViewTab.TIMELINE) {
            // Pose Filter Chips with edge padding
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 24.dp, top = 2.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(PhotoPoseCategory.values()) { cat ->
                    val isSelected = selectedPoseCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPoseCategory = cat },
                        label = {
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = "Selected", modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Action Header with "Add Photo" Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedPoseCategory == PhotoPoseCategory.ALL) "All Checkpoints (${filteredPhotos.size})" else "${selectedPoseCategory.displayName} (${filteredPhotos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tap to inspect or compare alignment",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddPhotoDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_progress_photo_button")
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }

            if (filteredPhotos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "No photos",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No ${selectedPoseCategory.displayName} Photos Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Capture your posture alignment or muscle tone to visually track your transformation over time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showAddPhotoDialog = true }) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Capture First Photo")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPhotos, key = { it.id }) { photo ->
                        val baselineTimestamp = allPhotos.minOfOrNull { it.dateTimestamp } ?: photo.dateTimestamp
                        val daysElapsed = TimeUnit.MILLISECONDS.toDays(photo.dateTimestamp - baselineTimestamp).toInt()

                        ProgressPhotoGridCard(
                            photo = photo,
                            daysElapsed = daysElapsed,
                            onCardClick = { viewingPhotoDetail = photo },
                            onCompareClick = {
                                comparisonPhotoA = allPhotos.lastOrNull()
                                comparisonPhotoB = photo
                                activeTab = ProgressPhotoViewTab.COMPARISON
                            }
                        )
                    }
                }
            }
        } else {
            // Before & After Split Comparator Tab
            ProgressPhotoComparisonView(
                allPhotos = allPhotos,
                selectedPhotoA = comparisonPhotoA,
                selectedPhotoB = comparisonPhotoB,
                onSelectPhotoA = { comparisonPhotoA = it },
                onSelectPhotoB = { comparisonPhotoB = it },
                onAddPhoto = { showAddPhotoDialog = true }
            )
        }
    }

    // Add Photo Dialog
    if (showAddPhotoDialog) {
        AddProgressPhotoDialog(
            defaultWeight = allPhotos.firstOrNull()?.bodyWeightLbs,
            onDismiss = { showAddPhotoDialog = false },
            onSaveFromUri = { uri, pose, weight, notes ->
                viewModel.addProgressPhotoFromUri(uri, pose, weight, notes)
                showAddPhotoDialog = false
            },
            onSaveFromBitmap = { bitmap, pose, weight, notes ->
                viewModel.addProgressPhotoFromBitmap(bitmap, pose, weight, notes)
                showAddPhotoDialog = false
            }
        )
    }

    // Photo Detail Fullscreen Dialog
    viewingPhotoDetail?.let { photo ->
        PhotoDetailDialog(
            photo = photo,
            allPhotos = allPhotos,
            onDismiss = { viewingPhotoDetail = null },
            onDelete = {
                viewModel.deleteProgressPhoto(photo)
                viewingPhotoDetail = null
            },
            onLaunchCompare = {
                comparisonPhotoA = allPhotos.lastOrNull()
                comparisonPhotoB = photo
                viewingPhotoDetail = null
                activeTab = ProgressPhotoViewTab.COMPARISON
            }
        )
    }
}

@Composable
fun ProgressPhotoGridCard(
    photo: ProgressPhotoEntity,
    daysElapsed: Int,
    onCardClick: () -> Unit,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("photo_card_${photo.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.82f)
                    .background(Color(0xFF111417))
            ) {
                AsyncImage(
                    model = File(photo.filePath),
                    contentDescription = "${photo.poseTag} on ${dateFormat.format(Date(photo.dateTimestamp))}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Pose Tag Overlay (Top-Left)
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = photo.poseTag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Days Elapsed Badge (Top-Right)
                Surface(
                    shape = RoundedCornerShape(bottomStart = 10.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (daysElapsed == 0) "Baseline" else "+$daysElapsed days",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = dateFormat.format(Date(photo.dateTimestamp)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (photo.bodyWeightLbs != null) {
                    Text(
                        text = "${photo.bodyWeightLbs} lbs",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (photo.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = photo.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inspect",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    FilledTonalIconButton(
                        onClick = onCompareClick,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("compare_photo_btn_${photo.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Compare ${photo.poseTag} Photo",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressPhotoComparisonView(
    allPhotos: List<ProgressPhotoEntity>,
    selectedPhotoA: ProgressPhotoEntity?,
    selectedPhotoB: ProgressPhotoEntity?,
    onSelectPhotoA: (ProgressPhotoEntity) -> Unit,
    onSelectPhotoB: (ProgressPhotoEntity) -> Unit,
    onAddPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPlumbLineGrid by remember { mutableStateOf(true) }
    var plumbLineXPercent by remember { mutableFloatStateOf(0.5f) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    val scrollState = rememberScrollState()

    if (allPhotos.size < 2) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Compare,
                    contentDescription = "Compare",
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Need At Least 2 Photos To Compare",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add photos across different weeks to compare posture changes, spinal alignment and muscular development.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddPhoto) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Progress Photo")
                }
            }
        }
        return
    }

    val photoA = selectedPhotoA ?: allPhotos.last()
    val photoB = selectedPhotoB ?: allPhotos.first()

    val daysDelta = remember(photoA, photoB) {
        val diffMs = photoB.dateTimestamp - photoA.dateTimestamp
        TimeUnit.MILLISECONDS.toDays(diffMs).toInt()
    }

    val weightDelta = remember(photoA, photoB) {
        if (photoA.bodyWeightLbs != null && photoB.bodyWeightLbs != null) {
            photoB.bodyWeightLbs - photoA.bodyWeightLbs
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Controls & Comparison Summary Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Transformation Metrics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (daysDelta >= 0) "$daysDelta Days of Consistent Training" else "${-daysDelta} Days Difference",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Posture Grid",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = showPlumbLineGrid,
                            onCheckedChange = { showPlumbLineGrid = it },
                            modifier = Modifier.testTag("posture_grid_toggle")
                        )
                    }
                }

                if (weightDelta != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Weight Change: ${if (weightDelta >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.1f", weightDelta)} lbs (${photoA.bodyWeightLbs} → ${photoB.bodyWeightLbs} lbs)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Side-by-Side Dual Images
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Before Photo
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "BEFORE (A)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Black)
                        .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                ) {
                    AsyncImage(
                        model = File(photoA.filePath),
                        contentDescription = "Before Photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (showPlumbLineGrid) {
                        PostureAlignmentOverlay(
                            plumbLineXPercent = plumbLineXPercent,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${dateFormat.format(Date(photoA.dateTimestamp))} • ${photoA.poseTag}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                if (photoA.notes.isNotBlank()) {
                    Text(
                        text = photoA.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // After Photo
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "AFTER (B)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Black)
                        .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                ) {
                    AsyncImage(
                        model = File(photoB.filePath),
                        contentDescription = "After Photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (showPlumbLineGrid) {
                        PostureAlignmentOverlay(
                            plumbLineXPercent = plumbLineXPercent,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${dateFormat.format(Date(photoB.dateTimestamp))} • ${photoB.poseTag}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                if (photoB.notes.isNotBlank()) {
                    Text(
                        text = photoB.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (showPlumbLineGrid) {
            Spacer(modifier = Modifier.height(12.dp))
            // Plumb Line Stepper Adjustment Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plumb Line Fine-Tune",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { plumbLineXPercent = (plumbLineXPercent - 0.02f).coerceAtLeast(0.15f) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nudge Left", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Left", fontSize = 11.sp)
                            }

                            TextButton(
                                onClick = { plumbLineXPercent = 0.5f },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Center", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { plumbLineXPercent = (plumbLineXPercent + 0.02f).coerceAtMost(0.85f) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Right", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Nudge Right", modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Landmarks: Top = Shoulders · Middle = Chest/Thoracic Spine · Lower = Pelvis/Hips · Bottom = Knees",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Select Photo A & B Selectors
        Text(
            text = "Select Comparison Pair",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose Before Photo (A):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(allPhotos) { p ->
                val isSelected = p.id == photoA.id
                ThumbnailPickerItem(
                    photo = p,
                    isSelected = isSelected,
                    label = "A",
                    onClick = { onSelectPhotoA(p) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Choose After Photo (B):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(allPhotos) { p ->
                val isSelected = p.id == photoB.id
                ThumbnailPickerItem(
                    photo = p,
                    isSelected = isSelected,
                    label = "B",
                    onClick = { onSelectPhotoB(p) }
                )
            }
        }
    }
}

@Composable
fun ThumbnailPickerItem(
    photo: ProgressPhotoEntity,
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("M/d", Locale.getDefault()) }

    Box(
        modifier = modifier
            .size(70.dp, 90.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = File(photo.filePath),
            contentDescription = "Thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            shape = RoundedCornerShape(bottomEnd = 6.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = "$label: ${dateFormat.format(Date(photo.dateTimestamp))}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun PostureAlignmentOverlay(
    plumbLineXPercent: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val gridColor = Color(0x664CAF50)
        val plumbLineColor = Color(0xCC00E676)
        val targetX = width * plumbLineXPercent.coerceIn(0.1f, 0.9f)

        // Vertical Plumb line
        drawLine(
            color = plumbLineColor,
            start = Offset(targetX, 0f),
            end = Offset(targetX, height),
            strokeWidth = 2.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
        )

        // Horizontal Guide Lines: Shoulders, Torso, Hips, Knees
        val shoulderY = height * 0.26f
        val chestY = height * 0.42f
        val hipY = height * 0.60f
        val kneeY = height * 0.80f

        drawLine(color = gridColor, start = Offset(0f, shoulderY), end = Offset(width, shoulderY), strokeWidth = 1.5f)
        drawLine(color = gridColor, start = Offset(0f, chestY), end = Offset(width, chestY), strokeWidth = 1.5f)
        drawLine(color = gridColor, start = Offset(0f, hipY), end = Offset(width, hipY), strokeWidth = 1.5f)
        drawLine(color = gridColor, start = Offset(0f, kneeY), end = Offset(width, kneeY), strokeWidth = 1.5f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProgressPhotoDialog(
    defaultWeight: Float?,
    onDismiss: () -> Unit,
    onSaveFromUri: (Uri, String, Float?, String) -> Unit,
    onSaveFromBitmap: (Bitmap, String, Float?, String) -> Unit
) {
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    var selectedPoseCategory by remember { mutableStateOf(PhotoPoseCategory.FRONT) }
    var weightInput by remember { mutableStateOf(defaultWeight?.toString() ?: "") }
    var notesInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    var showCalibrationGuide by remember { mutableStateOf(false) }
    var showPreviewPlumbGrid by remember { mutableStateOf(true) }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            selectedImageUri = tempCameraUri
            selectedBitmap = null
        }
    }

    // Photo Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedBitmap = null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Progress Photo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Standardization Protocol Card (Addresses Assumption 1: consistent camera angle/height)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCalibrationGuide = !showCalibrationGuide },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = "Standardization",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Standardized Setup Protocol",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Icon(
                                imageVector = if (showCalibrationGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (showCalibrationGuide) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "• Camera Height: Rest phone at mid-chest / sternum level (~42-46 in).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "• Distance: Stand exactly 7 feet (2.1 m) away for consistent body scale.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "• Lighting: Frontal, diffused light. Avoid overhead or backlighting shadows.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                // Photo Picker Row: Camera vs Gallery
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val (uri, _) = LocalPhotoStorageManager.createTempImageUriForCamera(context)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Camera", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Gallery", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gallery", fontSize = 12.sp)
                    }
                }

                // Quick Demo Posture Silhouette Scan Option
                OutlinedButton(
                    onClick = {
                        val bmp = LocalPhotoStorageManager.createSamplePostureBitmap(
                            title = "${selectedPoseCategory.displayName} Scan",
                            subtitle = "Logged Posture Checkpoint",
                            isImproved = true,
                            angle = selectedPoseCategory.displayName
                        )
                        selectedBitmap = bmp
                        selectedImageUri = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AccessibilityNew, contentDescription = "Scan", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Posture Guide Scan", fontSize = 12.sp)
                }

                // Image Preview Box with Alignment Plumb-Line Grid
                if (selectedImageUri != null || selectedBitmap != null) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF111417))
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Preview",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (selectedBitmap != null) {
                                AsyncImage(
                                    model = selectedBitmap,
                                    contentDescription = "Preview Bitmap",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Plumb-line overlay for alignment verification
                            if (showPreviewPlumbGrid) {
                                PostureAlignmentOverlay(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Preview Alignment Grid",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Switch(
                                checked = showPreviewPlumbGrid,
                                onCheckedChange = { showPreviewPlumbGrid = it },
                                modifier = Modifier.size(width = 40.dp, height = 24.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select camera, gallery, or generate a posture scan above to attach an image.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Pose Category Selector
                Text(
                    text = "Pose / Angle Focus",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PhotoPoseCategory.values().filter { it != PhotoPoseCategory.ALL }) { cat ->
                        val isSelected = selectedPoseCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPoseCategory = cat },
                            label = { Text(cat.displayName, fontSize = 11.sp) }
                        )
                    }
                }

                Text(
                    text = selectedPoseCategory.coachingDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )

                // Optional Body Weight
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Body Weight (optional, lbs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Posture Notes & Observations") },
                    placeholder = { Text("e.g. Thoracic extension improved, shoulder symmetry feeling balanced") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                if (inputError != null) {
                    Text(
                        text = inputError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedImageUri == null && selectedBitmap == null) {
                        inputError = "Please take a photo or select an image first."
                        return@Button
                    }
                    val weight = weightInput.toFloatOrNull()
                    if (selectedImageUri != null) {
                        onSaveFromUri(selectedImageUri!!, selectedPoseCategory.tag, weight, notesInput.trim())
                    } else if (selectedBitmap != null) {
                        onSaveFromBitmap(selectedBitmap!!, selectedPoseCategory.tag, weight, notesInput.trim())
                    }
                },
                modifier = Modifier.testTag("save_progress_photo_dialog_button")
            ) {
                Text("Save Locally")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PhotoDetailDialog(
    photo: ProgressPhotoEntity,
    allPhotos: List<ProgressPhotoEntity>,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onLaunchCompare: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showPlumbLine by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()) }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = photo.poseTag,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateFormat.format(Date(photo.dateTimestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Photo",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Interactive Zoomable Image Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 4f)
                                offset = if (scale > 1f) offset + pan else Offset.Zero
                            }
                        }
                ) {
                    AsyncImage(
                        model = File(photo.filePath),
                        contentDescription = "Full Detail Photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )

                    if (showPlumbLine) {
                        PostureAlignmentOverlay(modifier = Modifier.fillMaxSize())
                    }

                    // Grid Toggle FAB Overlay
                    FloatingActionButton(
                        onClick = { showPlumbLine = !showPlumbLine },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = if (showPlumbLine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (showPlumbLine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Icon(
                            Icons.Default.GridOn,
                            contentDescription = "Toggle Grid"
                        )
                    }
                }

                // Metadata Details Footer
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (photo.bodyWeightLbs != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Weight: ${photo.bodyWeightLbs} lbs",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Button(
                            onClick = onLaunchCompare,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Compare, contentDescription = "Compare", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compare with Baseline")
                        }
                    }

                    if (photo.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Notes & Observations:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = photo.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Progress Photo?") },
            text = { Text("This will permanently remove the photo and its records from your device.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
