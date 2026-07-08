package com.deepseek.bikesphere360.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deepseek.bikesphere360.navigations.*
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Categories, 1: Hot Deals
    var userRole by remember { mutableStateOf("user") }
    val repo = FirebaseRepository()

    LaunchedEffect(Unit) {
        repo.getCurrentUserRole { userRole = it }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, userRole)
        }
    ) { innerPadding ->
        BikeSphereScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
            // Top Tabs
            Row(modifier = Modifier.fillMaxWidth()) {
                TabButton("Categories", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                TabButton("Today's Hot Deals", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
            }

            if (selectedTab == 0) {
                CategoryDashboard(navController, userRole)
            } else {
                HotDealsScreen(navController)
            }
        } }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(60.dp)
            .background(if (isSelected) AppRed else Color.Gray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CategoryDashboard(navController: NavController, userRole: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (userRole == "admin") {
            CategoryCard("Admin Dashboard", AppYellow) { navController.navigate(ROUTE_ADMIN_DASHBOARD) }
            Spacer(modifier = Modifier.height(16.dp))
        }
        CategoryCard("Motorbikes", AppPurple) { navController.navigate("category_select/Motorbike") }
        Spacer(modifier = Modifier.height(16.dp))
        CategoryCard("Bicycles", AppPink) { navController.navigate("category_select/Bicycle") }
        Spacer(modifier = Modifier.height(16.dp))
        CategoryCard("Spareparts", AppGreen) { navController.navigate("category_select/Sparepart") }
    }
}

@Composable
fun CategoryCard(title: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(title, color = if (color == AppYellow) Color.Black else Color.White, style = MaterialTheme.typography.titleLarge, fontSize = 28.sp)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, userRole: String) {
    NavigationBar(containerColor = AppRed) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            label = { Text("Home", color = Color.White) },
            selected = false,
            onClick = { navController.navigate(ROUTE_HOME) }
        )
        if (userRole == "admin") {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White) },
                label = { Text("Add Product", color = Color.White) },
                selected = false,
                onClick = { navController.navigate(ROUTE_ADD_PRODUCT) }
            )
        }
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White) },
            label = { Text("Settings", color = Color.White) },
            selected = false,
            onClick = { navController.navigate(ROUTE_SETTINGS) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "About", tint = Color.White) },
            label = { Text("About Us", color = Color.White) },
            selected = false,
            onClick = { navController.navigate(ROUTE_ABOUT_US) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Phone, contentDescription = "Contact", tint = Color.White) },
            label = { Text("Contact", color = Color.White) },
            selected = false,
            onClick = { navController.navigate(ROUTE_CONTACT_US) }
        )
    }
}
