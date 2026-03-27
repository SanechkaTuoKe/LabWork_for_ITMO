# Лабораторная работа 1: Управление лабораторным оборудованием и калибровками

## Цель работы

Цель данной лабораторной работы — разработка консольного приложения для учета лабораторного оборудования, калибровок и обслуживания приборов. Программа должна обеспечивать:

- Ввод и хранение информации о приборах (Instrument), калибровках (Calibration) и обслуживании (Maintenance).
- Валидацию данных при создании и обновлении объектов.
- Интерактивное добавление объектов через CLI.
- Надежное управление коллекциями объектов в памяти с уникальными идентификаторами.

---

## Структура проекта и слои

Проект организован по принципу многослойной архитектуры:

1. **domain** — классы предметной области:
    - `Instrument`, `Calibration`, `Maintenance` и соответствующие enum-типов (`InstrumentType`, `InstrumentStatus`, `CalibrationType`, `CalibrationResult`, `MaintenanceType`).
2. **validation** — отдельные валидаторы для каждой сущности:
    - Проверка корректности полей при создании и обновлении объектов.
3. **service** — менеджеры коллекций:
    - Содержат структуры хранения (`TreeMap`), генерацию `id`, CRUD-операции и дополнительные методы для поддержки CLI-команд.
4. **cli** — обработка команд пользователя:
    - Командный интерпретатор с интерактивным вводом.
    - Обработка ошибок, в том числе неизвестных команд, неверных аргументов и нарушений ограничений валидаторов.

---

## CLI-команды

- `inst_add` — добавить прибор (интерактивно)
- `inst_list [--type TYPE] [--status STATUS]` — список приборов
- `inst_show <id>` — показать карточку прибора и последнюю калибровку
- `inst_update <id> field=value ...` — обновление полей прибора
- `cal_add <instrument_id>` — добавить калибровку
- `cal_list <instrument_id> [--last N]` — список калибровок прибора
- `cal_show <calibration_id>` — показать калибровку
- `maint_add <instrument_id>` — добавить обслуживание/ремонт
- `maint_list <instrument_id> [--last N]` — список обслуживаний
- `inst_due [--days N]` — приборы, которым скоро нужна калибровка
- `help` — справка
- `exit` — выход

Ошибки обрабатываются на всех уровнях, чтобы программа не падала:  
неверный id, неизвестная команда, пустые или некорректные поля, прибор вне работы и т.п.

---

## UML-диаграмма классов

![UML диаграмма](http://www.plantuml.com/plantuml/svg/VLExJiCm5Dtz5L4Nfl89gAWW5QaiBR2R-0eY72Voem8XXl95Y8KEY6zm_uWeXVRKZsGy3v_xk9aOoxLrjMIivUKJVw1Y99gQLsfKlBAY-qh9ZIckbB7QrQGiW5CkgtlDRTKeGAyxioN5LKciXqcv6XwuUMafXIyjjyuS67WTBCV4WeoJza11TkzvWxPMdKtqhbnhTpj5ezCzfcW8FD2OQaYlgf9oaSiTdGu4Cexx9p6ibDNf8rT2aZPnuhaGinqP4FFAs3HpsmfkASrTk3rnN3xc88unOK_W2PYCQqQoZHsNpzLU70xcRnF1OAPDNNU6UDJpfcbZT45Sa8RPdnJ-ot_uJx_Phlt6V_kVxNhxxZSisd4n7f_XVmIBHWN03DQD4e1Pxfae7XyR5hB47p5GF6govOCeQW0saAqf5c7IW2hgEfy8DvYE6nH4Typw2mB1iIUaWzDY4rB2rV8N)

---

## Примеры интересных блоков кода

### 1. Добавление прибора через `InstrumentService`

```kotlin
fun add(
    name: String,
    type: InstrumentType,
    inventoryNumber: String?,
    location: String,
    ownerUsername: String = "SYSTEM"
): Instrument {
    InstrumentValidator.validateName(name)
    InstrumentValidator.validateLocation(location)

    if (inventoryNumber != null && inventoryNumber.isNotBlank()) {
        InstrumentValidator.validateInventory(inventoryNumber)
    }

    val now = Instant.now()
    val instrument = Instrument(
        id = nextId++,
        name = name,
        type = type,
        inventoryNumber = inventoryNumber?.takeIf { it.isNotBlank() },
        location = location,
        status = InstrumentStatus.ACTIVE,
        ownerUsername = ownerUsername,
        createdAt = now,
        updatedAt = now
    )
    instruments[instrument.id] = instrument
    return instrument
}

