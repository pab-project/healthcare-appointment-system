package com.example.healthcareapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthcareapp.data.entity.NotificationEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notifications: List<NotificationEntity>,
    currentUserEmail: String,
    onBackClick: () -> Unit,
    onMarkAsRead: (Int) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteNotification: (Int) -> Unit
) {
    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifikasi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0)),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        IconButton(onClick = onMarkAllAsRead) {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
                                contentDescription = "Tandai semua dibaca",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
        ) {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F0FE),
                            modifier = Modifier.size(96.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.NotificationsNone,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Belum ada Notifikasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Anda akan melihat update penting dan berita terbaru di sini.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${notifications.size} Notifikasi",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            if (notifications.any { it.isRead }) {
                                // optional clear button
                            }
                        }
                    }

                    items(notifications, key = { it.id }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onItemClick = {
                                if (!notification.isRead) {
                                    onMarkAsRead(notification.id)
                                }
                            },
                            onDeleteClick = {
                                onDeleteNotification(notification.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isUnread = !notification.isRead

    val cardColor = if (isUnread) Color(0xFFEDF4FE) else Color.White
    val borderStroke = if (isUnread) BorderStroke(1.dp, Color(0xFFC2D9FC)) else null

    val (icon, color) = when (notification.type) {
        "APPOINTMENT_CREATED" -> Icons.Rounded.CalendarMonth to Color(0xFF2196F3)
        "APPOINTMENT_APPROVED" -> Icons.Rounded.CheckCircleOutline to Color(0xFF4CAF50)
        "APPOINTMENT_REJECTED" -> Icons.Rounded.HighlightOff to Color(0xFFE53935)
        else -> Icons.Rounded.Info to Color(0xFFFF9800)
    }

    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val formattedTime = sdf.format(Date(notification.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Unread Dot
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = if (isUnread) TextPrimary else TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = TextHint
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete action
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Hapus",
                    tint = TextHint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    val dummy = listOf(
        NotificationEntity(1, "Jadwal Disetujui 🩺", "Janji temu Anda dengan Dr. Andi Wijaya telah dikonfirmasi.", isRead = false, type = "APPOINTMENT_APPROVED"),
        NotificationEntity(2, "Pengajuan Janji Temu Baru 📅", "Pasien Berly Marcellino mengajukan janji temu baru.", isRead = true, type = "APPOINTMENT_CREATED"),
        NotificationEntity(3, "Welcome! 🎉", "Selamat datang di Healthcare appointment system.", isRead = true, type = "INFO")
    )
    HealthcareTheme {
        NotificationScreen(
            notifications = dummy,
            currentUserEmail = "test@healthcare.com",
            onBackClick = {},
            onMarkAsRead = {},
            onMarkAllAsRead = {},
            onDeleteNotification = {}
        )
    }
}
