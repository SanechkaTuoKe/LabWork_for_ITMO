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
    - `Instrument`, `Calibration`, `Maintenance` и соответствующие enum-типы (`InstrumentType`, `InstrumentStatus`, `CalibrationType`, `CalibrationResult`, `MaintenanceType`).
2. **validation** — отдельные валидаторы для каждой сущности:
    - Проверка корректности полей при создании и обновлении объектов.
3. **service** — менеджеры коллекций:
    - Содержат структуры хранения (`TreeMap`), генерацию `id`, CRUD-операции и дополнительные методы для поддержки CLI-команд.
4. **cli** — обработка команд пользователя:
    - Командный интерпретатор с интерактивным вводом.
    - Обработка ошибок, в том числе неизвестных команд, неверных аргументов и нарушений ограничений валидаторов.

---

## CLI-команды

| Команда | Описание |
|---------|----------|
| `inst_add` | Добавить прибор (интерактивно) |
| `inst_list [--type TYPE] [--status STATUS]` | Список приборов |
| `inst_show <id>` | Показать карточку прибора и последнюю калибровку |
| `inst_update <id> field=value ...` | Обновление полей прибора |
| `cal_add <instrument_id>` | Добавить калибровку |
| `cal_list <instrument_id> [--last N]` | Список калибровок прибора |
| `cal_show <calibration_id>` | Показать калибровку |
| `maint_add <instrument_id>` | Добавить обслуживание/ремонт |
| `maint_list <instrument_id> [--last N]` | Список обслуживаний |
| `inst_due [--days N]` | Приборы, которым скоро нужна калибровка |
| `help` | Справка |
| `exit` | Выход |

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
```

### 2. Валидация типа калибровки

```kotlin
fun validateType(input: String): CalibrationType {
    return try {
        CalibrationType.valueOf(input.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Invalid calibration type. Allowed: ONE_POINT, TWO_POINT"
        )
    }
}
```

### 3. Обработка ошибок в CLI

```kotlin
object ErrorHandler {
    fun handle(e: Exception) {
        println("Error: ${e.message}")
    }
}
```

---

## Тестирование приложения

Для обеспечения надежности и корректности работы приложения были разработаны  тесты.

### 1. Тестирование сервисов

#### InstrumentServiceTest
Тестирует CRUD-операции с приборами:
- Добавление прибора с валидными данными
- Добавление прибора без инвентарного номера
- Получение прибора по ID
- Обновление полей прибора
- Удаление прибора
- Проверка существования прибора

```kotlin
@Test
fun `add - should add instrument with valid data`() {
    val instrument = service.add(
        name = "pH Meter",
        type = InstrumentType.PH_METER,
        inventoryNumber = "INV-001",
        location = "Lab-1"
    )
    
    assertNotNull(instrument)
    assertEquals("pH Meter", instrument.name)
    assertEquals(InstrumentType.PH_METER, instrument.type)
}
```

#### CalibrationServiceTest
Тестирует работу с калибровками:
- Добавление калибровки с валидными параметрами
- Обработка пустого комментария
- Проверка ошибок при несуществующем приборе
- Получение списка калибровок с ограничением по количеству

#### MaintenanceServiceTest
Тестирует обслуживание приборов:
- Добавление записи о техническом обслуживании
- Проверка валидации пустых полей
- Получение списка обслуживаний

### 2. Тестирование валидаторов

#### InstrumentValidatorTest
Проверяет корректность валидации:
- Имя не может быть пустым
- Тип прибора должен быть в диапазоне 1-5
- Инвентарный номер не может быть пустым
- Локация не может быть пустой

```kotlin
@Test
fun `validateName - should throw for blank name`() {
    assertThrows<IllegalArgumentException> { 
        InstrumentValidator.validateName("") 
    }
    assertThrows<IllegalArgumentException> { 
        InstrumentValidator.validateName("   ") 
    }
}
```

#### CalibrationValidatorTest
Проверяет валидацию калибровок:
- Корректные типы калибровки (ONE_POINT, TWO_POINT)
- Корректные результаты (OK, FAIL)
- Обработка регистра символов
- Обработка пустых комментариев

#### MaintenanceValidatorTest
Проверяет валидацию обслуживания:
- Корректные типы обслуживания (SERVICE, REPAIR)
- Проверка на пустые детали обслуживания

### 3. Тестирование CLI-обработчиков

#### InstAddHandlerTest
Тестирует интерактивное добавление прибора:
- Успешное добавление с валидными данными
- Добавление без инвентарного номера
- Обработка ошибок: пустое имя, неверный тип, пустая локация

```kotlin
@Test
fun `handle - should add instrument with valid input`() {
    provideInput(
        "pH Meter Mettler",
        "1",
        "INV-001",
        "Lab-2 bench"
    )
    
    val result = handler.handle(
        params = emptyList(),
        instrumentService = instrumentService,
        commandList = emptyList()
    )
    
    assertTrue(result)
    assertTrue(outContent.toString().contains("OK instrument_id="))
}
```

#### MaintListHandlerTest
Тестирует вывод списка обслуживания:
- Отображение записей обслуживания
- Фильтрация по параметру `--last`
- Обработка пустого списка
- Обработка неверных параметров

### 4. Тестирование CommandService

Проверяет работу командного интерпретатора:
- Обработка неизвестных команд
- Выполнение команды `help`
- Выполнение команды `exit`
- Обработка пустого ввода
- Выполнение команды `inst_list`

```kotlin
@Test
fun `execute - should handle invalid command and print error`() {
    val result = commandService.execute(listOf("unknown_command"))
    assertTrue(result)
    assertTrue(outContent.toString().contains("Error: command not found"))
}
```

### 5. Тестирование ErrorHandler

Проверяет вывод сообщений об ошибках:
- Форматирование сообщения с префиксом "Error:"
- Обработка исключений с пустым сообщением

### Результаты тестирования

Все тесты были успешно выполнены. Общее количество тестов: **68**  
Результат: **пройдено**

| Компонент | Количество тестов | Статус |
|-----------|------------------|--------|
| InstrumentService | 9 |  пройдено |
| CalibrationService | 6 |  пройдено |
| MaintenanceService | 5 |  пройдено |
| InstrumentValidator | 6 |  пройдено |
| CalibrationValidator | 8 |  пройдено |
| MaintenanceValidator | 5 | пройдено |
| InstAddHandler | 7 | пройдено |
| MaintListHandler | 8 |  пройдено |
| CommandService | 8 |  пройдено |
| ErrorHandler | 2 |  пройдено |
| **Итого** | **68** | *пройдено* |

---

## Выводы по работе

В ходе выполнения лабораторной работы было разработано консольное приложение для управления лабораторным оборудованием, калибровками и обслуживанием приборов.

