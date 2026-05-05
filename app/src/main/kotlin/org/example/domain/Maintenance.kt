package org.example.domain

import java.time.Instant

class Maintenance(
    val id: Long,
    val instrumentId: Long,
    val type: MaintenanceType,
    val details: String,
    val doneAt: Instant,
    val ownerUsername: String,
    val createdAt: Instant
)