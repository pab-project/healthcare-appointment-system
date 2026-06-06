package com.example.healthcareapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminName: String,
    totalDoctors: Int,
    totalAppointments: Int,
    totalPatients: Int,
    onDoctorListClick: () -> Unit = {},
    onAppointmentListClick: () -> Unit = {},
    onLogoutClick: () -> Unit
) {
    // Tab Navigation State
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Ringkasan", "Dokter", "Pasien", "Appointment")

    // Search query states
    var doctorSearchQuery by remember { mutableStateOf("") }
    var patientSearchQuery by remember { mutableStateOf("") }
    var appointmentStatusFilter by remember { mutableStateOf("Semua") }

    // Dialog state for Doctor CRUD
    var showAddDoctorDialog by remember { mutableStateOf(false) }
    var doctorToEdit by remember { mutableStateOf<Doctor?>(null) }
    var doctorToDelete by remember { mutableStateOf<Doctor?>(null) }

    // Dialog state for Patient CRUD
    var showAddPatientDialog by remember { mutableStateOf(false) }
    var patientToEdit by remember { mutableStateOf<Patient?>(null) }
    var patientToDelete by remember { mutableStateOf<Patient?>(null) }

    // Logout confirmation state
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                        Text("Halo, $adminName", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccentRed),
                actions = {
                    IconButton(onClick = { showLogoutConfirm = true }) {
                        Text("🚪", fontSize = 20.sp)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = White,
                tonalElevation = 8.dp
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val icon = when (index) {
                        0 -> "📊"
                        1 -> "🩺"
                        2 -> "🧑"
                        3 -> "📅"
                        else -> "⚙️"
                    }
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        label = { Text(title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Text(icon, fontSize = 20.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentRed,
                            selectedTextColor = AccentRed,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Color(0xFFFFEBEE)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: RINGKASAN DASHBOARD
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Header Stats
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "Statistik Sistem",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AdminStatCard("🩺", "Dokter", DataManager.doctors.size.toString(), Color(0xFF1565C0), Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AdminStatCard("🧑", "Pasien", DataManager.patients.size.toString(), Color(0xFF2E7D32), Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AdminStatCard("📅", "Janji Temu", DataManager.appointments.size.toString(), Color(0xFFE65100), Modifier.weight(1f))
                                }
                            }
                        }

                        // Welcome & Info Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "Kelola Rumah Sakit",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Gunakan menu navigasi di bawah untuk mengelola data dokter, data pasien, serta menyetujui atau menolak janji temu pasien yang masuk.",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val pendingCount = DataManager.appointments.count { it.status == "Pending" }
                                if (pendingCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFFECE0),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("⚠️", fontSize = 24.sp)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    "Permintaan Janji Temu Baru",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFFE65100)
                                                )
                                                Text(
                                                    "Ada $pendingCount janji temu yang membutuhkan persetujuan Anda.",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 2: CRUD DOKTER
                    val filteredDoctors = DataManager.doctors.filter {
                        it.name.contains(doctorSearchQuery, ignoreCase = true) ||
                                it.specialization.contains(doctorSearchQuery, ignoreCase = true)
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = doctorSearchQuery,
                                onValueChange = { doctorSearchQuery = it },
                                placeholder = { Text("Cari Dokter...") },
                                leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 8.dp)) },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentRed,
                                    unfocusedBorderColor = TextHint
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { showAddDoctorDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("+ Dokter", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (filteredDoctors.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Tidak ada data dokter ditemukan", color = TextSecondary)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredDoctors) { doc ->
                                    DoctorRowItem(
                                        doctor = doc,
                                        onEditClick = { doctorToEdit = doc },
                                        onDeleteClick = { doctorToDelete = doc }
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // TAB 3: CRUD PASIEN
                    val filteredPatients = DataManager.patients.filter {
                        it.name.contains(patientSearchQuery, ignoreCase = true) ||
                                it.email.contains(patientSearchQuery, ignoreCase = true)
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = patientSearchQuery,
                                onValueChange = { patientSearchQuery = it },
                                placeholder = { Text("Cari Pasien...") },
                                leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 8.dp)) },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentRed,
                                    unfocusedBorderColor = TextHint
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { showAddPatientDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("+ Pasien", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (filteredPatients.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Tidak ada data pasien ditemukan", color = TextSecondary)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredPatients) { pat ->
                                    PatientRowItem(
                                        patient = pat,
                                        onEditClick = { patientToEdit = pat },
                                        onDeleteClick = { patientToDelete = pat }
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // TAB 4: PERSATUAN APPOINTMENT (ACC/REJECT)
                    val statusOptions = listOf("Semua", "Pending", "Upcoming", "Completed", "Cancelled")
                    
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // Filter Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            statusOptions.forEach { status ->
                                val isSelected = appointmentStatusFilter == status
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AccentRed else Color.White,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { appointmentStatusFilter = status },
                                    border = if (isSelected) null else BorderStroke(1.dp, TextHint)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        val filteredAppointments = DataManager.appointments.filter {
                            appointmentStatusFilter == "Semua" || it.status.equals(appointmentStatusFilter, ignoreCase = true)
                        }.sortedByDescending { it.id }

                        if (filteredAppointments.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Tidak ada janji temu", color = TextSecondary)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredAppointments) { appt ->
                                    AdminAppointmentItem(
                                        appointment = appt,
                                        onApproveClick = {
                                            DataManager.updateAppointmentStatus(appt.id, "Upcoming")
                                        },
                                        onRejectClick = {
                                            DataManager.updateAppointmentStatus(appt.id, "Cancelled")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================= DIALOGS =================================

    // 1. DIALOG TAMBAH DOKTER
    if (showAddDoctorDialog) {
        DoctorFormDialog(
            title = "Tambah Dokter Baru",
            onDismiss = { showAddDoctorDialog = false },
            onConfirm = { name, specialization, description, scheduleList ->
                DataManager.addDoctor(name, specialization, description, scheduleList)
                showAddDoctorDialog = false
            }
        )
    }

    // 2. DIALOG EDIT DOKTER
    if (doctorToEdit != null) {
        DoctorFormDialog(
            title = "Ubah Informasi Dokter",
            doctor = doctorToEdit,
            onDismiss = { doctorToEdit = null },
            onConfirm = { name, specialization, description, scheduleList ->
                DataManager.updateDoctor(doctorToEdit!!.id, name, specialization, description, scheduleList)
                doctorToEdit = null
            }
        )
    }

    // 3. DIALOG DELETE DOKTER
    if (doctorToDelete != null) {
        AlertDialog(
            onDismissRequest = { doctorToDelete = null },
            title = { Text("Hapus Dokter", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Apakah Anda yakin ingin menghapus data Dokter ${doctorToDelete!!.name}? Ini juga akan menghapus akun login terkait.") },
            confirmButton = {
                Button(
                    onClick = {
                        DataManager.deleteDoctor(doctorToDelete!!.id)
                        doctorToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Hapus", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { doctorToDelete = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 4. DIALOG TAMBAH PASIEN
    if (showAddPatientDialog) {
        PatientFormDialog(
            title = "Tambah Pasien Baru",
            onDismiss = { showAddPatientDialog = false },
            onConfirm = { name, email, phone, gender, birthDate, address ->
                DataManager.addPatient(name, email, phone, gender, birthDate, address)
                showAddPatientDialog = false
            }
        )
    }

    // 5. DIALOG EDIT PASIEN
    if (patientToEdit != null) {
        PatientFormDialog(
            title = "Ubah Informasi Pasien",
            patient = patientToEdit,
            onDismiss = { patientToEdit = null },
            onConfirm = { name, email, phone, gender, birthDate, address ->
                DataManager.updatePatient(patientToEdit!!.id, name, email, phone, gender, birthDate, address)
                patientToEdit = null
            }
        )
    }

    // 6. DIALOG DELETE PASIEN
    if (patientToDelete != null) {
        AlertDialog(
            onDismissRequest = { patientToDelete = null },
            title = { Text("Hapus Pasien", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Apakah Anda yakin ingin menghapus data Pasien ${patientToDelete!!.name}? Ini juga akan menghapus akun login dan riwayat janji temu terkait.") },
            confirmButton = {
                Button(
                    onClick = {
                        DataManager.deletePatient(patientToDelete!!.id)
                        patientToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Hapus", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { patientToDelete = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 7. DIALOG LOGOUT CONFIRM
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Keluar Admin", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Apakah Anda yakin ingin keluar dari Dashboard Admin?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Keluar", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

// ================================= SUB COMPONENT WIDGETS =================================

@Composable
fun AdminStatCard(emoji: String, label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun DoctorRowItem(doctor: Doctor, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(doctor.specialization, fontSize = 13.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(doctor.description, fontSize = 12.sp, color = TextSecondary, maxLines = 2)
            
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditClick) {
                    Text("✏️ Edit", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDeleteClick) {
                    Text("🗑️ Hapus", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PatientRowItem(patient: Patient, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(patient.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✉️", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(patient.email, fontSize = 13.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📞", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(patient.phone, fontSize = 13.sp, color = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditClick) {
                    Text("✏️ Edit", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDeleteClick) {
                    Text("🗑️ Hapus", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminAppointmentItem(
    appointment: Appointment,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    val badgeColor = when (appointment.status.lowercase()) {
        "pending" -> Color(0xFFFFECE0)
        "upcoming", "approved" -> Color(0xFFE8F5E9)
        "completed" -> Color(0xFFF5F5F5)
        else -> Color(0xFFFFEBEE) // cancelled / rejected
    }

    val badgeTextColor = when (appointment.status.lowercase()) {
        "pending" -> Color(0xFFE65100)
        "upcoming", "approved" -> Color(0xFF2E7D32)
        "completed" -> Color(0xFF757575)
        else -> Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Dokter: ${appointment.doctor}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text("Poli: ${appointment.poli}", fontSize = 12.sp, color = TextSecondary)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = appointment.status,
                        color = badgeTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = TextHint.copy(alpha = 0.4f))

            Text("Pasien: ${appointment.patientName} (${appointment.patientEmail})", fontSize = 13.sp, color = TextPrimary)
            Text("Jadwal: ${appointment.date} - ${appointment.time}", fontSize = 13.sp, color = TextSecondary)

            if (appointment.status.equals("Pending", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApproveClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Setujui", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Button(
                        onClick = onRejectClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Tolak", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ================================= FORM DIALOGS =================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorFormDialog(
    title: String,
    doctor: Doctor? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, specialization: String, description: String, schedule: List<String>) -> Unit
) {
    var name by remember { mutableStateOf(doctor?.name ?: "") }
    var specialization by remember { mutableStateOf(doctor?.specialization ?: "Dokter Umum") }
    var description by remember { mutableStateOf(doctor?.description ?: "") }
    
    // Join schedule as comma-separated string for editing
    var scheduleStr by remember { 
        mutableStateOf(doctor?.schedule?.joinToString(", ") ?: "Senin 08:00 - 12:00, Rabu 13:00 - 17:00") 
    }

    val poliOptions = listOf("Dokter Umum", "Dokter Gigi", "Dokter Anak", "Dokter Kulit", "Dokter Mata")
    var poliExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Dokter") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = poliExpanded,
                    onExpandedChange = { poliExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = specialization,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Spesialisasi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = poliExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = poliExpanded,
                        onDismissRequest = { poliExpanded = false }
                    ) {
                        poliOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    specialization = option
                                    poliExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi/Biografi") },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )

                OutlinedTextField(
                    value = scheduleStr,
                    onValueChange = { scheduleStr = it },
                    label = { Text("Jadwal Praktik (pisahkan dengan koma)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val scheduleList = scheduleStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onConfirm(name, specialization, description, scheduleList)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan", color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFormDialog(
    title: String,
    patient: Patient? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String, gender: String, birthDate: String, address: String) -> Unit
) {
    var name by remember { mutableStateOf(patient?.name ?: "") }
    var email by remember { mutableStateOf(patient?.email ?: "") }
    var phone by remember { mutableStateOf(patient?.phone ?: "") }
    var gender by remember { mutableStateOf(patient?.gender ?: "Laki-laki") }
    var birthDate by remember { mutableStateOf(patient?.birthDate ?: "2000-01-01") }
    var address by remember { mutableStateOf(patient?.address ?: "") }

    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Laki-laki", "Perempuan")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = patient == null, // email tidak boleh diubah jika edit
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. HP / WA") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Kelamin") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat") },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, email, phone, gender, birthDate, address)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan", color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    AdminDashboardScreen(
        adminName = "Administrator",
        totalDoctors = 5,
        totalAppointments = 3,
        totalPatients = 2,
        onLogoutClick = {}
    )
}
