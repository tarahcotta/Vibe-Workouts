package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoggedWorkoutSessionEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.theme.BoneDensityGold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Calendar-based workout history & upcoming schedule visualization card.
 * Allows users to navigate through past/future months and inspect consistency metrics.
 */
@Composable
fun WorkoutCalendarSummaryCard(
    sessions: List<LoggedWorkoutSessionEntity>,
    routines: List<WorkoutRoutineEntity>,
    onStartWorkout: (WorkoutRoutineEntity?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val todayCal = Calendar.getInstance()
    var displayMonth by remember { mutableIntStateOf(todayCal.get(Calendar.MONTH)) }
    var displayYear by remember { mutableIntStateOf(todayCal.get(Calendar.YEAR)) }

    // Selected Day inside the display month (null = no explicit day clicked)
    var selectedDayNumber by remember { mutableStateOf<Int?>(todayCal.get(Calendar.DAY_OF_MONTH)) }

    // Month Navigation Helpers
    val monthCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, displayYear)
        set(Calendar.MONTH, displayMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthCal.time)
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ...

    // Map sessions by date (yyyy-MM-dd)
    val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sessionsByDate = remember(sessions) {
        sessions.groupBy { session ->
            dateKeyFormat.format(Date(session.dateTimestamp))
        }
    }

    // Filter sessions in current displayed month
    val monthSessions = remember(sessions, displayMonth, displayYear) {
        sessions.filter { session ->
            val cal = Calendar.getInstance().apply { timeInMillis = session.dateTimestamp }
            cal.get(Calendar.MONTH) == displayMonth && cal.get(Calendar.YEAR) == displayYear
        }
    }

    val monthCompletedCount = monthSessions.size
    val monthTotalVolume = monthSessions.sumOf { it.totalVolumeLbs.toDouble() }.toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("workout_calendar_summary_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // 1. Header & Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$monthCompletedCount workouts • ${monthTotalVolume} lbs volume",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (displayMonth == 0) {
                                displayMonth = 11
                                displayYear -= 1
                            } else {
                                displayMonth -= 1
                            }
                            selectedDayNumber = null
                        },
                        modifier = Modifier.testTag("calendar_prev_month_button")
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                displayMonth = todayCal.get(Calendar.MONTH)
                                displayYear = todayCal.get(Calendar.YEAR)
                                selectedDayNumber = todayCal.get(Calendar.DAY_OF_MONTH)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("calendar_today_button")
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            if (displayMonth == 11) {
                                displayMonth = 0
                                displayYear += 1
                            } else {
                                displayMonth += 1
                            }
                            selectedDayNumber = null
                        },
                        modifier = Modifier.testTag("calendar_next_month_button")
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Days of Week Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Month Days Grid
            val totalCells = (firstDayOfWeek - 1) + daysInMonth
            val totalRows = (totalCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (rowIndex in 0 until totalRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (colIndex in 0 until 7) {
                            val cellIndex = rowIndex * 7 + colIndex
                            val dayNumber = cellIndex - (firstDayOfWeek - 2)

                            if (dayNumber in 1..daysInMonth) {
                                // Build date string
                                val dayCal = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, displayYear)
                                    set(Calendar.MONTH, displayMonth)
                                    set(Calendar.DAY_OF_MONTH, dayNumber)
                                }
                                val dateKey = dateKeyFormat.format(dayCal.time)
                                val daySessions = sessionsByDate[dateKey] ?: emptyList()
                                val isCompleted = daySessions.isNotEmpty()

                                val isToday = dayCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                                        dayCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                                        dayCal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)

                                val isFuture = dayCal.after(todayCal)

                                // Check if scheduled routine day (e.g. Mon, Wed, Fri)
                                val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK)
                                val isScheduled = (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY)

                                val isSelected = selectedDayNumber == dayNumber

                                CalendarDayCell(
                                    dayNumber = dayNumber,
                                    isToday = isToday,
                                    isCompleted = isCompleted,
                                    isScheduled = isScheduled && isFuture,
                                    isSelected = isSelected,
                                    sessionCount = daySessions.size,
                                    onClick = { selectedDayNumber = dayNumber },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                // Empty padding cell
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Selected Day Details Card
            selectedDayNumber?.let { dayNum ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, displayYear)
                    set(Calendar.MONTH, displayMonth)
                    set(Calendar.DAY_OF_MONTH, dayNum)
                }
                val dateKey = dateKeyFormat.format(selectedCal.time)
                val daySessions = sessionsByDate[dateKey] ?: emptyList()

                val formattedDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(selectedCal.time)
                val isFuture = selectedCal.after(todayCal)
                val isToday = selectedCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        selectedCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                        selectedCal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth().testTag("calendar_selected_day_details")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (isToday) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "TODAY",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (daySessions.isNotEmpty()) {
                            daySessions.forEach { session ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Icon",
                                        tint = BoneDensityGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.routineDayTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${session.totalSetsCompleted} sets logged • ${session.totalVolumeLbs.toInt()} lbs volume • ${session.overallFeel}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else if (isToday || isFuture) {
                            val dayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK)
                            val isScheduledDay = (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY)

                            if (isScheduledDay && routines.isNotEmpty()) {
                                val routineIndex = (dayNum % routines.size)
                                val assignedRoutine = routines[routineIndex]

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventAvailable,
                                        contentDescription = "Icon",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Scheduled Workout: ${assignedRoutine.dayName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = assignedRoutine.focusSummary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isToday) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onStartWorkout(assignedRoutine) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("start_workout_from_calendar_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Icon",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Start Today's Scheduled Session")
                                    }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = "Icon",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Active Recovery & Dynamic Mobility Rest Day",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = "Icon",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rest Day (No workout logged)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dayNumber: Int,
    isToday: Boolean,
    isCompleted: Boolean,
    isScheduled: Boolean,
    isSelected: Boolean,
    sessionCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isCompleted -> BoneDensityGold.copy(alpha = 0.85f)
        isToday -> MaterialTheme.colorScheme.primaryContainer
        isSelected -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

    val textColor = when {
        isCompleted -> Color.Black
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .then(
                if (isToday) Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                else if (isSelected && !isCompleted) Modifier.border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(10.dp))
                else if (isScheduled) Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable { onClick() }
            .testTag("calendar_day_cell_$dayNumber"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$dayNumber",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isToday || isCompleted || isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                color = textColor
            )

            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color.Black,
                    modifier = Modifier.size(10.dp)
                )
            } else if (isScheduled) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
