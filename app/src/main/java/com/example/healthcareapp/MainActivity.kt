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

            // ================= NAVIGATION =================
            val backStack = remember {
                mutableStateListOf<Any>()
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
                    val savedPatient = repo.patientProfile.first()
                    if (savedPatient != null) {
                        patient = savedPatient
                    } else if (loggedIn && role == UserRole.PATIENT) {
                        patient = Patient(
                            id = 1,
                            name = name,
                            email = email,
                            phone = "08123456789",
                            gender = "Laki-laki",
                            birthDate = "2004-01-01",
                            address = "Klaten, Indonesia"
                        )
                        repo.savePatientProfile(patient)
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

                    backStack.add(Routes.Login)
                }

                isLoading = false
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
                        user.name
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
                            DataManager.patients.add(patient)
                            DataManager.persistPatients()
                        }
                        repo.savePatientProfile(patient)
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
            HealthcareTheme {

                if (isLoading) {

                    Text("Loading...")

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

                                            onRegisterClick = { },

                                            onForgotPasswordClick = { }
                                        )
                                    }

                                    // ================= HOME =================
                                    is Routes.Home -> NavEntry(key) {

                                        MainScreen(

                                            isLoggedIn = true,

                                            patient = patient,

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

                                            onEmergencyCallClick = { }
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

                                        val user = viewModel.users.find {
                                            it.email == currentEmail
                                        }

                                        val doctor = user?.doctorId?.let { id ->
                                            viewModel.doctors.find {
                                                it.id == id
                                            }
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

                                            onAppointmentDetailClick = { appointment ->
                                                backStack.add(
                                                    Routes.AppointmentDetail(
                                                        appointment.id
                                                    )
                                                )
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

                                                viewModel.addAppointment(
                                                    patientName,
                                                    patientEmail,
                                                    doctorName,
                                                    date,
                                                    time,
                                                    symptoms
                                                )
                                            }
                                        )
                                    }

                                    // ================= HISTORY =================
                                    is Routes.HistoryList -> NavEntry(key) {

                                        HistoryScreen(

                                            onBackClick = {
                                                backStack.removeLastOrNull()
                                            },

                                            items = viewModel.historyItems
                                        )
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
                                                    val repo = DataManager.getRepository()
                                                    repo?.savePatientProfile(updatedPatient)
                                                    
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