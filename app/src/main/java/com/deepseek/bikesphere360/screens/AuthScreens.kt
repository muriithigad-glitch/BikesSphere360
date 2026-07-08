package com.deepseek.bikesphere360.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deepseek.bikesphere360.model.User
import com.deepseek.bikesphere360.navigations.*
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun LoginScreen(navController: NavController) {
    var identifier by remember { mutableStateOf("") } // Email or Username
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val repository = FirebaseRepository()

    BikeSphereScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Text("Bike Sphere360 Login", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Username or Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (identifier.isEmpty() || password.isEmpty()) {
                    errorMessage = "Please enter all details"
                    return@Button
                }
                isLoading = true
                val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()
                
                val loginCallback: (Boolean, String?) -> Unit = { success, msg ->
                    isLoading = false
                    if (success) {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                        }
                    } else {
                        errorMessage = msg
                    }
                }

                if (isEmail) {
                    repository.login(identifier, password, loginCallback)
                } else {
                    repository.loginWithUsername(identifier, password, loginCallback)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White)
            else Text("Login", color = Color.White)
        }

        TextButton(onClick = { navController.navigate(ROUTE_REGISTER) }) {
            Text("Don't have an account? Register", color = Color.White)
        }
    } }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val repository = FirebaseRepository()

    BikeSphereScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Text("Create Account", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))

        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                isLoading = true
                val user = User(username = username, email = email, phone = phone)
                repository.register(user, password) { success, msg ->
                    isLoading = false
                    if (success) {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_REGISTER) { inclusive = true }
                        }
                    } else {
                        errorMessage = msg
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White)
            else Text("Register", color = Color.White)
        }

        TextButton(onClick = { navController.navigate(ROUTE_LOGIN) }) {
            Text("Already have an account? Login", color = Color.White)
        }
    } }
}
