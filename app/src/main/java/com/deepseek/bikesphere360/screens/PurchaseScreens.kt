package com.deepseek.bikesphere360.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deepseek.bikesphere360.model.Appointment
import com.deepseek.bikesphere360.model.Order
import com.deepseek.bikesphere360.navigations.ROUTE_HOME
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.network.MpesaService
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun SparePartCheckoutScreen(navController: NavController, productId: String, productName: String, price: String) {
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    
    // Extract numeric price and calculate 40% default
    val numericPrice = price.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
    val defaultDownPayment = numericPrice * 0.4
    
    var phoneForMpesa by remember { mutableStateOf("") }
    var paymentAmount by remember { mutableStateOf(String.format("%.2f", defaultDownPayment)) }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val repo = FirebaseRepository()
    val mpesaService = MpesaService()

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Checkout: $productName", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Total Product Price: $price", color = Color.White)
            Text("Minimum Downpayment (40%): KES ${String.format("%.2f", defaultDownPayment)}", color = AppYellow)
            
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = paymentAmount,
                onValueChange = { paymentAmount = it },
                label = { Text("Amount to Pay Now (KES)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phoneForMpesa,
                onValueChange = { phoneForMpesa = it },
                label = { Text("M-Pesa Phone Number (e.g. 2547...)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = address, onValueChange = { address = it }, label = { Text("Home Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = location, onValueChange = { location = it }, label = { Text("Delivery Location") }, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Estimated Delivery: 2 to 3 days", color = AppGreen, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val finalAmount = paymentAmount.toDoubleOrNull() ?: 0.0
                    if (address.isEmpty() || location.isEmpty() || phoneForMpesa.isEmpty() || finalAmount <= 0) {
                        Toast.makeText(context, "Please fill all details correctly", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    if (finalAmount < defaultDownPayment) {
                        Toast.makeText(context, "Amount must be at least 40% (KES ${String.format("%.2f", defaultDownPayment)})", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    isLoading = true
                    
                    // 1. Initiate Mpesa Payment with manual phone and amount
                    mpesaService.initiateStkPush(phoneForMpesa, finalAmount.toInt()) { success, mpesaError ->
                        if (success) {
                            // 2. If prompt successful, save order
                            val order = Order(
                                productId = productId,
                                productName = productName,
                                address = address,
                                location = location,
                                totalAmount = price,
                                paidAmount = finalAmount
                            )
                            repo.placeOrder(order) { dbSuccess, dbError ->
                                isLoading = false
                                if (dbSuccess) {
                                    Toast.makeText(context, "Payment prompt sent to $phoneForMpesa! Order Placed.", Toast.LENGTH_LONG).show()
                                    navController.navigate(ROUTE_HOME)
                                } else {
                                    Toast.makeText(context, "Database Error: $dbError", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, "M-Pesa Error: $mpesaError", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Pay KES $paymentAmount & Order", color = Color.White)
            }
        }
    }
}

@Composable
fun BikeBookingScreen(navController: NavController, productId: String, productName: String) {
    var visitDate by remember { mutableStateOf("") }
    var visitTime by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val repo = FirebaseRepository()

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Book Showroom Visit", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text(productName, color = AppYellow)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Showroom Location:", style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("BikeSphere360 Showroom\nWestlands, Nairobi")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Please contact an attendant to confirm availability:")
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { 
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0743102758"))
                            context.startActivity(intent)
                        }, colors = ButtonDefaults.buttonColors(containerColor = AppPink)) {
                            Text("Call Us", color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Pick a day and date to visit:", color = Color.White)
            TextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text("Visit Date (e.g. 12th Oct)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = visitTime, onValueChange = { visitTime = it }, label = { Text("Visit Day/Time (e.g. Monday, 10 AM)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (visitDate.isEmpty() || visitTime.isEmpty()) {
                        Toast.makeText(context, "Please set date and time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    val appt = Appointment(
                        productId = productId,
                        productName = productName,
                        visitDate = visitDate,
                        visitTime = visitTime
                    )
                    repo.bookAppointment(appt) { success, err ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "Appointment Booked! See you in Westlands", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_HOME)
                        } else {
                            Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Confirm Appointment", color = Color.White)
            }
        }
    }
}
