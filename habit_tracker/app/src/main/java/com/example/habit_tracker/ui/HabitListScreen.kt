package com.example.habit_tracker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_tracker.data.Habit
import com.example.habit_tracker.data.HabitRepository
import com.example.habit_tracker.data.Priority
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    vm: HabitViewModel = viewModel(),
    onHabitClick: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { HabitRepository.get(context) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val habits by (if (selectedCategory == null) vm.habits else repo.observeHabitsByCategory(selectedCategory!!))
        .collectAsState(initial = emptyList())

    val categories by repo.observeCategories().collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var editHabit by remember { mutableStateOf<Habit?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }


    var searchQuery by remember { mutableStateOf("") }


    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            val gradient = Brush.horizontalGradient(
                listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        "Habit Tracker",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory ?: "All Categories",
                            onValueChange = {},
                            readOnly = true,
                            textStyle = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .menuAnchor()
                                .width(140.dp)
                                .height(48.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            placeholder = { Text("Category", color = Color.White.copy(alpha = 0.7f)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    selectedCategory = null
                                    categoryExpanded = false
                                }
                            )
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            Column {


                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    label = { Text("Search habits...") },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Clear")
                            }
                        }
                    }
                )


                val filteredHabits = habits.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                if (filteredHabits.isEmpty()) {
                    EmptyState(onAdd = { showAddDialog = true })
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(filteredHabits, key = { it.id }) { habit ->
                            HabitCard(
                                habit = habit,
                                onToggleToday = { checked -> vm.setTodayCheckedSync(habit.id, checked) },
                                onDelete = { vm.deleteHabit(habit) },
                                onEdit = { editHabit = habit },
                                repo = repo,
                                onClick = { onHabitClick(habit.id) }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        HabitDialog(
            categories = categories,
            initialCategory = selectedCategory,
            initialHabit = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, category, priority, reminderEnabled, reminderTime ->
                vm.addHabit(name, category, priority, reminderEnabled, reminderTime)
                showAddDialog = false
            }
        )
    }

    if (editHabit != null) {
        HabitDialog(
            categories = categories,
            initialCategory = editHabit?.category,
            initialHabit = editHabit,
            onDismiss = { editHabit = null },
            onSave = { name, category, priority, reminderEnabled, reminderTime ->
                vm.updateHabit(
                    editHabit!!.copy(
                        name = name,
                        category = category,
                        priority = priority ?: Priority.NORMAL,
                        reminderEnabled = reminderEnabled,
                        reminderTime = reminderTime
                    )
                )
                editHabit = null
            }
        )
    }
}



@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No habits yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Tap + to create your first habit")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAdd) { Text("Add Habit") }
    }
}


@Composable
private fun HabitCard(
    habit: Habit,
    onToggleToday: suspend (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (Habit) -> Unit,
    repo: HabitRepository,
    onClick: () -> Unit
) {
    var todayChecked by remember { mutableStateOf(false) }
    var week by remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }
    var streak by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(habit.id) {
        todayChecked = repo.isTodayChecked(habit.id)
        week = repo.last7Days(habit.id)
        streak = repo.currentStreak(habit.id)
    }

    val priorityColor = when (habit.priority) {
        Priority.HIGH -> Color.Red
        Priority.NORMAL -> Color(0xFFFFC107)
        Priority.LOW -> Color(0xFF2196F3)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    habit.category?.let {
                        Text("Category: $it", fontSize = 16.sp, color = Color.LightGray)
                    }
                    Text("Priority: ${habit.priority?.name ?: "Normal"}", fontSize = 16.sp, color = priorityColor)
                }

                Checkbox(
                    checked = todayChecked,
                    onCheckedChange = { checked ->
                        todayChecked = checked
                        scope.launch {
                            onToggleToday(checked)
                            week = repo.last7Days(habit.id)
                            streak = repo.currentStreak(habit.id)
                        }
                    },
                    modifier = Modifier.size(28.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4CAF50),
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )
            }

            Spacer(Modifier.height(8.dp))
            WeekStrip(week)
            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { ((streak % 7).coerceIn(0, 6)) / 6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF4CAF50),
                trackColor = Color.DarkGray
            )
            Spacer(Modifier.height(6.dp))
            Text("🔥 Streak: $streak days", fontSize = 14.sp, color = Color(0xFF4CAF50))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { onEdit(habit) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Black)
                }
                Spacer(Modifier.width(24.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x98, 0x15, 0x15, 0xFF))
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(week: Map<Int, Boolean>) {
    val ordered = remember(week) {
        if (week.isEmpty()) emptyList() else
            week.keys
                .sortedBy { LocalDate.ofEpochDay(it.toLong()).dayOfWeek.value }
                .map { it to week[it]!! }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ordered.forEach { (epoch, checked) ->
            val date = LocalDate.ofEpochDay(epoch.toLong())
            DayPill(
                text = date.dayOfWeek.name.take(3),
                checked = checked,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DayPill(
    text: String,
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bg = if (checked) Color(0xFFDFF6DD) else MaterialTheme.colorScheme.surface
    val fg = if (checked) Color(0xFF0F5132) else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(50),
        tonalElevation = 0.dp,
        color = bg,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitDialog(
    categories: List<String>,
    initialCategory: String?,
    initialHabit: Habit? = null,
    onDismiss: () -> Unit,
    onSave: (String, String?, Priority?, Boolean, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialHabit?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(initialHabit?.category ?: initialCategory ?: "Default") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(initialHabit?.priority) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }


    var enableReminder by remember { mutableStateOf(initialHabit?.reminderEnabled ?: false) }
    var reminderTime by remember { mutableStateOf(initialHabit?.reminderTime) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialHabit == null) "New Habit" else "Edit Habit") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit name") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))


                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        placeholder = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Default") },
                            onClick = {
                                selectedCategory = "Default"
                                categoryExpanded = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                        Divider()
                        DropdownMenuItem(
                            text = { Text("➕ New Category") },
                            onClick = {
                                categoryExpanded = false
                                showNewCategoryDialog = true
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))


                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = priority?.name ?: "Select Priority",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        placeholder = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(priorityExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        Priority.values().forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    priority = p
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = enableReminder,
                        onCheckedChange = { enableReminder = it }
                    )
                    Text("Set Reminder")
                }


                if (enableReminder) {
                    Button(onClick = {
                        val cal = java.util.Calendar.getInstance()
                        val dialog = android.app.TimePickerDialog(
                            context,
                            { _, hour: Int, minute: Int ->
                                reminderTime = "%02d:%02d".format(hour, minute)
                            },
                            cal.get(java.util.Calendar.HOUR_OF_DAY),
                            cal.get(java.util.Calendar.MINUTE),
                            true
                        )
                        dialog.show()
                    }) {
                        Text(reminderTime ?: "Choose Time")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    val finalCategory = if (selectedCategory == "Default") null else selectedCategory
                    onSave(name.trim(), finalCategory, priority, enableReminder, reminderTime)
                }
            ) { Text(if (initialHabit == null) "Add" else "Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )

    if (showNewCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showNewCategoryDialog = false },
            onAdd = { newCat ->
                selectedCategory = newCat
                showNewCategoryDialog = false
            }
        )
    }
}


@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = categoryName.isNotBlank(),
                onClick = { onAdd(categoryName.trim()) }
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
