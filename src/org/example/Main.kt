package org.example

import org.example.app.NotaryCaseApp
import org.example.domain.SortField
import org.example.models.NotaryCase
import org.example.ui.ConsoleUI
import org.example.ui.NotaryCaseFormatter
import org.example.utils.FileFormat
import java.time.LocalDate

private const val DEFAULT_JSON_PATH = "notary_cases.json"
private const val DEFAULT_CSV_PATH = "notary_cases.csv"

fun main() {
    val app = NotaryCaseApp()
    app.loadFromFile(DEFAULT_JSON_PATH, FileFormat.JSON)
        .onFailure { println(app.persistenceMessage(it)) }

    var choice: Int?
    do {
        println("\n===== СИСТЕМА УЧЁТА НОТАРИАЛЬНЫХ ДЕЛ =====")
        println("1. Ввод/вывод учётных записей")
        println("2. Отобразить все дела")
        println("3. Добавить новое дело")
        println("4. Редактировать дело")
        println("5. Удалить дело")
        println("6. Поиск дел")
        println("7. Сортировка дел")
        println("8. Фильтрация дел (по типу)")
        println("9. Агрегированные показатели (по сумме гонорара)")
        println("10. Сохранить данные в файл")
        println("11. Загрузить данные из файла")
        println("0. Выход")
        choice = ConsoleUI.readInt("Введите номер пункта: ")
        if (choice == null) {
            println("Ввод недоступен. Завершение программы.")
            break
        }

        when (choice) {
            1 -> ioSubmenu(app)
            2 -> displayAllCases(app)
            3 -> addNewCase(app)
            4 -> editCase(app)
            5 -> deleteCase(app)
            6 -> searchCases(app)
            7 -> sortCases(app)
            8 -> filterCases(app)
            9 -> showStatistics(app)
            10 -> saveData(app)
            11 -> loadData(app)
            0 -> println("Выход из программы.")
            else -> println("Неверный выбор. Пожалуйста, введите число от 0 до 11.")
        }
    } while (choice != 0)
}

private fun ioSubmenu(app: NotaryCaseApp) {
    var subChoice: Int?
    do {
        println("\n--- Ввод/вывод учётных записей ---")
        println("1. Сохранить данные в JSON-файл")
        println("2. Сохранить данные в CSV-файл")
        println("3. Загрузить данные из JSON-файла")
        println("4. Загрузить данные из CSV-файла")
        println("0. Назад")
        subChoice = ConsoleUI.readInt("Выберите действие: ")
        if (subChoice == null) {
            println("Ввод недоступен. Возврат в главное меню.")
            return
        }

        when (subChoice) {
            1 -> {
                val path = ConsoleUI.readString("Введите имя JSON-файла для сохранения (по умолчанию notary_cases.json): ")
                persistToFile(app, path.ifEmpty { DEFAULT_JSON_PATH }, FileFormat.JSON)
            }
            2 -> {
                val path = ConsoleUI.readString("Введите имя CSV-файла для сохранения (по умолчанию notary_cases.csv): ")
                persistToFile(app, path.ifEmpty { DEFAULT_CSV_PATH }, FileFormat.CSV)
            }
            3 -> {
                val path = ConsoleUI.readString("Введите имя JSON-файла для загрузки (по умолчанию notary_cases.json): ")
                loadFromFile(app, path.ifEmpty { DEFAULT_JSON_PATH }, FileFormat.JSON)
            }
            4 -> {
                val path = ConsoleUI.readString("Введите имя CSV-файла для загрузки (по умолчанию notary_cases.csv): ")
                loadFromFile(app, path.ifEmpty { DEFAULT_CSV_PATH }, FileFormat.CSV)
            }
            0 -> return
            else -> println("Неверный выбор.")
        }
    } while (true)
}

private fun displayAllCases(app: NotaryCaseApp) {
    val all = app.getAllCases()
    if (all.isEmpty()) {
        println("Список дел пуст.")
        return
    }
    println("\nВсе нотариальные дела (${all.size}):")
    all.forEach { println(NotaryCaseFormatter.toShortString(it)) }
}

private fun addNewCase(app: NotaryCaseApp) {
    println("\n--- Добавление нового дела ---")
    val caseNumber = ConsoleUI.readNotEmptyString("Номер дела: ")
    val clientName = ConsoleUI.readNotEmptyString("ФИО клиента: ")
    val caseType = ConsoleUI.readNotEmptyString("Тип нотариального действия: ")
    val openDate = ConsoleUI.readLocalDate("Дата открытия (ГГГГ-ММ-ДД): ") ?: LocalDate.now()
    val closeDateInput = ConsoleUI.readLocalDate("Дата закрытия (ГГГГ-ММ-ДД, или Enter если не закрыто): ")
    val fee = ConsoleUI.readDouble("Сумма гонорара (руб.): ") ?: 0.0

    val newCase = NotaryCase(
        id = 0,
        caseNumber = caseNumber,
        clientName = clientName,
        caseType = caseType,
        openDate = openDate,
        closeDate = closeDateInput,
        fee = fee
    )
    val added = app.addCase(newCase)
    println("Дело успешно добавлено с ID = ${added.id}")
}

