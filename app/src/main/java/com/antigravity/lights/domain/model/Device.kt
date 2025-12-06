package com.antigravity.lights.domain.model

data class Device(
    val id: String,
    val name: String,
    val ipAddress: String,
    val isOnline: Boolean
)
