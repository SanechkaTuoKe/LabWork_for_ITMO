# PuTao — Equipment Manager

Десктопное приложение для учёта лабораторного оборудования, калибровок и технического обслуживания. Написано на **Kotlin** с использованием **Compose for Desktop** (Jetpack Compose Multiplatform), данные хранятся в **PostgreSQL**.

---

## Стек технологий

- **Kotlin 2.0.21** + **Compose Multiplatform 1.7.0** — UI и логика
- **Compose Material3** — компоненты интерфейса
- **PostgreSQL + JDBC** (`postgresql:42.7.1`) — персистентное хранилище
- **Calf File Picker** — нативный диалог выбора файла
- **Gradle** (Kotlin DSL) — сборка

---

## Архитектура

Проект организован по принципу многослойной архитектуры:

```
app/src/main/kotlin/org/example/
├── domain/           # Доменные модели и enum-типы
│   ├── Instrument, Calibration, Maintenance
│   └── InstrumentType, InstrumentStatus, CalibrationType, ...
├── validation/       # Валидаторы для каждой сущности
├── service/          # Бизнес-логика и CRUD-операции
│   ├── InstrumentService
│   ├── CalibrationService
│   └── MaintenanceService
├── auth/             # Аутентификация пользователей
│   ├── UserService
│   └── AuthState (Anon | LoggedIn)
├── storage/          # Слой хранилища
│   ├── DatabaseConfig.kt       # Подключение к PostgreSQL
│   └── saveLoad/               # Маппинг данных в/из БД и CSV
├── ui/               # Compose-интерфейс
│   ├── MainView.kt
│   ├── auth/         # Экран входа / регистрации
│   ├── instruments/  # Master/Detail + InstrumentController
│   │   └── dialogs/  # Диалоги добавления, редактирования, калибровки, ...
│   ├── components/   # Общие компоненты (EmptyState, ErrorDialog, ...)
│   └── theme/        # Тема приложения
└── App.kt            # Точка входа
```

---

## Требования

- **JDK 17+**
- **PostgreSQL 14+** (запущенный инстанс)

---

## Настройка базы данных

Приложение подключается к PostgreSQL, используя (в порядке приоритета):

1. **`app/src/main/resources/database.properties`** — файл конфигурации:

```properties
db.url=jdbc:postgresql://localhost:5432/putao
db.user=postgres
db.password=your_password
```

2. **Переменные окружения** — если файл не найден:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/putao
export DB_USER=postgres
export DB_PASSWORD=your_password
```

3. **Диалог подключения** — параметры можно ввести прямо в интерфейсе при запуске (кнопка «Connect to DB»).

Создайте базу данных вручную перед первым запуском:

```sql
CREATE DATABASE putao;
```

Схема таблиц создаётся автоматически при первом старте приложения.

---

## Запуск

```bash
./gradlew :app:run
```

---

## Сборка в JAR

```bash
./gradlew :app:packageUberJarForCurrentOS
```

Готовый файл: `app/build/compose/jars/app-windows-x64-1.0.0.jar`

---

## Функциональность

**Управление приборами**
- Добавление, редактирование, удаление приборов
- Смена статуса (ACTIVE / INACTIVE / REPAIR)
- Фильтрация и поиск списка

**Калибровки**
- Добавление записей о калибровке (тип, результат, дата, комментарий)
- История калибровок по каждому прибору
- Сортировка по дате (новые сверху)

**Техническое обслуживание**
- Добавление записей о сервисе/ремонте
- История обслуживания по прибору

**Аутентификация**
- Регистрация и вход пользователей
- Доступ к приборам разграничен по владельцу (`ownerUsername`)
- Загрузка приборов другого пользователя через «Load by Owner»

**Хранилище**
- Полная миграция в PostgreSQL через JDBC
- Экспорт/импорт данных через CSV (FilePickerDialog)
- Реконнект к БД из интерфейса без перезапуска

---

## Структура веток

| Ветка | Содержание |
|---|---|
| `main` | Базовая CLI-версия (Лаб. 1) |
| `Steps3,4` | Добавление CSV-хранилища |
| `corrections-andstep5` | Исправления и шаг 5 |
| `fixes-and-mini` | Мелкие фиксы |
| `FinalStep` | Финальная версия с Compose UI и PostgreSQL |

---

## Тесты

```bash
./gradlew :app:test
```

Покрыты: `InstrumentService`, `CalibrationService`, `MaintenanceService`, валидаторы (`InstrumentValidator`, `CalibrationValidator`, `MaintenanceValidator`).

Тестовая БД конфигурируется через `app/src/test/resources/database.properties`.
