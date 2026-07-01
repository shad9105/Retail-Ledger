package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.AppDatabase
import com.example.data.repository.SaleRepository
import com.example.ui.screens.MainScreen
import com.example.ui.screens.MainViewModel
import com.example.ui.screens.MainViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Database & Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = SaleRepository(database.saleDao())
        
        // Setup ViewModel with Factory
        val factory = MainViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
