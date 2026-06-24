package com.example.healthcareapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthcareapp.network.TimeSlotResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorTimeslotScreen(
    doctorId: Int? = null,
    doctorName: String? = null,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var slots by remember { mutableStateOf<List<TimeSlotResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val isAdmin = doctorId != null
    val primaryColor = if (isAdmin) AccentRed else Color(0xFF2E7D32)
    val lightBg = if (isAdmin) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var slotToEdit by remember { mutableStateOf<TimeSlotResponse?>(null) }
    var slotToDelete by remember { mutableStateOf<TimeSlotResponse?>(null) }

    // Load slots on first composition
    LaunchedEffect(Unit) {
        isLoading = true
        slots = DataManager.getMySlots(doctorId)
        isLoading = false
    }

    fun refreshSlots() {
        scope.launch {
            isLoading = true
            slots = DataManager.getMySlots(doctorId)
            isLoading = false
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = TextHint,
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (isAdmin) "Kelola Jadwal - $doctorName" else "Kelola Jadwal Praktik",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = White
                        )
                        Text(
                            "${slots.size} slot tersedia",
                            fontSize = 12.sp,
                            color = White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = primaryColor,
                contentColor = White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Tambah Jadwal"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (slots.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = lightBg,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📅", fontSize = 32.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Belum Ada Jadwal",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tekan tombol + untuk menambahkan jadwal praktik baru.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // Group by date
                val groupedSlots = slots.groupBy { it.date }.toSortedMap()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedSlots.forEach { (date, dateSlots) ->
                        item {
                            // Date header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = lightBg
                                ) {
                                    Text(
                                        text = "📅 $date",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = primaryColor
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "${dateSlots.size} slot",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        items(dateSlots) { slot ->
                            TimeslotCard(
                                slot = slot,
                                onEditClick = { slotToEdit = slot },
                                onDeleteClick = { slotToDelete = slot }
                            )
                        }
                    }

                    // Bottom spacer for FAB
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // ======================== ADD DIALOG ========================
    if (showAddDialog) {
        TimeslotFormDialog(
            title = "Tambah Jadwal Baru",
            fieldColors = fieldColors,
            primaryColor = primaryColor,
            lightBg = lightBg,
            onDismiss = { showAddDialog = false },
            onConfirm = { date, startTime, endTime ->
                scope.launch {
                    val success = DataManager.createSlot(date, startTime, endTime, doctorId)
                    if (success) {
                        showAddDialog = false
                        refreshSlots()
                    }
                }
            }
        )
    }

    // ======================== EDIT DIALOG ========================
    if (slotToEdit != null) {
        TimeslotFormDialog(
            title = "Edit Jadwal",
            initialDate = slotToEdit!!.date,
            initialStartTime = slotToEdit!!.startTime.substring(0, 5),
            initialEndTime = slotToEdit!!.endTime.substring(0, 5),
            fieldColors = fieldColors,
            primaryColor = primaryColor,
            lightBg = lightBg,
            onDismiss = { slotToEdit = null },
            onConfirm = { date, startTime, endTime ->
                scope.launch {
                    val success = DataManager.updateSlot(slotToEdit!!.id, date, startTime, endTime, doctorId)
                    if (success) {
                        slotToEdit = null
                        refreshSlots()
                    }
                }
            }
        )
    }

    // ======================== DELETE CONFIRM ========================
    if (slotToDelete != null) {
        AlertDialog(
            onDismissRequest = { slotToDelete = null },
            title = {
                Text(
                    "Hapus Jadwal",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus jadwal pada ${slotToDelete!!.date} " +
                            "(${slotToDelete!!.startTime.substring(0, 5)} - ${slotToDelete!!.endTime.substring(0, 5)})?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val success = DataManager.deleteSlot(slotToDelete!!.id, doctorId)
                            if (success) {
                                slotToDelete = null
                                refreshSlots()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hapus", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { slotToDelete = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun TimeslotCard(
    slot: TimeSlotResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val startTime = slot.startTime.substring(0, 5)
    val endTime = slot.endTime.substring(0, 5)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time icon
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (slot.isBooked) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        if (slot.isBooked) "🔒" else "🕒",
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$startTime - $endTime",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                if (slot.isBooked) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = "Sudah Dipesan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "Tersedia",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Action buttons (only for unbooked slots)
            if (!slot.isBooked) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Hapus",
                        tint = AccentRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeslotFormDialog(
    title: String,
    initialDate: String = "",
    initialStartTime: String = "",
    initialEndTime: String = "",
    fieldColors: TextFieldColors,
    primaryColor: Color,
    lightBg: Color,
    onDismiss: () -> Unit,
    onConfirm: (date: String, startTime: String, endTime: String) -> Unit
) {
    var date by remember { mutableStateOf(initialDate) }
    var startTime by remember { mutableStateOf(initialStartTime) }
    var endTime by remember { mutableStateOf(initialEndTime) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Error banner
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ ${errorMessage ?: ""}",
                            color = AccentRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it; errorMessage = null },
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    placeholder = { Text("2026-07-01", color = TextHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it; errorMessage = null },
                        label = { Text("Mulai") },
                        placeholder = { Text("08:00", color = TextHint) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it; errorMessage = null },
                        label = { Text("Selesai") },
                        placeholder = { Text("09:00", color = TextHint) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = lightBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ℹ️ Format tanggal: YYYY-MM-DD\nFormat waktu: HH:mm (24 jam)",
                        fontSize = 11.sp,
                        color = primaryColor,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        date.isBlank() -> errorMessage = "Tanggal wajib diisi"
                        startTime.isBlank() -> errorMessage = "Waktu mulai wajib diisi"
                        endTime.isBlank() -> errorMessage = "Waktu selesai wajib diisi"
                        !date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> errorMessage = "Format tanggal: YYYY-MM-DD"
                        !startTime.matches(Regex("\\d{2}:\\d{2}")) -> errorMessage = "Format waktu: HH:mm"
                        !endTime.matches(Regex("\\d{2}:\\d{2}")) -> errorMessage = "Format waktu: HH:mm"
                        else -> onConfirm(date, startTime, endTime)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Simpan", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}
