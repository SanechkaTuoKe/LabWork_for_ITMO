package org.example.domain

import java.time.Instant

class Calibration(
    val id: Long,
    val instrumentId: Long,
    val type: Int,
    val result: String,
    val comment: String?,
    val calibratedAt: Instant,
    val ownerUsername: String,
    val createdAt: Instant
){
    override fun toString(): String {
        return "id=$id\ntype=$type\nresult=$result"
    }
}