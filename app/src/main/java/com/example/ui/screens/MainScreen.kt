package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.database.SaleEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) } // 0: Today, 1: History, 2: Compare, 3: Export

    // Form Dialog States
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<SaleEntry?>(null) }
    
    // Deletion confirmation
    var entryToDelete by remember { mutableStateOf<SaleEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "RETAIL LEDGER",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                            viewModel.changeSelectedDate(dateStr)
                        }
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Sync Today",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Elegant, minimalist NavigationBar matching the dark theme
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            if (currentTab == 0) Icons.Filled.Today else Icons.Outlined.Today,
                            contentDescription = "Daily Ledger",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            "Today",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(
                            if (currentTab == 1) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "Search History",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            "History",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(
                            if (currentTab == 2) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Dues Tracker",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            "Dues",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = {
                        Icon(
                            if (currentTab == 3) Icons.Filled.TrendingUp else Icons.Outlined.TrendingUp,
                            contentDescription = "Compare Periods",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            "Compare",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { currentTab = 4 },
                    icon = {
                        Icon(
                            if (currentTab == 4) Icons.Filled.Share else Icons.Outlined.Share,
                            contentDescription = "Export Excel",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            "Export",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
            }
        },
        floatingActionButton = {
            if (currentTab == 0 || currentTab == 1) {
                FloatingActionButton(
                    onClick = {
                        editingEntry = null
                        showAddDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("add_sale_fab"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add New Sale",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                0 -> {
                    DailyLedgerTab(
                        viewModel = viewModel,
                        onEditEntry = { entry ->
                            editingEntry = entry
                            showAddDialog = true
                        },
                        onDeleteEntry = { entry ->
                            entryToDelete = entry
                        }
                    )
                }
                1 -> {
                    HistoryTab(
                        viewModel = viewModel,
                        onEditEntry = { entry ->
                            editingEntry = entry
                            showAddDialog = true
                        },
                        onDeleteEntry = { entry ->
                            entryToDelete = entry
                        }
                    )
                }
                2 -> {
                    DuesLedgerTab(
                        viewModel = viewModel
                    )
                }
                3 -> {
                    CompareTab(
                        viewModel = viewModel
                    )
                }
                4 -> {
                    ExportTab(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Add or Edit Dialog
    if (showAddDialog) {
        AddEditSaleDialog(
            entry = editingEntry,
            onDismiss = { showAddDialog = false },
            onSave = { date, time, category, item, qty, bill, paid, cost, customer ->
                if (editingEntry == null) {
                    viewModel.addSale(date, time, category, item, qty, bill, paid, cost, customer)
                } else {
                    viewModel.updateSale(editingEntry!!.id, date, time, category, item, qty, bill, paid, cost, customer)
                }
                showAddDialog = false
            }
        )
    }

    // Deletion confirmation
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = {
                Text(
                    "Delete Entry?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this sale entry of ৳${entryToDelete?.totalBill}?",
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        entryToDelete?.let { viewModel.deleteSale(it) }
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Delete", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { entryToDelete = null }
                ) {
                    Text("Cancel", fontSize = 16.sp)
                }
            }
        )
    }
}

@Composable
fun DailyLedgerTab(
    viewModel: MainViewModel,
    onEditEntry: (SaleEntry) -> Unit,
    onDeleteEntry: (SaleEntry) -> Unit
) {
    val context = LocalContext.current
    val selectedDate by viewModel.selectedDateFilter.collectAsState()
    val sales by viewModel.salesForSelectedDate.collectAsState()

    // Aggregate totals for today
    val totalSalesSum = sales.sumOf { it.totalBill }
    val totalProfitSum = sales.sumOf { it.estimatedProfit }
    val totalDuesSum = sales.sumOf { it.due }

    // Date formatter for display, e.g. "Tuesday, June 30, 2026"
    val displayDate = remember(selectedDate) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDate)
            if (date != null) {
                SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US).format(date)
            } else {
                selectedDate
            }
        } catch (e: Exception) {
            selectedDate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date Selector Row - Big, Easy to tap
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable {
                    val calendar = Calendar.getInstance()
                    try {
                        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDate)
                        if (parsed != null) calendar.time = parsed
                    } catch (e: Exception) {
                    }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                            viewModel.changeSelectedDate(newDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "LEDGER DATE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        displayDate,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Summary Statistics Cards - Highly Readable, Bold
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sales Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TOTAL SALES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "৳${String.format(Locale.US, "%,.0f", totalSalesSum)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Profit Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TOTAL PROFIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "৳${String.format(Locale.US, "%,.0f", totalProfitSum)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Dues Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.error)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TOTAL DUES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "৳${String.format(Locale.US, "%,.0f", totalDuesSum)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Section Title: Today's Entries
        Text(
            "SALES RECORDS (${sales.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Today's Entries List
        if (sales.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No sales records for today.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tap the ➕ button below to add sales.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sales, key = { it.id }) { entry ->
                    SaleEntryRow(
                        entry = entry,
                        onEdit = { onEditEntry(entry) },
                        onDelete = { onDeleteEntry(entry) }
                    )
                }
            }
        }
    }
}

@Composable
fun SaleEntryRow(
    entry: SaleEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedMenu = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (entry.due > 0) MaterialTheme.colorScheme.error.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Row 1: Time, Category, and Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Time Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            entry.time,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Category Name
                    Text(
                        entry.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                // Click indicators or simple edit/delete icons directly for high-contrast senior visibility
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Customer/Item and Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.itemName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Qty: ${if (entry.quantity % 1 == 0.0) entry.quantity.toInt().toString() else entry.quantity}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Row 3: Total Bill, Paid, and Profit Calculations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TOTAL BILL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("৳${entry.totalBill.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text("PAID CASH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("৳${entry.cashPaid.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (entry.due > 0) {
                        Text("DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text("৳${entry.due.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("৳0", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("EST. PROFIT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Text("৳${entry.estimatedProfit.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
fun ExportTab(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val currentYear by viewModel.selectedExportYear.collectAsState()
    val currentMonth by viewModel.selectedExportMonth.collectAsState()
    val monthlySales by viewModel.monthlyEntries.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    // Aggregate monthly statistics
    val mTotalSales = monthlySales.sumOf { it.totalBill }
    val mTotalDues = monthlySales.sumOf { it.due }
    val mTotalProfit = monthlySales.sumOf { it.estimatedProfit }

    val monthName = remember(currentMonth) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth - 1)
        SimpleDateFormat("MMMM", Locale.US).format(calendar.time)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Year & Month Selection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "SELECT LEDGER MONTH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Month Button
                        IconButton(
                            onClick = {
                                if (currentMonth == 1) {
                                    viewModel.changeExportMonthAndYear(12, currentYear - 1)
                                } else {
                                    viewModel.changeExportMonthAndYear(currentMonth - 1, currentYear)
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Prev Month", modifier = Modifier.size(36.dp))
                        }

                        // Month & Year Large Display
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                monthName.uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                currentYear.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }

                        // Next Month Button
                        IconButton(
                            onClick = {
                                if (currentMonth == 12) {
                                    viewModel.changeExportMonthAndYear(1, currentYear + 1)
                                } else {
                                    viewModel.changeExportMonthAndYear(currentMonth + 1, currentYear)
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Next Month", modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }

        // Monthly Totals Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "MONTHLY SUMMARY FOR $monthName $currentYear".uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Transactions Saved:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${monthlySales.size} Sales", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Sales:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("৳${String.format(Locale.US, "%,.2f", mTotalSales)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Profit:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("৳${String.format(Locale.US, "%,.2f", mTotalProfit)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Unpaid Dues:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("৳${String.format(Locale.US, "%,.2f", mTotalDues)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Large high-contrast EXPORT Button
        item {
            Button(
                onClick = { viewModel.exportExcel(context) },
                enabled = exportState != MainViewModel.ExportStatus.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("export_excel_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (exportState == MainViewModel.ExportStatus.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "EXPORT MONTHLY EXCEL FILE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Informative guidance box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Format Notice",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            "LEDGER EXPORT RULES:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Each calendar day of the month gets its own separate worksheet (named DDMMYY, e.g., '010626').\n" +
                            "• Rows 0-9 contain the category profit, sales, and dues headers.\n" +
                            "• Raw entries list starting from row 12 onwards.\n" +
                            "• Shares instantly via WhatsApp, Email, or Google Drive.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSaleDialog(
    entry: SaleEntry?,
    onDismiss: () -> Unit,
    onSave: (
        date: String,
        time: String,
        category: String,
        itemName: String,
        quantity: Double,
        totalBill: Double,
        cashPaid: Double,
        costPrice: Double,
        customerName: String
    ) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Initial values
    val isEdit = entry != null
    var dateVal by remember { mutableStateOf(entry?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var timeVal by remember { mutableStateOf(entry?.time ?: SimpleDateFormat("HH:mm", Locale.US).format(Date())) }
    var categoryVal by remember { mutableStateOf(entry?.category ?: "Grocery") }
    var itemNameVal by remember { mutableStateOf(entry?.itemName ?: "") }
    var customerNameVal by remember { mutableStateOf(entry?.customerName ?: "") }
    var quantityVal by remember { mutableStateOf(entry?.quantity?.toString() ?: "") }
    var totalBillVal by remember { mutableStateOf(entry?.totalBill?.toInt()?.toString() ?: "") }
    var cashPaidVal by remember { mutableStateOf(entry?.cashPaid?.toInt()?.toString() ?: "") }
    var costPriceVal by remember { mutableStateOf(entry?.costPrice?.toInt()?.toString() ?: "") }

    // Dropdown expanding state
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Grocery", "Rickshaw Parts", "Gas", "Hardware", "Mobile Card", 
        "Bkash Bill", "Electrical Item", "Stationary", "Mobile Recharge"
    )

    // Form errors
    var itemError by remember { mutableStateOf(false) }
    var customerNameError by remember { mutableStateOf(false) }
    var qtyError by remember { mutableStateOf(false) }
    var billError by remember { mutableStateOf(false) }
    var paidError by remember { mutableStateOf(false) }
    var costError by remember { mutableStateOf(false) }

    // Automated Calculations (live feedback)
    val doubleBill = totalBillVal.toDoubleOrNull() ?: 0.0
    val doublePaid = cashPaidVal.toDoubleOrNull() ?: 0.0
    val doubleCost = costPriceVal.toDoubleOrNull() ?: 0.0

    val liveDue = doubleBill - doublePaid
    val liveProfit = doubleBill - doubleCost

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // Allows full screen style sizing for accessibility
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Top Header Bar
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEdit) "EDIT TRANSACTION" else "NEW TRANSACTION",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Dialog Form Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date & Time pickers
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Date field
                            Card(
                                onClick = {
                                    val parts = dateVal.split("-")
                                    if (parts.size == 3) {
                                        calendar.set(Calendar.YEAR, parts[0].toInt())
                                        calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                                        calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
                                    }
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            dateVal = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("DATE (TAP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(dateVal, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            // Time field
                            Card(
                                onClick = {
                                    val parts = timeVal.split(":")
                                    if (parts.size == 2) {
                                        calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                                        calendar.set(Calendar.MINUTE, parts[1].toInt())
                                    }
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            timeVal = String.format(Locale.US, "%02d:%02d", h, m)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("TIME (TAP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(timeVal, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Category Dropdown - Senior accessible (Click launches a huge picker)
                    item {
                        ExposedDropdownMenuBox(
                            expanded = categoryMenuExpanded,
                            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = categoryVal,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("CATEGORY", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, fontSize = 18.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryMenuExpanded,
                                onDismissRequest = { categoryMenuExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            categoryVal = cat
                                            categoryMenuExpanded = false
                                        },
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Item & Customer Name
                    item {
                        OutlinedTextField(
                            value = itemNameVal,
                            onValueChange = {
                                itemNameVal = it
                                itemError = it.trim().isEmpty()
                            },
                            label = { Text("ITEM & CUSTOMER NAME", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("e.g. Rickshaw Rim (Rahim)") },
                            isError = itemError,
                            modifier = Modifier.fillMaxWidth().testTag("item_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (itemError) {
                            Text("Item & Customer Name is required", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    // Customer Name (Optional, but required if due > 0)
                    item {
                        OutlinedTextField(
                            value = customerNameVal,
                            onValueChange = {
                                customerNameVal = it
                                customerNameError = liveDue > 0 && it.trim().isEmpty()
                            },
                            label = { Text("CUSTOMER NAME FOR DUES", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("e.g. Rahim, Karim, Sumon") },
                            isError = customerNameError,
                            modifier = Modifier.fillMaxWidth().testTag("customer_name_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (customerNameError) {
                            Text("Customer Name is required when there are unpaid dues!", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Provide customer name to track cumulative dues and credit history.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                    }

                    // Quantity
                    item {
                        OutlinedTextField(
                            value = quantityVal,
                            onValueChange = {
                                quantityVal = it
                                qtyError = it.toDoubleOrNull() == null || it.toDouble() <= 0
                            },
                            label = { Text("QUANTITY", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("e.g. 1, 2.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = qtyError,
                            modifier = Modifier.fillMaxWidth().testTag("quantity_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (qtyError) {
                            Text("Please enter a valid numeric quantity greater than 0", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    // Total Bill
                    item {
                        OutlinedTextField(
                            value = totalBillVal,
                            onValueChange = {
                                totalBillVal = it
                                billError = it.toDoubleOrNull() == null || it.toDouble() < 0
                            },
                            label = { Text("TOTAL BILL (৳)", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("Total Bill Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = billError,
                            modifier = Modifier.fillMaxWidth().testTag("bill_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (billError) {
                            Text("Please enter a valid bill amount (0 or more)", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    // Cash Paid
                    item {
                        OutlinedTextField(
                            value = cashPaidVal,
                            onValueChange = {
                                cashPaidVal = it
                                paidError = it.toDoubleOrNull() == null || it.toDouble() < 0
                            },
                            label = { Text("CASH PAID (৳)", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("Amount Paid by Customer") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = paidError,
                            modifier = Modifier.fillMaxWidth().testTag("paid_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (paidError) {
                            Text("Please enter a valid cash paid amount (0 or more)", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    // Cost Price
                    item {
                        OutlinedTextField(
                            value = costPriceVal,
                            onValueChange = {
                                costPriceVal = it
                                costError = it.toDoubleOrNull() == null || it.toDouble() < 0
                            },
                            label = { Text("COST PRICE (৳)", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("Our Purchase/Cost Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = costError,
                            modifier = Modifier.fillMaxWidth().testTag("cost_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (costError) {
                            Text("Please enter a valid cost price (0 or more)", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    // Real-time Automated Calculations Box
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(
                                    "AUTOMATED CALCULATIONS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Unpaid Due:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "৳${String.format(Locale.US, "%,.1f", liveDue)}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (liveDue > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Estimated Profit:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "৳${String.format(Locale.US, "%,.1f", liveProfit)}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Footer Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("CANCEL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            // Validation checks
                            val itemEmpty = itemNameVal.trim().isEmpty()
                            val qtyInvalid = quantityVal.toDoubleOrNull() == null || quantityVal.toDouble() <= 0
                            val billInvalid = totalBillVal.toDoubleOrNull() == null || totalBillVal.toDouble() < 0
                            val paidInvalid = cashPaidVal.toDoubleOrNull() == null || cashPaidVal.toDouble() < 0
                            val costInvalid = costPriceVal.toDoubleOrNull() == null || costPriceVal.toDouble() < 0
                            
                            val hasDue = liveDue > 0
                            val customerNameEmpty = customerNameVal.trim().isEmpty()
                            val customerNameErr = hasDue && customerNameEmpty

                            itemError = itemEmpty
                            customerNameError = customerNameErr
                            qtyError = qtyInvalid
                            billError = billInvalid
                            paidError = paidInvalid
                            costError = costInvalid

                            if (!itemEmpty && !customerNameErr && !qtyInvalid && !billInvalid && !paidInvalid && !costInvalid) {
                                onSave(
                                    dateVal,
                                    timeVal,
                                    categoryVal,
                                    itemNameVal.trim(),
                                    quantityVal.toDouble(),
                                    totalBillVal.toDouble(),
                                    cashPaidVal.toDouble(),
                                    costPriceVal.toDouble(),
                                    customerNameVal.trim()
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp)
                            .testTag("save_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Check, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE RECORD", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompareTab(viewModel: MainViewModel) {
    val report by viewModel.comparisonData.collectAsState()

    if (report == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Analyzing transaction data...", fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Introduction Banner Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = "Analytics Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "PERFORMANCE COMPARISON",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Compare today, this week, and this month with previous periods to track your business growth.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Section 1: Daily Comparison
        item {
            ComparisonCard(section = report!!.todayVsYesterday)
        }

        // Section 2: Weekly Comparison
        item {
            ComparisonCard(section = report!!.thisWeekVsLastWeek)
        }

        // Section 3: Monthly Comparison
        item {
            ComparisonCard(section = report!!.thisMonthVsLastMonth)
        }
    }
}

@Composable
fun ComparisonCard(section: ComparisonSection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Title
            Text(
                section.periodName.uppercase(),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Current Period vs Previous Period subtitling
            Text(
                "Current: ${section.currentPeriodLabel}",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Previous: ${section.previousPeriodLabel}",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comparison Metrics
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                MetricRow(metric = section.sales, isDues = false)
                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                MetricRow(metric = section.profit, isDues = false)
                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                MetricRow(metric = section.dues, isDues = true)
            }
        }
    }
}

@Composable
fun MetricRow(metric: ComparisonMetric, isDues: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Metric Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                metric.title.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "৳${String.format(Locale.US, "%,.0f", metric.currentVal)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "vs",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "৳${String.format(Locale.US, "%,.0f", metric.previousVal)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        // Difference Badge
        val isPositive = metric.difference > 0
        val isNegative = metric.difference < 0
        
        // Dues are bad if positive (increased unpaid bills), good if negative (decreased unpaid bills)
        val isFavorable = if (isDues) {
            !isPositive
        } else {
            isPositive
        }
        
        val badgeColor = when {
            metric.difference == 0.0 -> Color.Gray.copy(alpha = 0.1f)
            isFavorable -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f) // Green theme
            else -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f) // Red theme
        }
        
        val contentColor = when {
            metric.difference == 0.0 -> Color.DarkGray
            isFavorable -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(badgeColor)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPositive) {
                    Icon(
                        Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Upward Trend",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (isNegative) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Downward Trend",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                val diffPrefix = if (isPositive) "+" else ""
                val percentText = if (metric.percentChange != null) {
                    String.format(Locale.US, " (%.1f%%)", metric.percentChange)
                } else {
                    ""
                }
                
                Text(
                    text = "$diffPrefix${String.format(Locale.US, "%,.0f", metric.difference)}$percentText",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun HistoryTab(
    viewModel: MainViewModel,
    onEditEntry: (SaleEntry) -> Unit,
    onDeleteEntry: (SaleEntry) -> Unit
) {
    val query by viewModel.historySearchQuery.collectAsState()
    val selectedYear by viewModel.historySelectedYear.collectAsState()
    val selectedMonth by viewModel.historySelectedMonth.collectAsState()
    val selectedDay by viewModel.historySelectedDay.collectAsState()
    val selectedCategory by viewModel.historySelectedCategory.collectAsState()

    val availableYears by viewModel.availableYears.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val filteredSales by viewModel.filteredHistorySales.collectAsState()

    val totalFilteredSalesSum = filteredSales.sumOf { it.totalBill }
    val totalFilteredProfitSum = filteredSales.sumOf { it.estimatedProfit }
    val totalFilteredDuesSum = filteredSales.sumOf { it.due }

    val monthsList = listOf(
        "All" to "All",
        "01" to "Jan", "02" to "Feb", "03" to "Mar", "04" to "Apr",
        "05" to "May", "06" to "Jun", "07" to "Jul", "08" to "Aug",
        "09" to "Sep", "10" to "Oct", "11" to "Nov", "12" to "Dec"
    )

    val daysList = listOf("All") + (1..31).map { String.format(Locale.US, "%02d", it) }

    var yearExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High-end Search input field
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.setHistorySearchQuery(it) },
            placeholder = { Text("Search item, category, bill...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("history_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Horizontal Compact Filter Grid row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Year filter dropdown button
            Box(modifier = Modifier.weight(1f)) {
                FilterDropdownButton(
                    label = "Year",
                    value = selectedYear,
                    onClick = { yearExpanded = true }
                )
                DropdownMenu(
                    expanded = yearExpanded,
                    onDismissRequest = { yearExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    availableYears.forEach { yr ->
                        DropdownMenuItem(
                            text = { Text(yr) },
                            onClick = {
                                viewModel.setHistorySelectedYear(yr)
                                yearExpanded = false
                            }
                        )
                    }
                }
            }

            // Month Filter dropdown button
            Box(modifier = Modifier.weight(1.1f)) {
                val monthLabel = monthsList.find { it.first == selectedMonth }?.second ?: selectedMonth
                FilterDropdownButton(
                    label = "Month",
                    value = monthLabel,
                    onClick = { monthExpanded = true }
                )
                DropdownMenu(
                    expanded = monthExpanded,
                    onDismissRequest = { monthExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    monthsList.forEach { (mVal, mLabel) ->
                        DropdownMenuItem(
                            text = { Text(mLabel) },
                            onClick = {
                                viewModel.setHistorySelectedMonth(mVal)
                                monthExpanded = false
                            }
                        )
                    }
                }
            }

            // Day Filter dropdown button
            Box(modifier = Modifier.weight(1f)) {
                FilterDropdownButton(
                    label = "Day",
                    value = selectedDay,
                    onClick = { dayExpanded = true }
                )
                DropdownMenu(
                    expanded = dayExpanded,
                    onDismissRequest = { dayExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    daysList.forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d) },
                            onClick = {
                                viewModel.setHistorySelectedDay(d)
                                dayExpanded = false
                            }
                        )
                    }
                }
            }

            // Category Filter dropdown button
            Box(modifier = Modifier.weight(1.3f)) {
                FilterDropdownButton(
                    label = "Category",
                    value = selectedCategory,
                    onClick = { categoryExpanded = true }
                )
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    availableCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.setHistorySelectedCategory(cat)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Live aggregated statistics for searched / filtered selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTAL SALES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("৳${String.format(Locale.US, "%,.0f", totalFilteredSalesSum)}", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EST. PROFIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Text("৳${String.format(Locale.US, "%,.0f", totalFilteredProfitSum)}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTAL DUES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Text("৳${String.format(Locale.US, "%,.0f", totalFilteredDuesSum)}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ITEMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text("${filteredSales.size}", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Historical Ledger list
        if (filteredSales.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "No Results",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No matching records found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Try adjusting your filters or search keywords.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSales, key = { "hist_${it.id}" }) { entry ->
                    HistoricalSaleRow(
                        entry = entry,
                        onEdit = { onEditEntry(entry) },
                        onDelete = { onDeleteEntry(entry) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdownButton(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun HistoricalSaleRow(
    entry: SaleEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    val formattedDate = remember(entry.date) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(entry.date)
            if (date != null) {
                SimpleDateFormat("dd MMM yyyy", Locale.US).format(date)
            } else {
                entry.date
            }
        } catch (e: Exception) {
            entry.date
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedMenu = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = if (entry.due > 0) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Date Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            formattedDate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Time Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            entry.time,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        entry.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Customer/Item and Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.itemName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (entry.customerName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Customer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = entry.customerName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                Text(
                    text = "Qty: ${if (entry.quantity % 1 == 0.0) entry.quantity.toInt().toString() else entry.quantity}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Row 3: Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TOTAL BILL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text("৳${entry.totalBill.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column {
                    Text("PAID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text("৳${entry.cashPaid.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DUE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (entry.due > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text(
                        "৳${entry.due.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (entry.due > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("EST. PROFIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Text("৳${entry.estimatedProfit.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }

        // Dropdown menu actions on click
        DropdownMenu(
            expanded = expandedMenu,
            onDismissRequest = { expandedMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Entry") },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Edit") },
                onClick = {
                    expandedMenu = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete Entry", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) },
                onClick = {
                    expandedMenu = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
fun DuesLedgerTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val customerDues by viewModel.customerDues.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var prefilledCustomerName by remember { mutableStateOf("") }

    val filteredDues = customerDues.filter {
        it.customerName.contains(searchQuery, ignoreCase = true)
    }

    val totalDuesOutstanding = customerDues.filter { it.totalDue > 0 }.sumOf { it.totalDue }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dues Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AccountBalanceWallet,
                    contentDescription = "Wallet",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "DUES LEDGER",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Summary Card for Total Outstanding Debt
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
            ),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "TOTAL OUTSTANDING DEBTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.error,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "৳${String.format(Locale.US, "%,.1f", totalDuesOutstanding)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Huge, accessible "Record Due Payment" Button for senior usability
        Button(
            onClick = {
                prefilledCustomerName = ""
                showPaymentDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .testTag("record_due_payment_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Payment,
                    contentDescription = "Payment Icon",
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "RECORD DUE PAYMENT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search customer name...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dues_search_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            singleLine = true
        )

        // Customer Dues List
        if (filteredDues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.People,
                        contentDescription = "No customers",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (searchQuery.isEmpty()) "No customer dues stored yet." else "No customers found for \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDues) { due ->
                    CustomerDueRow(
                        customerDue = due,
                        onQuickPay = {
                            prefilledCustomerName = due.customerName
                            showPaymentDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showPaymentDialog) {
        AddDuePaymentDialog(
            prefilledName = prefilledCustomerName,
            existingCustomers = customerDues.map { it.customerName },
            onDismiss = { showPaymentDialog = false },
            onSave = { date, time, name, amount ->
                viewModel.addDuePayment(date, time, name, amount)
                showPaymentDialog = false
                Toast.makeText(context, "Dues payment recorded successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun CustomerDueRow(
    customerDue: MainViewModel.CustomerDue,
    onQuickPay: () -> Unit
) {
    val isUnpaid = customerDue.totalDue > 0
    val initials = if (customerDue.customerName.isNotBlank()) {
        customerDue.customerName.trim().take(1).uppercase(Locale.US)
    } else {
        "?"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isUnpaid) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Initial avatar & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Initial Circle Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (isUnpaid) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (isUnpaid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = customerDue.customerName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Last active: ${customerDue.lastUpdated}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Right: Due balance & Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        text = "OUTSTANDING DUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (customerDue.totalDue <= 0.0) "৳00 DUE (PAID)" else "৳${String.format(Locale.US, "%,.1f", customerDue.totalDue)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isUnpaid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                    )
                }

                if (isUnpaid) {
                    // Quick Pay Button
                    IconButton(
                        onClick = onQuickPay,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Quick Pay",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    // Paid Check Icon
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Fully Paid",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDuePaymentDialog(
    prefilledName: String,
    existingCustomers: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        date: String,
        time: String,
        customerName: String,
        amountPaid: Double
    ) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var dateVal by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var timeVal by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.US).format(Date())) }
    var customerNameVal by remember { mutableStateOf(prefilledName) }
    var amountPaidVal by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    // Dropdown/suggestions for existing customers to avoid spelling mistakes
    val uniqueCustomers = existingCustomers.distinct().sorted()
    val filteredSuggestions = uniqueCustomers.filter {
        it.contains(customerNameVal, ignoreCase = true) && it.trim().lowercase() != customerNameVal.trim().lowercase()
    }
    var showSuggestions by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header Bar
                TopAppBar(
                    title = {
                        Text(
                            "RECORD DUE PAYMENT",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Scrollable Form Fields with nice spacing
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date & Time pickers Row (Senior Accessible)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                onClick = {
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            val cal = Calendar.getInstance()
                                            cal.set(y, m, d)
                                            dateVal = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(64.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("DATE (TAP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(dateVal, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            Card(
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            timeVal = String.format(Locale.US, "%02d:%02d", h, m)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("TIME (TAP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(timeVal, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Customer Name (with auto-suggestions)
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                OutlinedTextField(
                                    value = customerNameVal,
                                    onValueChange = {
                                        customerNameVal = it
                                        nameError = it.trim().isEmpty()
                                        showSuggestions = true
                                    },
                                    label = { Text("CUSTOMER NAME", fontWeight = FontWeight.Bold) },
                                    placeholder = { Text("Enter customer name") },
                                    isError = nameError,
                                    modifier = Modifier.fillMaxWidth().testTag("due_pay_customer_input"),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                    singleLine = true
                                )
                                if (nameError) {
                                    Text("Customer Name is required!", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                                }

                                // Interactive Auto-Suggestions chip list
                                if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Suggested Existing Customers (Tap to select):", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        filteredSuggestions.take(3).forEach { suggestion ->
                                            SuggestionChip(
                                                onClick = {
                                                    customerNameVal = suggestion
                                                    showSuggestions = false
                                                },
                                                label = { Text(suggestion, fontWeight = FontWeight.Bold) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Paid Amount Field
                    item {
                        OutlinedTextField(
                            value = amountPaidVal,
                            onValueChange = {
                                amountPaidVal = it
                                amountError = it.toDoubleOrNull() == null || it.toDouble() <= 0
                            },
                            label = { Text("AMOUNT PAID (৳)", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("e.g. 100, 500") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = amountError,
                            modifier = Modifier.fillMaxWidth().testTag("due_pay_amount_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, fontSize = 18.sp),
                            singleLine = true
                        )
                        if (amountError) {
                            Text("Please enter a valid amount greater than 0", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    // Info banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Recording a due payment will automatically subtract this amount from the customer's outstanding balance.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Footer Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("CANCEL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val nameEmpty = customerNameVal.trim().isEmpty()
                            val amountInvalid = amountPaidVal.toDoubleOrNull() == null || amountPaidVal.toDouble() <= 0

                            nameError = nameEmpty
                            amountError = amountInvalid

                            if (!nameEmpty && !amountInvalid) {
                                onSave(
                                    dateVal,
                                    timeVal,
                                    customerNameVal.trim(),
                                    amountPaidVal.toDouble()
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp)
                            .testTag("save_due_payment_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Check, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE PAYMENT", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

