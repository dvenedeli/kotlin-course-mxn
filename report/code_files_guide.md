# Справка по файлам кода (нотариат)

Личные заметки: что за что отвечает. **Не для сдачи отчёта** — только чтобы быстро вспомнить структуру проекта.

Исходники лежат в `src/org/example/` (зеркало в `src/main/kotlin/org/example/` для Gradle, если используешь его).

## Как слои связаны

```
Main.kt  →  меню, ввод с клавиатуры
    ↓
NotaryCaseApp  →  «сервис»: CRUD + загрузка/сохранение
    ↓
NotaryCaseStore  →  список дел в памяти (логика)
    ↓
NotaryCaseRepository (JSON/CSV)  →  файлы на диске
```

---

## `src/org/example/`

### `Main.kt`
Точка входа (`main`). При старте загружает `notary_cases.json`. Главное меню (0–11): дела нотариуса — показ, добавление, правка, удаление, поиск, сортировка, фильтр по типу дела, статистика по гонорару, save/load. Обработчики меню и подменю ввода/вывода — здесь же.

### `app/NotaryCaseApp.kt`
Фасад для UI: все операции над `NotaryCaseStore`, загрузка/сохранение через `RepositoryFactory`, человекочитаемые сообщения об ошибках файлов.

### `models/NotaryCase.kt`
Запись дела: id, номер дела, клиент, тип, дата открытия, дата закрытия (может быть `null` — «в работе»), гонорар. JSON: обычная дата + nullable дата закрытия.

### `domain/NotaryCaseStore.kt`
In-memory хранилище дел: CRUD, авто-id, поиск по клиенту/номеру/типу, сортировка, фильтр по типу, статистика по `fee`, полная замена списка после load.

### `domain/SortField.kt`
Поля сортировки: ID, номер дела, имя клиента, дата открытия, гонорар.

### `domain/FeeStats.kt`
Агрегаты по гонорару: среднее, сумма, min, max (аналог `PriceStats` в проекте автомойки).

### `data/NotaryCaseRepository.kt`
Контракт load/save для списка `NotaryCase`.

### `data/RepositoryFactory.kt`
Выбор `JsonNotaryCaseRepository` или `CsvNotaryCaseRepository` по формату.

### `data/JsonNotaryCaseRepository.kt`
JSON-массив дел; те же типы ошибок, что и у автомойки.

### `data/CsvNotaryCaseRepository.kt`
Файловый CSV-репозиторий поверх `CsvNotaryCaseCodec`.

### `data/CsvNotaryCaseCodec.kt`
7 полей + пустая `closeDate` в CSV означает «дело не закрыто»; encode/decode построчно.

### `data/JsonConfig.kt`
Настройки `Json` для сериализации.

### `data/PersistenceError.kt`
Ошибки persistence: FileNotFound, ParseError, IoError.

### `ui/ConsoleUI.kt`
Общий ввод с консоли (тот же паттерн, что в carwash).

### `ui/NotaryCaseFormatter.kt`
Краткая строка для вывода дела в списке (в т.ч. «в работе», если `closeDate == null`).

### `utils/FileFormat.kt`
JSON или CSV.

### `serialization/LocalDateSerializer.kt`
Два объекта в одном файле:
- **`LocalDateSerializer`** — обязательная дата (`openDate`);
- **`NullableLocalDateSerializer`** — опциональная `closeDate` (null в JSON и пустая ячейка в CSV).

---

## `test/org/example/`

| Файл | Что проверяет |
|------|----------------|
| `app/NotaryCaseAppTest.kt` | Save/load через app; ошибка при missing file |
| `domain/NotaryCaseStoreTest.kt` | CRUD, поиск, сортировка, фильтр, статистика, replaceAll |
| `data/CsvNotaryCaseCodecTest.kt` | Кодек CSV, в т.ч. пустая дата закрытия |
| `data/CsvNotaryCaseRepositoryTest.kt` | Репозиторий CSV на временном файле |
| `data/JsonNotaryCaseRepositoryTest.kt` | Репозиторий JSON |

---

## Скрипты и сборка (корень `project/`)

| Файл | Назначение |
|------|------------|
| `compile-main.bat` / `.sh` | Компиляция основного кода |
| `test.bat` / `.sh` | Тесты |
| `run.bat` / `.sh` | Запуск `org.example.MainKt` |
| `lib-*.bat` / `.sh` | Classpath для compile/run/test |
| `build.gradle.kts`, `settings.gradle.kts` | Gradle (опционально) |

`lib/`, `tools/` — зависимости и компилятор, не логика приложения.

---

## Данные (не Kotlin)

| Файл | Назначение |
|------|------------|
| `notary_cases.json` | Данные по умолчанию при запуске |
| `notary_cases.csv` | Тот же набор в CSV |

---

## Отличия от проекта `carwash/`

| Аспект | carwash | project (нотариат) |
|--------|---------|---------------------|
| Сущность | `CarWashClient` | `NotaryCase` |
| Статистика | `PriceStats` / цена услуги | `FeeStats` / гонорар |
| Фильтр | по типу **услуги** | по **типу дела** |
| Даты | одна (`visitDate`) | `openDate` + nullable `closeDate` |
| CSV-колонки | 8 | 7 (+ пустой closeDate) |

Архитектура (Main → App → Store → Repository) намеренно одинаковая — два варианта одной курсовой темы.

---

## Папка `report/` (кроме этого файла)

Материалы для отчёта и защиты; на работу программы не влияют.
