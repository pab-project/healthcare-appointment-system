package com.example.healthcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.healthcareapp.ui.HealthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi DataManager untuk DataStore
        DataManager.init(applicationContext)

        setContent {

            // ================= VIEWMODEL =================
            val viewModel: HealthViewModel = viewModel()

            val coroutineScope = rememberCoroutineScope()

            // ================= SESSION STATE =================
            var isLoading by remember { mutableStateOf(true) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var currentRole by remember { mutableStateOf<UserRole?>(null) }
            var currentEmail by remember { mutableStateOf("") }
            var currentUserName by remember { mutableStateOf("") }

            var showSplash by remember { mutableStateOf(true) }
            var splashAnimationDone by remember { mutableStateOf(false) }

            // ================= NAVIGATION =================
            val backStack = remember {
                mutableStateListOf<Any>(Routes.Login as Any)
            }

            // ================= PATIENT =================
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

            // ================= LOAD SESSION =================
            LaunchedEffect(Unit) {

                // Load appointments dari datastore/repository
                viewModel.loadAppointments()

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

                    // Muat data profil pasien jika ada
                    if (loggedIn && role == UserRole.PATIENT) {
                        val savedPatient = DataManager.patients.find { it.email.equals(email, ignoreCase = true) }
                        if (savedPatient != null) {
                            patient = savedPatient
                        } else {
                            patient = Patient(
                                id = 1,
                                name = name,
                                email = email,
                                phone = "08123456789",
                                gender = "Laki-laki",
                                birthDate = "2004-01-01",
                                address = "Klaten, Indonesia"
                            )
                        }
                    }

                    backStack.clear()

                    if (loggedIn && role != null) {

                        when (role) {

                            UserRole.ADMIN -> {
                                backStack.add(Routes.AdminDashboard)
                            }

                            UserRole.DOCTOR -> {
                                backStack.add(Routes.DoctorDashboard)
                            }

                            UserRole.PATIENT -> {
                                backStack.add(Routes.Home)
                            }
                        }

                    } else {

                        backStack.add(Routes.Login)
                    }

                } else {

                    backStack.clear()
                    backStack.add(Routes.Login)
                }

                isLoading = false
                if (splashAnimationDone) {
                    showSplash = false
                }
            }

            LaunchedEffect(currentEmail) {
                if (currentEmail.isNotBlank()) {
                    DataManager.loadNotifications(currentEmail)
                }
            }

            // ================= HANDLE LOGIN =================
            fun handleLogin(user: User) {

                coroutineScope.launch {

                    val repo = DataManager.getRepository()
                        ?: return@launch

                    // Save session
                    repo.saveLoginSession(
                        user.email,
                        user.role,
                        user.name,
                        user.token
                    )

                    isLoggedIn = true
                    currentRole = user.role
                    currentEmail = user.email
                    currentUserName = user.name

                    // Update patient jika role patient
                    if (user.role == UserRole.PATIENT) {
                        val existingPatient = DataManager.patients.find { it.email.equals(user.email, ignoreCase = true) }
                        if (existingPatient != null) {
                            patient = existingPatient
                        } else {
                            patient = Patient(
                                id = (DataManager.patients.maxByOrNull { it.id }?.id ?: 0) + 1,
                                name = user.name,
                                email = user.email,
                                phone = "08123456789",
                                gender = "Laki-laki",
                                birthDate = "2004-01-01",
                                address = "Klaten, Indonesia"
                            )
                        }
                    }

                    backStack.clear()

                    when (user.role) {

                        UserRole.ADMIN -> {
                            backStack.add(Routes.AdminDashboard)
                        }

                        UserRole.DOCTOR -> {
                            backStack.add(Routes.DoctorDashboard)
                        }

                        UserRole.PATIENT -> {
                            backStack.add(Routes.Home)
                        }
                    }
                }
            }

            // ================= HANDLE LOGOUT =================
            fun handleLogout() {

                coroutineScope.launch {

                    val repo = DataManager.getRepository()
                        ?: return@launch

                    repo.clearLoginSession()

                    isLoggedIn = false
                    currentRole = null
                    currentEmail = ""
                    currentUserName = ""

                    backStack.clear()
                    backStack.add(Routes.Login)
                }
            }

            // ================= UI =================
            // Show splash until BOTH animation is done AND loading is done
            val shouldShowSplash = showSplash || isLoading

            HealthcareTheme {

                if (shouldShowSplash) {

                    SplashScreen(
                        onSplashFinished = {
                            splashAnimationDone = true
                            if (!isLoading) {
                                showSplash = false
                            }
                        }
                    )

                } else {

                    CompositionLocalProvider(
                        LocalBackStack provides backStack
                    ) {

                        NavDisplay(

                            backStack = backStack,

                            onBack = {
                                backStack.removeLastOrNull()
                            },

                            entryProvider = { key ->

                                when (key) {

                                    // ================= LOGIN =================
                                    is Routes.Login -> NavEntry(key) {

                                        LoginScreen(

                                            onLoginSuccess = { user ->
                                                handleLogin(user)
                                            },

                                            onRegisterClick = { backStack.add(Routes.Register) },

                                            onForgotPasswordClick = { }
                                        )
                                    }

                                    is Routes.Register -> NavEntry(key) {
                                        RegisterScreen(
                                            onRegisterSuccess = { user ->
                                                handleLogin(user)
                                            },
                                            onBackToLogin = {
                                                backStack.removeLastOrNull()
                                            }
                                        )
                                    }

                                    // ================= HOME =================
                                    is Routes.Home -> NavEntry(key) {

                                        val upcoming = viewModel.getAppointmentsForUser(
                                            currentEmail,
                                            UserRole.PATIENT
                                        ).filter { it.status.equals("Upcoming", ignoreCase = true) }

                                        MainScreen(
                                            isLoggedIn = true,
                                            patient = patient,
                                            unreadNotificationsCount = DataManager.notifications.count { !it.isRead },
                                            upcomingAppointments = upcoming,
                                            onLoginClick = { },
                                            onProfileClick = {
                                                backStack.add(Routes.Profile)
                                            },
                                            onDoctorListClick = {
                                                backStack.add(Routes.DoctorList)
                                            },
                                            onAppointmentClick = {
                                                backStack.add(Routes.AppointmentList)
                                            },
                                            onFindHospitalClick = { },
                                            onEmergencyCallClick = { },
                                            onNotificationClick = {
                                                backStack.add(Routes.Notifications)
                                            }
                                        )
                                    }

                                    // ================= ADMIN DASHBOARD =================
                                    is Routes.AdminDashboard -> NavEntry(key) {

                                        AdminDashboardScreen(

                                            adminName = currentUserName.ifBlank {
                                                "Admin"
                                            },

                                            totalDoctors = viewModel.doctors.size,

                                            totalAppointments = viewModel.appointments.size,

                                            totalPatients = viewModel.users.count {
                                                it.role == UserRole.PATIENT
                                            },

                                            onDoctorListClick = {
                                                backStack.add(Routes.DoctorList)
                                            },

                                            onAppointmentListClick = {
                                                backStack.add(Routes.AppointmentList)
                                            },

                                            onLogoutClick = {
                                                handleLogout()
                                            }
                                        )
                                    }

                                    // ================= DOCTOR DASHBOARD =================
                                    is Routes.DoctorDashboard -> NavEntry(key) {

                                        val doctor = viewModel.doctors.find {
                                            it.name.equals(currentUserName, ignoreCase = true)
                                        }

                                        val doctorAppointments =
                                            viewModel.getAppointmentsForUser(
                                                currentEmail,
                                                UserRole.DOCTOR
                                            )

                                        DoctorDashboardScreen(
                                            doctor = doctor,
                                            doctorName = currentUserName.ifBlank {
                                                "Dokter"
                                            },
                                            appointments = doctorAppointments,
                                            unreadNotificationsCount = DataManager.notifications.count { !it.isRead },
                                            onAppointmentDetailClick = { appointment ->
                                                backStack.add(
                                                    Routes.AppointmentDetail(
                                                        appointment.id
                                                    )
                                                )
                                            },
                                            onAcceptClick = { apptId ->
                                                coroutineScope.launch { DataManager.updateAppointmentStatus(apptId, "Upcoming") }
                                            },
                                            onRejectClick = { apptId ->
                                                coroutineScope.launch { DataManager.updateAppointmentStatus(apptId, "Cancelled") }
                                            },
                                            onCompleteClick = { apptId, diagnosis, treatment, medications, notes ->
                                                coroutineScope.launch {
                                                    DataManager.updateAppointmentStatus(
                                                        id = apptId,
                                                        status = "Completed",
                                                        diagnosis = diagnosis,
                                                        treatment = treatment,
                                                        medications = medications,
                                                        notes = notes
                                                    )
                                                }
                                            },
                                            onProfileClick = {
                                                backStack.add(Routes.DoctorProfile)
                                            },
                                            onNotificationClick = {
                                                backStack.add(Routes.Notifications)
                                            },
                                            onLogoutClick = {
                                                handleLogout()
                                            }
                                        )
                                    }

                                    // ================= DOCTOR LIST =================
                                    is Routes.DoctorList -> NavEntry(key) {

                                        DoctorListScreen(

                                            doctors = viewModel.doctors,

                                            onDoctorClick = { doctor ->

                                                backStack.add(
                                                    Routes.DoctorDetail(
                                                        doctor.id
                                                    )
                                                )
                                            },

                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            }
                                        )
                                    }

                                    // ================= DOCTOR DETAIL =================
                                    is Routes.DoctorDetail -> NavEntry(key) {

                                        val doctor = viewModel.doctors.find {
                                            it.id == key.doctorId
                                        }

                                        if (doctor != null) {

                                            DoctorDetailScreen(

                                                doctor = doctor,

                                                onBackClick = {
                                                    backStack.removeLastOrNull()
                                                }
                                            )

                                        } else {

                                            Text("Doctor tidak ditemukan")
                                        }
                                    }

                                    // ================= APPOINTMENT LIST =================
                                    is Routes.AppointmentList -> NavEntry(key) {

                                        val filteredAppointments =
                                            if (currentRole != null) {

                                                viewModel.getAppointmentsForUser(
                                                    currentEmail,
                                                    currentRole!!
                                                )

                                            } else {

                                                viewModel.appointments.toList()
                                            }

                                        AppointmentListScreen(

                                            appointments = filteredAppointments,

                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            },

                                            onAddAppointmentClick = {
                                                backStack.add(
                                                    Routes.FormAppointment
                                                )
                                            },

                                            onDetailClick = { appointment ->

                                                backStack.add(
                                                    Routes.AppointmentDetail(
                                                        appointment.id
                                                    )
                                                )
                                            },

                                            onRescheduleClick = { appointment, newDate, newTime ->
                                                viewModel.rescheduleAppointment(
                                                    appointment.id,
                                                    newDate,
                                                    newTime
                                                )
                                            },

                                            onCancelClick = { appointment ->
                                                coroutineScope.launch {
                                                    DataManager.updateAppointmentStatus(
                                                        appointment.id,
                                                        "Cancelled"
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    // ================= APPOINTMENT DETAIL =================
                                    is Routes.AppointmentDetail -> NavEntry(key) {

                                        val appointment =
                                            viewModel.appointments.find {
                                                it.id == key.id
                                            }

                                        if (appointment != null) {

                                            AppointmentDetailScreen(

                                                appointment = appointment,

                                                onBackClick = {
                                                    backStack.removeLastOrNull()
                                                }
                                            )

                                        } else {

                                            Text("Appointment tidak ditemukan")
                                        }
                                    }

                                    // ================= FORM APPOINTMENT =================
                                    is Routes.FormAppointment -> NavEntry(key) {

                                        FormAppointment(

                                            patientEmail = currentEmail,

                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            },

                                            onConfirmClick = {
                                                    patientName,
                                                    patientEmail,
                                                    doctorName,
                                                    date,
                                                    time,
                                                    symptoms ->

                                                coroutineScope.launch {
                                                    DataManager.addAppointment(
                                                        patientName,
                                                        patientEmail,
                                                        doctorName,
                                                        date,
                                                        time,
                                                        symptoms
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    // ================= HISTORY =================
                                    is Routes.HistoryList -> NavEntry(key) {

                                        HistoryScreen(
                                            items = viewModel.historyItems,
                                            onItemClick = { item ->
                                                backStack.add(Routes.HistoryDetail(item.id))
                                            },
                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            }
                                        )
                                    }

                                    // ================= HISTORY DETAIL =================
                                    is Routes.HistoryDetail -> NavEntry(key) {
                                        val historyItem = viewModel.historyItems.find { it.id == key.id }
                                        if (historyItem != null) {
                                            HistoryDetailScreen(
                                                historyItem = historyItem,
                                                onBackClick = {
                                                    backStack.removeLastOrNull()
                                                }
                                            )
                                        } else {
                                            Text("Detail rekam medis tidak ditemukan")
                                        }
                                    }


                                    // ================= PROFILE =================
                                    is Routes.Profile -> NavEntry(key) {

                                        ProfileScreen(

                                            patient = patient,

                                            userRole = currentRole
                                                ?: UserRole.PATIENT,

                                            onEditProfileClick = {
                                                backStack.add(
                                                    Routes.EditProfile
                                                )
                                            },

                                            onMedicalRecordClick = {
                                                backStack.add(
                                                    Routes.HistoryList
                                                )
                                            },

                                            onAppointmentClick = {
                                                backStack.add(
                                                    Routes.AppointmentList
                                                )
                                            },

                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            },

                                            onLogoutClick = {
                                                handleLogout()
                                            }
                                        )
                                    }

                                    // ================= EDIT PROFILE =================
                                    is Routes.EditProfile -> NavEntry(key) {
                                        EditProfileScreen(
                                            patient = patient,
                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            },
                                            onSaveClick = { updatedPatient ->
                                                patient = updatedPatient
                                                coroutineScope.launch {
                                                    // Update di list patients DataManager
                                                    DataManager.updatePatient(
                                                        updatedPatient.id,
                                                        updatedPatient.name,
                                                        updatedPatient.email,
                                                        updatedPatient.phone,
                                                        updatedPatient.gender,
                                                        updatedPatient.birthDate,
                                                        updatedPatient.address
                                                    )
                                                }
                                                backStack.removeLastOrNull()
                                            }
                                        )
                                    }

                                    // ================= NOTIFICATIONS =================
                                    is Routes.Notifications -> NavEntry(key) {
                                        NotificationScreen(
                                            notifications = DataManager.notifications.toList(),
                                            currentUserEmail = currentEmail,
                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            },
                                            onMarkAsRead = { id ->
                                                DataManager.markNotificationAsRead(id)
                                            },
                                            onMarkAllAsRead = {
                                                DataManager.markAllNotificationsAsRead(currentEmail)
                                            },
                                            onDeleteNotification = { id ->
                                                DataManager.deleteNotification(id)
                                            }
                                        )
                                    }

                                    // ================= DOCTOR PROFILE =================
                                    is Routes.DoctorProfile -> NavEntry(key) {
                                        val doctor = viewModel.doctors.find {
                                            it.name.equals(currentUserName, ignoreCase = true)
                                        }
                                        if (doctor != null) {
                                            DoctorProfileScreen(
                                                doctor = doctor,
                                                doctorEmail = currentEmail,
                                                onEditProfileClick = {
                                                    backStack.add(Routes.DoctorEditProfile)
                                                },
                                                onLogoutClick = {
                                                    handleLogout()
                                                },
                                                onBackClick = {
                                                    backStack.removeLastOrNull()
                                                }
                                            )
                                        } else {
                                            Text("Dokter tidak ditemukan")
                                        }
                                    }

                                    // ================= DOCTOR EDIT PROFILE =================
                                    is Routes.DoctorEditProfile -> NavEntry(key) {
                                        val doctor = viewModel.doctors.find {
                                            it.name.equals(currentUserName, ignoreCase = true)
                                        }
                                        if (doctor != null) {
                                            DoctorEditProfileScreen(
                                                doctor = doctor,
                                                onSaveClick = { name, spec, desc, sched ->
                                                     coroutineScope.launch { DataManager.updateDoctor(doctor.id, name, spec, desc, sched) }
                                                    backStack.removeLastOrNull()
                                                },
                                                onBackClick = {
                                                    backStack.removeLastOrNull()
                                                }
                                            )
                                        } else {
                                            Text("Dokter tidak ditemukan")
                                        }
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