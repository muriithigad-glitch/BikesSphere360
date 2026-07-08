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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deepseek.bikesphere360.navigations.ROUTE_REPORT_PROBLEM
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController) {
    val repository = FirebaseRepository()
    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(ROUTE_REPORT_PROBLEM) },
                colors = ButtonDefaults.buttonColors(containerColor = AppYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Report a Problem", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    repository.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Composable
fun AboutUsScreen() {
    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("About Us", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Bike Sphere360 is your one-stop shop for Motorbikes, Bicycles, and Spareparts. " +
                        "We provide the best deals in town!",
                color = Color.White
            )
        }
    }
}

@Composable
fun ContactUsScreen() {
    val context = LocalContext.current
    val phoneNumbers = listOf("0743102758", "0703481642", "0711516618")

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Contact Us", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Call Us:", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            phoneNumbers.forEach { num ->
                Text(num, color = AppYellow, modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                    context.startActivity(intent)
                }.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Social Media:", color = Color.White)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SocialIcon("Facebook") { /* Open FB link */ }
                SocialIcon("Instagram") { /* Open IG link */ }
                SocialIcon("TikTok") { /* Open TT link */ }
                SocialIcon("WhatsApp") {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254743102758"))
                    context.startActivity(intent)
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
