package com.example.healthcareapp



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // 🔥 Start langsung dari Login
            val backStack = remember { mutableStateListOf<Any>(Routes.Login) }

            val patient = remember {
                Patient(
                    id = 1,
                    name = "Berly Marcellino",
                    email = "berly@email.com",
                    phone = "08123456789",
                    gender = "Laki-laki",
                    birthDate = "2004-01-01",
                    address = "Klaten, Indonesia"
                )
            }

            HealthcareTheme {
                CompositionLocalProvider(LocalBackStack provides backStack) {

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },

                        entryProvider = { key ->
                            when (key) {

                                // ================= LOGIN =================
                                is Routes.Login -> NavEntry(key) {
                                    LoginScreen(
                                        onLoginClick = { email, password ->
                                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                                backStack.clear()
                                                backStack.add(Routes.Home)
                                            }
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
                                    val doctor = DataManager.doctors.find {
                                        it.id == key.doctorId
                                    }

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
                                    AppointmentListScreen(
                                        appointments = DataManager.appointments,
                                        onBackClick = { backStack.removeLastOrNull() },

                                        // 🔥 FIX: tidak looping lagi
                                        onAddAppointmentClick = {
                                            backStack.add(Routes.FormAppointment)
                                        },

                                        onDetailClick = { appointment ->
                                            backStack.add(
                                                Routes.AppointmentDetail(appointment.id)
                                            )
                                        }
                                    )
                                }

                                // ================= APPOINTMENT DETAIL =================
                                is Routes.AppointmentDetail -> NavEntry(key) {

                                    val appointment = DataManager.appointments.find {
                                        it.id == key.id
                                    }

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
                                        onBackClick = { backStack.removeLastOrNull() },
                                        onConfirmClick = { patientName, doctorName, date, time, symptoms ->
                                            DataManager.addAppointment(patientName, doctorName, date, time, symptoms)
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
                                        onMedicalRecordClick = {
                                            backStack.add(Routes.HistoryList)
                                        },
                                        onAppointmentClick = {
                                            backStack.add(Routes.AppointmentList)
                                        },
                                        onBackClick = { backStack.removeLastOrNull() },
                                        onLogoutClick = {
                                            backStack.clear()
                                            backStack.add(Routes.Login)
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