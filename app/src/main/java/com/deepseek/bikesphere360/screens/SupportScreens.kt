package com.deepseek.bikesphere360.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deepseek.bikesphere360.navigations.ROUTE_REPORT_PROBLEM
import com.deepseek.bikesphere360.navigations.ROUTE_PROFILE
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController) {
    val repository = FirebaseRepository()
    BikeSphereScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = AppYellow
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { navController.navigate(ROUTE_PROFILE) },
                colors = ButtonDefaults.buttonColors(containerColor = AppPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "View Profile",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate(ROUTE_REPORT_PROBLEM) },
                colors = ButtonDefaults.buttonColors(containerColor = AppYellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Report a Problem",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    repository.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Logout",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    val repository = FirebaseRepository()
    var user by remember { mutableStateOf<com.deepseek.bikesphere360.model.User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repository.getCurrentUserData { data ->
            user = data
            isLoading = false
        }
    }

    BikeSphereScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "My Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = AppYellow
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = AppYellow)
            } else if (user != null) {
                ProfileDetailItem("Username", user!!.username)
                ProfileDetailItem("Email", user!!.email)
                ProfileDetailItem("Phone", user!!.phone)
                ProfileDetailItem("Role", user!!.role)
            } else {
                Text("Failed to load user data", color = Color.White)
            }

            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("Back to Settings", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = AppYellow, style = MaterialTheme.typography.titleMedium)
            Text(value, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AboutUsScreen() {
    BikeSphereScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "About Us",
                style = MaterialTheme.typography.headlineLarge,
                color = AppYellow
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Welcome to BikesSphere360 where we are dedicated to serve our customers to ensure they get quality products",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "Our Vision",
                style = MaterialTheme.typography.headlineMedium,
                color = AppYellow
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "To be the outstanding shop for all your bikes needs",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "Welcome to BikesSphere360 where quality meets professionalism and style",
                color = AppYellow,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ContactUsScreen() {
    val context = LocalContext.current
    val phoneNumbers = listOf("0743102758", "0703481642", "0711516618")

    BikeSphereScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Contact Us",
                style = MaterialTheme.typography.headlineMedium,
                color = AppYellow
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Call Us:",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            phoneNumbers.forEach { num ->
                Text(
                    num,
                    color = AppYellow,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                            context.startActivity(intent)
                        }
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "Social Media:",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialIcon("Facebook") { /* Open FB link */ }
                    SocialIcon("Instagram") { /* Open IG link */ }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialIcon("TikTok") { /* Open TT link */ }
                    SocialIcon("WhatsApp") {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254743102758"))
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun SocialIcon(name: String, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = AppPink)) {
        Text(name, color = Color.Black)
    }
}

@Composable
fun ReportProblemScreen() {
    var problem by remember { mutableStateOf("") }
    val repository = FirebaseRepository()
    val context = LocalContext.current
    
    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Report a Problem", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = problem,
                onValueChange = { problem = it },
                label = { Text("Describe the problem you faced") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    if (problem.isNotEmpty()) {
                        repository.submitReport(problem) { success, msg ->
                            if (success) {
                                Toast.makeText(context, "Report submitted!", Toast.LENGTH_SHORT).show()
                                problem = ""
                            } else {
                                Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }, 
                colors = ButtonDefaults.buttonColors(containerColor = AppRed)
            ) {
                Text("Submit", color = Color.White)
            }
        }
    }
}
