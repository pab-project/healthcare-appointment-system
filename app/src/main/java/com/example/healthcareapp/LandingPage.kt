package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingPage(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onDoctorListClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onAdminClick: () -> Unit,
    onFindHospital: () -> Unit,
    onEmergencyCall: () -> Unit,
    isAdmin: Boolean = false
) {
    val scrollState = rememberScrollState()
    
    // Modern Theme Colors
    val primaryBlue = Color(0xFF1976D2)
    val secondaryBlue = Color(0xFF42A5F5)
    val backgroundColor = Color(0xFFF8FAFC)
    val cardContentColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
    ) {
        // --- MODERN HEADER WITH GRADIENT ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryBlue, secondaryBlue)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Datang di",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Healthcare App",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Profile Icon placeholder
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        onClick = onLoginClick
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar Placeholder
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = textMuted
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cari dokter atau rumah sakit...",
                            color = textMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- QUICK ACTIONS GRID ---
        Text(
            text = "Layanan Utama",
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = cardContentColor
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ServiceCard(
                title = "Janji Temu",
                icon = Icons.Rounded.AddCircleOutline,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                onClick = onAppointmentClick
            )
            ServiceCard(
                title = "Cari Dokter",
                icon = Icons.Rounded.PersonSearch,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f),
                onClick = onDoctorListClick
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ServiceCard(
                title = "Jadwal Saya",
                icon = Icons.Rounded.EventNote,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f),
                onClick = onScheduleClick
            )
            ServiceCard(
                title = "Akun Saya",
                icon = Icons.Rounded.AccountCircle,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
                onClick = onLoginClick
            )
        }

        // --- ADDITIONAL FEATURES ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Bantuan & Informasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = cardContentColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            FeatureActionRow(
                title = "Lokasi Rumah Sakit",
                subtitle = "Temukan faskes terdekat Anda",
                icon = Icons.Rounded.LocalHospital,
                iconColor = Color(0xFFE57373),
                onClick = onFindHospital
            )

            FeatureActionRow(
                title = "Panggilan Darurat",
                subtitle = "Bantuan medis segera (119)",
                icon = Icons.Rounded.PhoneInTalk,
                iconColor = Color(0xFFD32F2F),
                onClick = onEmergencyCall
            )

            if (isAdmin) {
                FeatureActionRow(
                    title = "Panel Admin",
                    subtitle = "Manajemen sistem & data",
                    icon = Icons.Rounded.AdminPanelSettings,
                    iconColor = Color(0xFF455A64),
                    onClick = onAdminClick
                )
            }
        }

        // --- FOOTER INFO ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = primaryBlue
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Gunakan aplikasi untuk booking yang lebih cepat dan mudah.",
                    fontSize = 13.sp,
                    color = primaryBlue,
                    lineHeight = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ServiceCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeatureActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF718096)
                )
            }
            
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E0)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingPagePreview() {
    LandingPage(
        onLoginClick = {},
        onRegisterClick = {},
        onDoctorListClick = {},
        onAppointmentClick = {},
        onScheduleClick = {},
        onAdminClick = {},
        onFindHospital = {},
        onEmergencyCall = {},
        isAdmin = true
    )
}
