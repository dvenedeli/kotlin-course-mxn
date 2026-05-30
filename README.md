# Система учёта нотариальных дел

Консольное Kotlin JVM-приложение с меню.

## Требования

- **JDK 11+**
- **Kotlin 1.9.x** — `kotlinc` в `PATH`, `KOTLIN_HOME`, или автоустановка в `tools/kotlin-1.9.24/` при первой загрузке зависимостей
- **Сеть** — при первом `run.bat` / `test.bat` JAR скачиваются в `lib/` (папка в `.gitignore`)

## Структура папки `project/`

| Путь | Назначение |
|------|------------|
| `deps.list` | Список зависимостей (Maven Central + plugin из дистрибутива Kotlin) |
| `download-deps.bat` / `download-deps.sh` | Явная загрузка зависимостей в `lib/` |
| `lib/` | Скачанные JAR (не в git) |
| `src/org/example/` | Точка входа `Main.kt` |
| `test/org/example/` | Unit- и интеграционные тесты |
| `run.bat`, `test.bat`, `run.sh`, `test.sh` | Сборка и запуск |
| `notary_cases.json` | Пример данных |
| `build/` | Артефакты компиляции (в `.gitignore`) |

### Зависимости (версии в `deps.list`)

| Компонент | Версия |
|-----------|--------|
| Kotlin stdlib, reflect, test | 1.9.24 |
| kotlinx-serialization | 1.6.0 |
| kotlinx-serialization-compiler-plugin | 1.9.24 |
| JUnit Platform console | 1.10.2 |

## Запуск

```bash
cd project
run.bat            # Windows — при первом запуске скачает lib/
test.bat           # Windows — автотесты
./run.sh           # Linux/macOS
./test.sh
```

Только загрузить зависимости без сборки:

```bash
download-deps.bat   # Windows
./download-deps.sh  # Linux/macOS
```
