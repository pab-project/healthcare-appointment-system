package com.example.healthcareapp

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf

val LocalBackStack = staticCompositionLocalOf<SnapshotStateList<Any>> {
    error("No BackStack provided")
}
