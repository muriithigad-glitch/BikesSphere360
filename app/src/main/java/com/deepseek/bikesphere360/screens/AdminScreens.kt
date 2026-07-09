package com.deepseek.bikesphere360.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deepseek.bikesphere360.model.*
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val repo = FirebaseRepository()

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Admin Dashboard", style = MaterialTheme.typography.titleLarge, color = Color.White)
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = AppGreen
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Users", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Orders", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Bookings", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> UsersList(repo)
                1 -> OrdersList(repo)
                2 -> AppointmentsList(repo)
            }
        }
    }
}

@Composable
fun UsersList(repo: FirebaseRepository) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    LaunchedEffect(Unit) { repo.getAllUsers { users = it } }

    LazyColumn {
        items(users) { user ->
            AdminCard {
                Text("Username: ${user.username}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("Email: ${user.email}")
                Text("Phone: ${user.phone}")
                Text("Role: ${user.role}", color = if (user.role == "admin") AppRed else Color.Gray)
            }
        }
    }
}

@Composable
fun OrdersList(repo: FirebaseRepository) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    LaunchedEffect(Unit) { repo.getAllOrders { orders = it } }

    LazyColumn {
        items(orders) { order ->
            AdminCard {
                Text("Product: ${order.productName}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("Paid (40%): ${String.format("%.2f", order.paidAmount)}")
                Text("Address: ${order.address}")
                Text("Location: ${order.location}")
            }
        }
    }
}

@Composable
fun AppointmentsList(repo: FirebaseRepository) {
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    LaunchedEffect(Unit) { repo.getAllAppointments { appointments = it } }

    LazyColumn {
        items(appointments) { appt ->
            AdminCard {
                Text("Bike: ${appt.productName}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("Visit Date: ${appt.visitDate}")
                Text("Visit Time: ${appt.visitTime}")
                Text("Showroom: Westlands, Nairobi")
            }
        }
    }
}

@Composable
fun AdminCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppBlue.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}