private fun editCase(app: NotaryCaseApp) {
    val id = ConsoleUI.readInt("Введите ID дела для редактирования: ") ?: return
    val oldCase = app.findById(id)
    if (oldCase == null) {
        println("Дело с ID $id не найдено.")
        return
    }
    println("Текущие данные: ${NotaryCaseFormatter.toShortString(oldCase)}")
    println("Введите новые значения (оставьте пустым для сохранения старого):")

    val caseNumber = ConsoleUI.readString("Номер дела [${oldCase.caseNumber}]: ").ifEmpty { oldCase.caseNumber }
    val clientName = ConsoleUI.readString("ФИО клиента [${oldCase.clientName}]: ").ifEmpty { oldCase.clientName }
    val caseType = ConsoleUI.readString("Тип [${oldCase.caseType}]: ").ifEmpty { oldCase.caseType }
    val openDateStr = ConsoleUI.readString("Дата открытия [${oldCase.openDate}]: ")
    val openDate = if (openDateStr.isBlank()) {
        oldCase.openDate
    } else {
        runCatching { LocalDate.parse(openDateStr) }
            .getOrElse {
                println("Неверный формат даты. Оставлено старое значение: ${oldCase.openDate}")
                oldCase.openDate
            }
    }
    val closeDateStr = ConsoleUI.readString("Дата закрытия [${oldCase.closeDate ?: ""}]: ")
    val closeDate = when {
        closeDateStr.isBlank() -> oldCase.closeDate
        closeDateStr == "null" -> null
        else -> runCatching { LocalDate.parse(closeDateStr) }
            .getOrElse {
                println("Неверный формат даты. Оставлено старое значение: ${oldCase.closeDate ?: "в работе"}")
                oldCase.closeDate
            }
    }
    val feeStr = ConsoleUI.readString("Сумма [${oldCase.fee}]: ")
    val fee = feeStr.toDoubleOrNull() ?: oldCase.fee

    val updated = oldCase.copy(
        caseNumber = caseNumber,
        clientName = clientName,
        caseType = caseType,
        openDate = openDate,
        closeDate = closeDate,
        fee = fee
    )
    if (app.updateCase(id, updated)) {
        println("Дело успешно обновлено.")
    } else {
        println("Ошибка обновления.")
    }
}

private fun deleteCase(app: NotaryCaseApp) {
    val id = ConsoleUI.readInt("Введите ID дела для удаления: ") ?: return
    if (app.deleteCase(id)) {
        println("Дело с ID $id удалено.")
    } else {
        println("Дело с ID $id не найдено.")
    }
}

private fun searchCases(app: NotaryCaseApp) {
    val query = ConsoleUI.readString("Введите текст для поиска (по номеру, ФИО, типу): ")
    val results = app.search(query)
    if (results.isEmpty()) {
        println("Ничего не найдено.")
    } else {
        println("Найдено ${results.size}:")
        results.forEach { println(NotaryCaseFormatter.toShortString(it)) }
    }
}

private fun sortCases(app: NotaryCaseApp) {
    println("Выберите поле для сортировки:")
    println("1. ID")
    println("2. Номер дела")
    println("3. ФИО клиента")
    println("4. Дата открытия")
    println("5. Сумма гонорара")
    val fieldChoice = ConsoleUI.readInt("Ваш выбор: ")
    val field = when (fieldChoice) {
        1 -> SortField.ID
        2 -> SortField.CASE_NUMBER
        3 -> SortField.CLIENT_NAME
        4 -> SortField.OPEN_DATE
        5 -> SortField.FEE
        else -> {
            println("Неверный выбор, сортировка по ID по умолчанию.")
            SortField.ID
        }
    }
    val ascending = ConsoleUI.readString("По возрастанию? (y/n, по умолчанию y): ").lowercase() != "n"
    val sorted = app.sortBy(field, ascending)
    println("Отсортированный список:")
    sorted.forEach { println(NotaryCaseFormatter.toShortString(it)) }
}

private fun filterCases(app: NotaryCaseApp) {
    val type = ConsoleUI.readString("Введите тип дела для фильтрации: ")
    val filtered = app.filterByType(type)
    if (filtered.isEmpty()) {
        println("Нет дел с таким типом.")
    } else {
        println("Найдено ${filtered.size}:")
        filtered.forEach { println(NotaryCaseFormatter.toShortString(it)) }
    }
}

private fun showStatistics(app: NotaryCaseApp) {
    val stats = app.getFeeStatistics()
    println("Статистика по гонорарам:")
    println("Среднее: %.2f руб.".format(stats.average))
    println("Сумма: %.2f руб.".format(stats.sum))
    println("Минимум: %.2f руб.".format(stats.min))
    println("Максимум: %.2f руб.".format(stats.max))
}

private fun saveData(app: NotaryCaseApp) {
    val path = ConsoleUI.readString("Введите имя файла (с расширением .json или .csv): ")
    val format = detectFileFormat(path) ?: run {
        println("Неизвестное расширение, используйте .json или .csv")
        return
    }
    persistToFile(app, path, format)
}

private fun loadData(app: NotaryCaseApp) {
    val path = ConsoleUI.readString("Введите имя файла (с расширением .json или .csv): ")
    val format = detectFileFormat(path) ?: run {
        println("Неизвестное расширение, используйте .json или .csv")
        return
    }
    loadFromFile(app, path, format)
}

private fun loadFromFile(app: NotaryCaseApp, path: String, format: FileFormat) {
    app.loadFromFile(path, format)
        .onSuccess { println("Данные успешно загружены из файла $path (формат $format)") }
        .onFailure { println(app.persistenceMessage(it)) }
}

private fun persistToFile(app: NotaryCaseApp, path: String, format: FileFormat) {
    app.saveToFile(path, format)
        .onSuccess { println("Данные успешно сохранены в файл $path") }
        .onFailure { println(app.persistenceMessage(it)) }
}

private fun detectFileFormat(path: String): FileFormat? = when {
    path.endsWith(".json", ignoreCase = true) -> FileFormat.JSON
    path.endsWith(".csv", ignoreCase = true) -> FileFormat.CSV
    else -> null
}
