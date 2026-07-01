package com.example.utils

import android.content.Context
import android.os.Environment
import com.example.data.database.SaleEntry
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ExcelExporter {

    private val CATEGORIES = listOf(
        "Grocery",
        "Rickshaw Parts",
        "Gas",
        "Hardware",
        "Mobile Card",
        "Bkash Bill",
        "Electrical Item",
        "Stationary",
        "Mobile Recharge"
    )

    fun exportMonthlyLedger(
        context: Context,
        year: Int,
        month: Int, // 1-indexed: 1 = January, 12 = December
        entries: List<SaleEntry>
    ): File? {
        try {
            // Group entries by date for easy lookup.
            // Format of date in SaleEntry is "YYYY-MM-DD"
            val entriesByDate = entries.groupBy { it.date }

            val workbook = XSSFWorkbook()

            // Define styles
            val headerFont = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 11.toShort()
            }

            val summaryHeaderStyle = workbook.createCellStyle().apply {
                setFont(headerFont)
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                alignment = HorizontalAlignment.CENTER
            }

            val rawHeaderStyle = workbook.createCellStyle().apply {
                setFont(headerFont)
                fillForegroundColor = IndexedColors.LIGHT_BLUE.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                alignment = HorizontalAlignment.CENTER
            }

            val boldStyle = workbook.createCellStyle().apply {
                val font = workbook.createFont().apply { bold = true }
                setFont(font)
            }

            val rightAlignStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.RIGHT
            }

            // Loop through all days of the selected month
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1) // 0-indexed in Calendar
            val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            for (day in 1..maxDays) {
                // Generate strict DDMMYY sheet name
                // Example: June 1st 2026 -> "010626"
                val sheetName = String.format("%02d%02d%02d", day, month, year % 100)
                val sheet = workbook.createSheet(sheetName)

                // Match with YYYY-MM-DD key
                val dbDateKey = String.format("%04d-%02d-%02d", year, month, day)
                val dayEntries = entriesByDate[dbDateKey] ?: emptyList()

                // Row 0: Summary table header
                val row0 = sheet.createRow(0)
                row0.createCell(0).apply {
                    setCellValue("Category")
                    setCellStyle(summaryHeaderStyle)
                }
                row0.createCell(1).apply {
                    setCellValue("Total Sales (৳)")
                    setCellStyle(summaryHeaderStyle)
                }
                row0.createCell(2).apply {
                    setCellValue("Total Dues (৳)")
                    setCellStyle(summaryHeaderStyle)
                }
                row0.createCell(3).apply {
                    setCellValue("Total Profit (৳)")
                    setCellStyle(summaryHeaderStyle)
                }

                // Row 1 to 9: Category summary data
                CATEGORIES.forEachIndexed { index, category ->
                    val row = sheet.createRow(index + 1)
                    val catEntries = dayEntries.filter { it.category.equals(category, ignoreCase = true) }
                    
                    val totalSales = catEntries.sumOf { it.totalBill }
                    val totalDues = catEntries.sumOf { it.due }
                    val totalProfit = catEntries.sumOf { it.estimatedProfit }

                    row.createCell(0).apply {
                        setCellValue(category)
                        setCellStyle(boldStyle)
                    }
                    row.createCell(1).apply {
                        setCellValue(totalSales)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(2).apply {
                        setCellValue(totalDues)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(3).apply {
                        setCellValue(totalProfit)
                        setCellStyle(rightAlignStyle)
                    }
                }

                // Row 10: Blank spacing row
                sheet.createRow(10)

                // Row 11: Column headers for raw data
                val row11 = sheet.createRow(11)
                val columns = listOf(
                    "Time", 
                    "Category", 
                    "Item & Customer Name", 
                    "Quantity", 
                    "Total Bill (৳)", 
                    "Cash Paid (৳)", 
                    "Due (৳)", 
                    "Cost Price (৳)", 
                    "Estimated Profit (৳)"
                )
                
                columns.forEachIndexed { index, colName ->
                    row11.createCell(index).apply {
                        setCellValue(colName)
                        setCellStyle(rawHeaderStyle)
                    }
                }

                // Row 12 onwards: Raw data
                dayEntries.forEachIndexed { index, entry ->
                    val row = sheet.createRow(12 + index)
                    row.createCell(0).setCellValue(entry.time)
                    row.createCell(1).setCellValue(entry.category)
                    row.createCell(2).setCellValue(entry.itemName)
                    row.createCell(3).apply {
                        setCellValue(entry.quantity)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(4).apply {
                        setCellValue(entry.totalBill)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(5).apply {
                        setCellValue(entry.cashPaid)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(6).apply {
                        setCellValue(entry.due)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(7).apply {
                        setCellValue(entry.costPrice)
                        setCellStyle(rightAlignStyle)
                    }
                    row.createCell(8).apply {
                        setCellValue(entry.estimatedProfit)
                        setCellStyle(rightAlignStyle)
                    }
                }

                // Auto-fit columns for this sheet
                for (colIndex in 0..8) {
                    sheet.autoSizeColumn(colIndex)
                }
            }

            // Generate output file
            val fileName = String.format(Locale.US, "Ledger_%04d_%02d.xlsx", year, month)
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, fileName)
            val fileOut = FileOutputStream(file)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
