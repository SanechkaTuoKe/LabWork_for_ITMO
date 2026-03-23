package org.example.domain

import java.time.Instant

class Instrument(
    val id: Long,
    var name: String,
    val type: InstrumentType,
    var inventoryNumber: String?,
    var location: String,
    var status: InstrumentStatus,
    val ownerUsername: String,
    val createdAt: Instant,
    var updatedAt: Instant

) {
    override fun toString(): String {
        return "id=$id\nname=$name\ntype=$type\ninventoryNumber=$inventoryNumber\nlocation=$location"+
                "createdAt=$createdAt"
    }
}