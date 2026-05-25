package com.example.healthcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * =============================================================
 * MainActivity.kt
 * =============================================================
 * PERUBAHAN UTAMA:
 * 1. Inisialisasi DataStore via DataManager.init(context)
 * 2. Membaca session login dari DataStore saat app dibuka
 * 3. Routing berdasarkan role (ADMIN → AdminDashboard, DOCTOR → DoctorDashboard, PATIENT → Home)
 * 4. Login menyimpan session ke DataStore
 * 5. Logout menghapus session dari DataStore
 * 6. Appointments di-load dari DataStore saat app dibuka
 * =============================================================
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi DataManager dengan context untuk DataStore
        DataManager.init(applicationContext)

        setContent {
            val coroutineScope = rememberCoroutineScope()

            // State untuk tracking login dan role
            var isLoading by remember { mutableStateOf(true) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var currentRole by remember { mutableStateOf<UserRole?>(null) }
            var currentEmail by remember { mutableStateOf("") }
            var currentUserName by remember { mutableStateOf("") }

            val backStack = remember { mutableStateListOf<Any>() }

            // Patient data (untuk role PATIENT)
            var patient by remember {
                mutableStateOf(
                    Patient(
                        id = 1,
                        name = "Berly Marcellino",
                        email = "berly@healthcare.com",
                        phone = "08123456789",
                        gender = "Laki-laki",
                        birthDate = "2004-01-01",
                        address = "Klaten, Indonesia"
                    )
                )
            }

            // Load session dan appointments dari DataStore saat pertama kali
            LaunchedEffect(Unit) {
                // Load appointments dari DataStore
                DataManager.loadAppointments()

                // Baca session login dari DataStore
                val repo = DataManager.getRepository()
                if (repo != null) {
                    val loggedIn = repo.isLoggedIn.first()
                    val email = repo.currentUserEmail.first()
                    val role = repo.currentUserRole.first()
                    val name = repo.currentUserName.first()

                    isLoggedIn = loggedIn
                    currentEmail = email
                    currentRole = role
                    currentUserName = name

                    // Set starting route berdasarkan session
                    backStack.clear()
                    if (loggedIn && role != null) {
                        when (role) {
                            UserRole.ADMIN -> backStack.add(Routes.AdminDashboard)
                            UserRole.DOCTOR -> backStack.add(Routes.DoctorDashboard)
                            UserRole.PATIENT -> backStack.add(Routes.Home)
                        }
                    } else {
                        backStack.add(Routes.Login)
                    }
                } else {
                    backStack.add(Routes.Login)
                }

                isLoading = false
            }

            // Fungsi helper untuk handle login
            fun handleLogin(user: User) {
                coroutineScope.launch {
                    val repo = DataManager.getRepository() ?: return@launch
                    // Simpan session ke DataStore
                    repo.saveLoginSession(user.email, user.role, user.name)

                    isLoggedIn = true
                    currentRole = user.role
                    currentEmail = user.email
                    currentUserName = user.name

                    // Update patient data jika role PATIENT
                    if (user.role == UserRole.PATIENT) {
                        patient = Patient(
                            id = 1,
                            name = user.name,
                            email = user.email,
                            phone = "08123456789",
                            gender = "Laki-laki",
                            birthDate = "2004-01-01",
                            address = "Klaten, Indonesia"
                        )
                    }

                    // Navigate ke dashboard sesuai role
                    backStack.clear()
                    when (user.role) {
                        UserRole.ADMIN -> backStack.add(Routes.AdminDashboard)
                        UserRole.DOCTOR -> backStack.add(Routes.DoctorDashboard)
                        UserRole.PATIENT -> backStack.add(Routes.Home)
                    }
                }
            }

            // Fungsi helper untuk handle logout
            fun handleLogout() {
                coroutineScope.launch {
                    val repo = DataManager.getRepository() ?: return@launch
                    // Hapus session dari DataStore
                    repo.clearLoginSession()

                    isLoggedIn = false
                    currentRole = null
                    currentEmail = ""
                    currentUserName = ""

                    backStack.clear()
                    backStack.add(Routes.Login)
                }
            }

            HealthcareTheme {
                if (isLoading) {
                    // Splash/loading saat baca DataStore
                    Text("Loading...")
                } else {
                    CompositionLocalProvider(LocalBackStack provides backStack) {
                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            entryProvider = { key ->
                                when (key) {
                                    // ================= LOGIN =================
                                    is Routes.Login -> NavEntry(key) {
                                        LoginScreen(
                                            onLoginSuccess = { user -> handleLogin(user) },
                                            onRegisterClick = { },
                                            onForgotPasswordClick = { }
                                        )
                                    }

                                    // ================= ADMIN DASHBOARD =================
                                    is Routes.AdminDashboard -> NavEntry(key) {
                                        AdminDashboardScreen(
                                            adminName = currentUserName.ifBlank { "Admin" },
                                            totalDoctors = DataManager.doctors.size,
                                            totalAppointments = DataManager.appointments.size,
                                            totalPatients = DataManager.users.count { it.role == UserRole.PATIENT },
                                            onDoctorListClick = { backStack.add(Routes.DoctorList) },
                                            onAppointmentListClick = { backStack.add(Routes.AppointmentList) },
                                            onLogoutClick = { handleLogout() }
                                        )
                                    }

                                    // ================= DOCTOR DASHBOARD =================
                                    is Routes.DoctorDashboard -> NavEntry(key) {
                                        val user = DataManager.users.find { it.email == currentEmail }
                                        val doctor = user?.doctorId?.let { id ->
                                            DataManager.doctors.find { it.id == id }
                                        }
                                        val doctorAppointments = DataManager.getAppointmentsForUser(
                                            currentEmail, UserRole.DOCTOR
                                        )

                                        DoctorDashboardScreen(
                                            doctor = doctor,
                                            doctorName = currentUserName.ifBlank { "Dokter" },
                                            appointments = doctorAppointments,
                                            onAppointmentDetailClick = { appt ->
                                                backStack.add(Routes.AppointmentDetail(appt.id))
                                            },
                                            onLogoutClick = { handleLogout() }
                                        )
                                    }

                                    // ================= HOME (PATIENT) =================
                                    is Routes.Home -> NavEntry(key) {
                                        MainScreen(
                                            isLoggedIn = true,
                                            patient = patient,
                                            onLoginClick = { },
                                            onProfileClick = { backStack.add(Routes.Profile) },
                                            onDoctorListClick = { backStack.add(Routes.DoctorList) },
                                            onAppointmentClick = { backStack.add(Routes.AppointmentList) },
                                            onFindHospitalClick = { },
                                            onEmergencyCallClick = { }
                                        )
                                    }

                                    // ================= DOCTOR LIST =================
                                    is Routes.DoctorList -> NavEntry(key) {
                                        DoctorListScreen(
                                            doctors = DataManager.doctors,
                                            onDoctorClick = { doctor ->
                                                backStack.add(Routes.DoctorDetail(doctor.id))
                                            },
                                            onBackClick = { backStack.removeLastOrNull() }
                                        )
                                    }

                                    // ================= DOCTOR DETAIL =================
                                    is Routes.DoctorDetail -> NavEntry(key) {
                                        val doctor = DataManager.doctors.find { it.id == key.doctorId }
                                        if (doctor != null) {
                                            DoctorDetailScreen(
                                                doctor = doctor,
                                                onBackClick = { backStack.removeLastOrNull() }
                                            )
                                        } else {
                                            Text("Doctor tidak ditemukan")
                                        }
                                    }

                                    // ================= APPOINTMENT LIST =================
                                    is Routes.AppointmentList -> NavEntry(key) {
                                        // Filter appointment berdasarkan role
                                        val filteredAppointments = if (currentRole != null) {
                                            DataManager.getAppointmentsForUser(currentEmail, currentRole!!)
                                        } else {
                                            DataManager.appointments.toList()
                                        }

                                        AppointmentListScreen(
                                            appointments = filteredAppointments,
                                            onBackClick = { backStack.removeLastOrNull() },
                                            onAddAppointmentClick = {
                                                backStack.add(Routes.FormAppointment)
                                            },
                                            onDetailClick = { appointment ->
                                                backStack.add(Routes.AppointmentDetail(appointment.id))
                                            }
                                        )
                                    }

                                    // ================= APPOINTMENT DETAIL =================
                                    is Routes.AppointmentDetail -> NavEntry(key) {
                                        val appointment = DataManager.appointments.find { it.id == key.id }
                                        if (appointment != null) {
                                            AppointmentDetailScreen(
                                                appointment = appointment,
                                                onBackClick = { backStack.removeLastOrNull() }
                                            )
                                        } else {
                                            Text("Appointment tidak ditemukan")
                                        }
                                    }

                                    // ================= FORM APPOINTMENT =================
                                    is Routes.FormAppointment -> NavEntry(key) {
                                        FormAppointment(
                                            patientEmail = currentEmail,
                                            onBackClick = { backStack.removeLastOrNull() },
                                            onConfirmClick = { patientName, patientEmail, doctorName, date, time, symptoms ->
                                                DataManager.addAppointment(
                                                    patientName, patientEmail, doctorName, date, time, symptoms
                                                )
                                            }
                                        )
                                    }

                                    // ================= HISTORY =================
                                    is Routes.HistoryList -> NavEntry(key) {
                                        HistoryScreen(
                                            onBackClick = { backStack.removeLastOrNull() },
                                            items = DataManager.historyItems
                                        )
                                    }

                                    // ================= PROFILE =================
                                    is Routes.Profile -> NavEntry(key) {
                                        ProfileScreen(
                                            patient = patient,
                                            userRole = currentRole ?: UserRole.PATIENT,
                                            onMedicalRecordClick = {
                                                backStack.add(Routes.HistoryList)
                                            },
                                            onAppointmentClick = {
                                                backStack.add(Routes.AppointmentList)
                                            },
                                            onBackClick = { backStack.removeLastOrNull() },
                                            onLogoutClick = { handleLogout() }
                                        )
                                    }

                                    // ================= FALLBACK =================
                                    else -> NavEntry(key) {
                                        Text("Halaman tidak ditemukan")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}