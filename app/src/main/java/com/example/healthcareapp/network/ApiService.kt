package com.example.healthcareapp.network

import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(): Response<GenericResponse>

    @GET("me")
    suspend fun getMe(): Response<UserDetailResponse>

    @GET("doctors")
    suspend fun getDoctors(
        @Query("specialization") specialization: String? = null,
        @Query("search") search: String? = null
    ): Response<DoctorListResponse>

    @GET("doctors/{id}")
    suspend fun getDoctorDetail(
        @Path("id") id: Int
    ): Response<DoctorDetailResponse>

    @GET("doctors/{id}/slots")
    suspend fun getDoctorSlots(
        @Path("id") doctorId: Int
    ): Response<TimeSlotListResponse>

    // Patient routes
    @GET("patient/profile")
    suspend fun getPatientProfile(): Response<PatientProfileResponse>

    @PUT("patient/profile")
    suspend fun updatePatientProfile(
        @Body request: UpdatePatientRequest
    ): Response<PatientProfileResponse>

    @GET("patient/appointments")
    suspend fun getPatientAppointments(
        @Query("status") status: String? = null
    ): Response<AppointmentListResponse>

    @POST("patient/appointments")
    suspend fun createAppointment(
        @Body request: StoreAppointmentRequest
    ): Response<AppointmentDetailResponse>

    @PATCH("patient/appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Path("id") id: Int
    ): Response<GenericResponse>

    @GET("patient/medical-records")
    suspend fun getPatientMedicalRecords(): Response<MedicalRecordListResponse>

    // Doctor routes
    @GET("doctor/profile")
    suspend fun getDoctorProfile(): Response<DoctorDetailResponse>

    @PUT("doctor/profile")
    suspend fun updateDoctorProfile(
        @Body request: UpdateDoctorRequest
    ): Response<DoctorDetailResponse>

    @GET("doctor/appointments")
    suspend fun getDoctorSchedule(
        @Query("status") status: String? = null
    ): Response<AppointmentListResponse>

    // Doctor timeslot CRUD
    @GET("doctor/slots")
    suspend fun getDoctorOwnSlots(): Response<TimeSlotListResponse>

    @POST("doctor/slots")
    suspend fun createDoctorSlot(
        @Body request: CreateTimeSlotRequest
    ): Response<SingleTimeSlotResponse>

    @PUT("doctor/slots/{id}")
    suspend fun updateDoctorSlot(
        @Path("id") id: Int,
        @Body request: CreateTimeSlotRequest
    ): Response<SingleTimeSlotResponse>

    @DELETE("doctor/slots/{id}")
    suspend fun deleteDoctorSlot(
        @Path("id") id: Int
    ): Response<GenericResponse>

    @PATCH("doctor/appointments/{id}/done")
    suspend fun markAppointmentDone(
        @Path("id") id: Int,
        @Body request: CompleteAppointmentRequest
    ): Response<AppointmentDetailResponse>

    // Admin routes
    @GET("admin/appointments")
    suspend fun getAdminAppointments(
        @Query("status") status: String? = null
    ): Response<AppointmentListResponse>

    @PATCH("admin/appointments/{id}/approve")
    suspend fun approveAppointment(
        @Path("id") id: Int
    ): Response<AppointmentDetailResponse>

    @PATCH("admin/appointments/{id}/reject")
    suspend fun rejectAppointment(
        @Path("id") id: Int,
        @Body request: RejectAppointmentRequest
    ): Response<AppointmentDetailResponse>

    @GET("admin/patients")
    suspend fun getAdminPatients(
        @Query("search") search: String? = null
    ): Response<PatientListResponse>

    @POST("admin/doctors")
    suspend fun createDoctor(
        @Body request: CreateDoctorRequest
    ): Response<DoctorDetailResponse>

    @PUT("admin/doctors/{id}")
    suspend fun updateDoctorByAdmin(
        @Path("id") id: Int,
        @Body request: CreateDoctorRequest // Reuse same params or subset
    ): Response<DoctorDetailResponse>

    @DELETE("admin/doctors/{id}")
    suspend fun deleteDoctorByAdmin(
        @Path("id") id: Int
    ): Response<GenericResponse>

    @DELETE("admin/patients/{id}")
    suspend fun deletePatientByAdmin(
        @Path("id") id: Int
    ): Response<GenericResponse>

    // Admin timeslot routes
    @GET("admin/doctors/{doctorId}/slots")
    suspend fun getAdminDoctorSlots(
        @Path("doctorId") doctorId: Int
    ): Response<TimeSlotListResponse>

    @POST("admin/doctors/{doctorId}/slots")
    suspend fun createAdminDoctorSlot(
        @Path("doctorId") doctorId: Int,
        @Body request: CreateTimeSlotRequest
    ): Response<SingleTimeSlotResponse>

    @PUT("admin/slots/{id}")
    suspend fun updateAdminDoctorSlot(
        @Path("id") id: Int,
        @Body request: CreateTimeSlotRequest
    ): Response<SingleTimeSlotResponse>

    @DELETE("admin/slots/{id}")
    suspend fun deleteAdminDoctorSlot(
        @Path("id") id: Int
    ): Response<GenericResponse>
}

// Request Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class StoreAppointmentRequest(
    @SerializedName("time_slot_id") val timeSlotId: Int,
    val notes: String?
)

data class UpdatePatientRequest(
    val name: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    val gender: String?,
    val address: String?,
    val phone: String?
)

data class UpdateDoctorRequest(
    val name: String?,
    val specialization: String?,
    val phone: String?,
    val bio: String?
)

data class CreateDoctorRequest(
    val name: String,
    val email: String,
    val password: String? = null, // optional on update
    val specialization: String,
    val phone: String? = null,
    val bio: String? = null
)

data class RejectAppointmentRequest(
    val reason: String?
)

data class UserDetailResponse(
    val status: String,
    val data: UserResponse
)

data class CompleteAppointmentRequest(
    val diagnosis: String?,
    val treatment: String?,
    val medications: String?,
    val notes: String?
)

data class CreateTimeSlotRequest(
    val date: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String
)
