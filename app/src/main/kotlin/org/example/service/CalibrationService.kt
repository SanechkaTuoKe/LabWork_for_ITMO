package org.example.service

import org.example.domain.Calibration
import org.example.domain.InstrumentStatus
import org.example.validation.CalibrationValidator
import java.time.Instant
import java.util.*

class CalibrationService(private val instrumentService: InstrumentService) {

    private val calibrations = TreeMap<Long, Calibration>()
    private var nextId = 1L

    // Добавление калибровки
    fun add(
        instrumentId: Long,
        type: Int,
        result: String,
        comment: String? = "",
        ownerUsername: String = "SYSTEM",
        calibratedAt: Instant = Instant.now()
    ): Calibration {
        val instrument = instrumentService.getById(instrumentId)
            ?: throw IllegalArgumentException("Ошибка: прибор с id=$instrumentId не найден")

        if (instrument.status != InstrumentStatus.ACTIVE) {
            throw IllegalArgumentException("Ошибка: прибор не в работе")
        }

        CalibrationValidator.validateResult(result)
        CalibrationValidator.validateComment(comment)

        val calibration = Calibration(
            id = nextId++,
            instrumentId = instrumentId,
            type = type,
            result = result,
            comment = comment ?: "",
            calibratedAt = calibratedAt,
            ownerUsername = ownerUsername,
            createdAt = Instant.now()
        )

        calibrations[calibration.id] = calibration
        return calibration
    }

    // Получить калибровку по ID
    fun getById(id: Long): Calibration =
        calibrations[id] ?: throw NoSuchElementException("Ошибка: калибровка с id=$id не найдена")

    // Список калибровок прибора, с опцией взять только последние N
    fun listByInstrument(instrumentId: Long, last: Int? = null): List<Calibration> {
        if (instrumentService.getById(instrumentId) == null) {
            throw IllegalArgumentException("Ошибка: прибор с id=$instrumentId не найден")
        }

        val result = calibrations.values
            .filter { it.instrumentId == instrumentId }
            .sortedByDescending { it.calibratedAt }

        return if (last != null && last > 0) result.take(last) else result
    }

    // Метод для InstrumentService и CalListHandler
    fun listForInstrument(instrumentId: Long): List<Calibration> =
        listByInstrument(instrumentId)

    // Метод для удаления калибровок прибора
    fun removeByInstrumentId(instrumentId: Long) =
        calibrations.filterValues { it.instrumentId == instrumentId }
            .keys.forEach { calibrations.remove(it) }

    // Дополнительно: получить все калибровки прибора как Map (id -> Calibration)
    fun getByInstrumentID(instrumentID: Long): Map<Long, Calibration> =
        calibrations.filterValues { it.instrumentId == instrumentID }
}