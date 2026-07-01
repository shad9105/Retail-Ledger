package com.example.ui.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.SaleEntry
import com.example.data.repository.SaleRepository
import com.example.utils.ExcelExporter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    application: Application,
    private val repository: SaleRepository
) : AndroidViewModel(application) {

    // Current date/time auto-fill helpers
    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.US).format(Date())
    }

    // Selected Date for Today's view (defaults to today, editable)
    private val _selectedDateFilter = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    )
    val selectedDateFilter = _selectedDateFilter.asStateFlow()

    // Flow of entries for the selected date
    val salesForSelectedDate: StateFlow<List<SaleEntry>> = _selectedDateFilter
        .flatMapLatest { date ->
            repository.getEntriesByDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All sales flow (useful for general stats or debugging)
    val allSales: StateFlow<List<SaleEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Selected month and year for Excel export & monthly review
    private val _selectedExportYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedExportYear = _selectedExportYear.asStateFlow()

    private val _selectedExportMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1) // 1-indexed
    val selectedExportMonth = _selectedExportMonth.asStateFlow()

    // Monthly entries for review
    private val _monthlyEntries = MutableStateFlow<List<SaleEntry>>(emptyList())
    val monthlyEntries = _monthlyEntries.asStateFlow()

    // Export status tracking
    private val _exportState = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportState = _exportState.asStateFlow()

    // History tab states for fully searchable, customizable historical views
    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery = _historySearchQuery.asStateFlow()

    private val _historySelectedYear = MutableStateFlow("All")
    val historySelectedYear = _historySelectedYear.asStateFlow()

    private val _historySelectedMonth = MutableStateFlow("All")
    val historySelectedMonth = _historySelectedMonth.asStateFlow()

    private val _historySelectedDay = MutableStateFlow("All")
    val historySelectedDay = _historySelectedDay.asStateFlow()

    private val _historySelectedCategory = MutableStateFlow("All")
    val historySelectedCategory = _historySelectedCategory.asStateFlow()

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun setHistorySelectedYear(year: String) {
        _historySelectedYear.value = year
    }

    fun setHistorySelectedMonth(month: String) {
        _historySelectedMonth.value = month
    }

    fun setHistorySelectedDay(day: String) {
        _historySelectedDay.value = day
    }

    fun setHistorySelectedCategory(category: String) {
        _historySelectedCategory.value = category
    }

    // Dynamic list of available years in the database
    val availableYears: StateFlow<List<String>> = allSales.map { sales ->
        val years = sales.mapNotNull { 
            val parts = it.date.split("-")
            parts.getOrNull(0)
        }.distinct().sortedDescending()
        listOf("All") + years
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("All")
    )

    // Dynamic list of available categories in the database
    val availableCategories: StateFlow<List<String>> = allSales.map { sales ->
        val cats = sales.map { it.category }.distinct().sorted()
        listOf("All") + cats
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("All")
    )

    // Main filtered history flow combining search and year/month/day/category filters reactively
    val filteredHistorySales: StateFlow<List<SaleEntry>> = combine(
        allSales,
        _historySearchQuery,
        _historySelectedYear,
        _historySelectedMonth,
        _historySelectedDay,
        _historySelectedCategory
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val sales = flows[0] as List<SaleEntry>
        val query = flows[1] as String
        val year = flows[2] as String
        val month = flows[3] as String
        val day = flows[4] as String
        val category = flows[5] as String

        sales.filter { sale ->
            val parts = sale.date.split("-")
            val saleYear = parts.getOrNull(0) ?: ""
            val saleMonth = parts.getOrNull(1) ?: ""
            val saleDay = parts.getOrNull(2) ?: ""

            val matchesSearch = query.isBlank() || 
                    sale.itemName.contains(query, ignoreCase = true) ||
                    sale.category.contains(query, ignoreCase = true) ||
                    sale.totalBill.toString().contains(query)

            val matchesYear = year == "All" || saleYear == year
            val matchesMonth = month == "All" || saleMonth == month
            val matchesDay = day == "All" || saleDay == day
            val matchesCategory = category == "All" || sale.category == category

            matchesSearch && matchesYear && matchesMonth && matchesDay && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun getOffsetDateStr(offset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offset)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    private fun getMonthName(prefix: String): String {
        try {
            val date = SimpleDateFormat("yyyy-MM", Locale.US).parse(prefix)
            if (date != null) {
                return SimpleDateFormat("MMMM yyyy", Locale.US).format(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return prefix
    }

    // Reactive comparison report
    val comparisonData: StateFlow<ComparisonReport?> = allSales.map { sales ->
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayCal = Calendar.getInstance()
            val todayStr = sdf.format(todayCal.time)
            
            // Yesterday
            todayCal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(todayCal.time)
            
            // This week: last 7 days (including today) -> offset 0 to -6
            val sevenDaysAgoStr = getOffsetDateStr(-6)
            
            // Last week: preceding 7 days -> offset -7 to -13
            val eightDaysAgoStr = getOffsetDateStr(-7)
            val fourteenDaysAgoStr = getOffsetDateStr(-13)
            
            // This Month and Last Month
            val thisMonthCal = Calendar.getInstance()
            val thisMonthPrefix = String.format(Locale.US, "%04d-%02d", thisMonthCal.get(Calendar.YEAR), thisMonthCal.get(Calendar.MONTH) + 1)
            
            thisMonthCal.add(Calendar.MONTH, -1)
            val lastMonthPrefix = String.format(Locale.US, "%04d-%02d", thisMonthCal.get(Calendar.YEAR), thisMonthCal.get(Calendar.MONTH) + 1)
            
            // Filter lists
            val todayEntries = sales.filter { it.date == todayStr }
            val yesterdayEntries = sales.filter { it.date == yesterdayStr }
            
            val thisWeekEntries = sales.filter { it.date in sevenDaysAgoStr..todayStr }
            val lastWeekEntries = sales.filter { it.date in fourteenDaysAgoStr..eightDaysAgoStr }
            
            val thisMonthEntries = sales.filter { it.date.startsWith(thisMonthPrefix) }
            val lastMonthEntries = sales.filter { it.date.startsWith(lastMonthPrefix) }
            
            // Helper to compute stats
            fun getStats(list: List<SaleEntry>) = Triple(
                list.sumOf { it.totalBill },
                list.sumOf { it.estimatedProfit },
                list.sumOf { it.due }
            )
            
            val (tSales, tProfit, tDues) = getStats(todayEntries)
            val (ySales, yProfit, yDues) = getStats(yesterdayEntries)
            
            val (twSales, twProfit, twDues) = getStats(thisWeekEntries)
            val (lwSales, lwProfit, lwDues) = getStats(lastWeekEntries)
            
            val (tmSales, tmProfit, tmDues) = getStats(thisMonthEntries)
            val (lmSales, lmProfit, lmDues) = getStats(lastMonthEntries)
            
            ComparisonReport(
                todayVsYesterday = ComparisonSection(
                    periodName = "Today vs Yesterday",
                    currentPeriodLabel = "Today ($todayStr)",
                    previousPeriodLabel = "Yesterday ($yesterdayStr)",
                    sales = ComparisonMetric("Total Sales", tSales, ySales),
                    profit = ComparisonMetric("Estimated Profit", tProfit, yProfit),
                    dues = ComparisonMetric("Total Dues", tDues, yDues)
                ),
                thisWeekVsLastWeek = ComparisonSection(
                    periodName = "This Week vs Last Week",
                    currentPeriodLabel = "This Week ($sevenDaysAgoStr to $todayStr)",
                    previousPeriodLabel = "Last Week ($fourteenDaysAgoStr to $eightDaysAgoStr)",
                    sales = ComparisonMetric("Total Sales", twSales, lwSales),
                    profit = ComparisonMetric("Estimated Profit", twProfit, lwProfit),
                    dues = ComparisonMetric("Total Dues", twDues, lwDues)
                ),
                thisMonthVsLastMonth = ComparisonSection(
                    periodName = "This Month vs Last Month",
                    currentPeriodLabel = "This Month (${getMonthName(thisMonthPrefix)})",
                    previousPeriodLabel = "Last Month (${getMonthName(lastMonthPrefix)})",
                    sales = ComparisonMetric("Total Sales", tmSales, lmSales),
                    profit = ComparisonMetric("Estimated Profit", tmProfit, lmProfit),
                    dues = ComparisonMetric("Total Dues", tmDues, lmDues)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Customer Dues data class
    data class CustomerDue(
        val customerName: String,
        val totalDue: Double,
        val lastUpdated: String
    )

    // Flow of customer dues computed dynamically from all sales entries
    val customerDues: StateFlow<List<CustomerDue>> = allSales
        .map { sales ->
            sales
                .filter { it.customerName.isNotBlank() }
                .groupBy { it.customerName.trim().lowercase(Locale.US) }
                .map { (lowercasedName, entries) ->
                    val originalName = entries.first().customerName.trim()
                    val totalDue = entries.sumOf { it.due }
                    val sortedByTimestamp = entries.sortedByDescending { it.timestamp }
                    val lastDate = sortedByTimestamp.firstOrNull()?.date ?: ""
                    CustomerDue(
                        customerName = originalName,
                        totalDue = totalDue,
                        lastUpdated = lastDate
                    )
                }
                .sortedWith(
                    compareByDescending<CustomerDue> { it.totalDue > 0 }
                        .thenByDescending { it.totalDue }
                        .thenBy { it.customerName }
                )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadMonthlyEntries()
    }

    fun changeSelectedDate(newDate: String) {
        _selectedDateFilter.value = newDate
    }

    fun changeExportMonthAndYear(month: Int, year: Int) {
        _selectedExportMonth.value = month
        _selectedExportYear.value = year
        loadMonthlyEntries()
    }

    fun loadMonthlyEntries() {
        viewModelScope.launch {
            val monthStr = String.format("%02d", _selectedExportMonth.value)
            val pattern = "${_selectedExportYear.value}-$monthStr-%"
            val list = repository.getEntriesByMonth(pattern)
            _monthlyEntries.value = list
        }
    }

    // Insert Entry
    fun addSale(
        date: String,
        time: String,
        category: String,
        itemName: String,
        quantity: Double,
        totalBill: Double,
        cashPaid: Double,
        costPrice: Double,
        customerName: String = ""
    ) {
        viewModelScope.launch {
            // Automated Calculations
            val due = totalBill - cashPaid
            val estimatedProfit = totalBill - costPrice

            // Parse date for timestamp sorting
            var timestamp = System.currentTimeMillis()
            try {
                val parsedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse("$date $time")
                if (parsedDate != null) {
                    timestamp = parsedDate.time
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val entry = SaleEntry(
                date = date,
                time = time,
                category = category,
                itemName = itemName,
                quantity = quantity,
                totalBill = totalBill,
                cashPaid = cashPaid,
                costPrice = costPrice,
                due = due,
                estimatedProfit = estimatedProfit,
                timestamp = timestamp,
                customerName = customerName
            )
            repository.insert(entry)
            // Refresh monthly view if we added something in that month
            loadMonthlyEntries()
        }
    }

    // Add explicit Due Payment
    fun addDuePayment(
        date: String,
        time: String,
        customerName: String,
        amountPaid: Double
    ) {
        viewModelScope.launch {
            var timestamp = System.currentTimeMillis()
            try {
                val parsedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse("$date $time")
                if (parsedDate != null) {
                    timestamp = parsedDate.time
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val entry = SaleEntry(
                date = date,
                time = time,
                category = "Due Payment",
                itemName = "Due Payment - Received",
                quantity = 1.0,
                totalBill = 0.0,
                cashPaid = amountPaid,
                costPrice = 0.0,
                due = -amountPaid, // Negative due offsets the positive dues
                estimatedProfit = 0.0,
                timestamp = timestamp,
                customerName = customerName
            )
            repository.insert(entry)
            loadMonthlyEntries()
        }
    }

    // Update Entry
    fun updateSale(
        id: Int,
        date: String,
        time: String,
        category: String,
        itemName: String,
        quantity: Double,
        totalBill: Double,
        cashPaid: Double,
        costPrice: Double,
        customerName: String = ""
    ) {
        viewModelScope.launch {
            val due = totalBill - cashPaid
            val estimatedProfit = totalBill - costPrice

            var timestamp = System.currentTimeMillis()
            try {
                val parsedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse("$date $time")
                if (parsedDate != null) {
                    timestamp = parsedDate.time
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val entry = SaleEntry(
                id = id,
                date = date,
                time = time,
                category = category,
                itemName = itemName,
                quantity = quantity,
                totalBill = totalBill,
                cashPaid = cashPaid,
                costPrice = costPrice,
                due = due,
                estimatedProfit = estimatedProfit,
                timestamp = timestamp,
                customerName = customerName
            )
            repository.update(entry)
            loadMonthlyEntries()
        }
    }

    // Delete Entry
    fun deleteSale(entry: SaleEntry) {
        viewModelScope.launch {
            repository.delete(entry)
            loadMonthlyEntries()
        }
    }

    // Export Excel Flow
    fun exportExcel(context: Context) {
        viewModelScope.launch {
            _exportState.value = ExportStatus.Loading
            
            val year = _selectedExportYear.value
            val month = _selectedExportMonth.value
            
            // Get latest data
            val monthStr = String.format("%02d", month)
            val pattern = "$year-$monthStr-%"
            val list = repository.getEntriesByMonth(pattern)

            val file = ExcelExporter.exportMonthlyLedger(context, year, month, list)
            if (file != null && file.exists()) {
                _exportState.value = ExportStatus.Success(file)
                shareExcelFile(context, file)
            } else {
                _exportState.value = ExportStatus.Error("Failed to generate Excel file")
                Toast.makeText(context, "Export Failed. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun shareExcelFile(context: Context, file: File) {
        try {
            val authority = "com.aistudio.retailledger.xtrkpw.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Monthly Ledger - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Share Ledger File via:")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    sealed interface ExportStatus {
        object Idle : ExportStatus
        object Loading : ExportStatus
        data class Success(val file: File) : ExportStatus
        data class Error(val message: String) : ExportStatus
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: SaleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class ComparisonMetric(
    val title: String,
    val currentVal: Double,
    val previousVal: Double
) {
    val difference: Double = currentVal - previousVal
    val percentChange: Double? = if (previousVal > 0.0) {
        (difference / previousVal) * 100.0
    } else {
        null
    }
}

data class ComparisonSection(
    val periodName: String,
    val currentPeriodLabel: String,
    val previousPeriodLabel: String,
    val sales: ComparisonMetric,
    val profit: ComparisonMetric,
    val dues: ComparisonMetric
)

data class ComparisonReport(
    val todayVsYesterday: ComparisonSection,
    val thisWeekVsLastWeek: ComparisonSection,
    val thisMonthVsLastMonth: ComparisonSection
)
