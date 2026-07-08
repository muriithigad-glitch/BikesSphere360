package com.deepseek.bikesphere360.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deepseek.bikesphere360.model.Product
import com.deepseek.bikesphere360.navigations.*
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@Composable
fun CategorySelectionScreen(navController: NavController, type: String) {
    val subCategories = when (type) {
        "Motorbike" -> listOf("Sports motorbikes", "Road bikes", "Electric Motorbikes")
        "Bicycle" -> listOf("Racing Bicycles", "Mountain bikes")
        "Sparepart" -> listOf("Motorbike Spareparts", "Bicycle Spareparts")
        else -> emptyList()
    }

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Select $type Category", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            subCategories.forEach { sub ->
                Button(
                    onClick = { navController.navigate(createViewProductsRoute(type, sub)) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    Text(sub, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProductListScreen(navController: NavController, type: String, subCategory: String) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userRole by remember { mutableStateOf("user") }
    val repo = FirebaseRepository()

    fun loadProducts() {
        isLoading = true
        repo.getProductsByCategory(type, subCategory) { all ->
            products = all
            isLoading = false
        }
    }

    LaunchedEffect(type, subCategory) {
        repo.getCurrentUserRole { userRole = it }
        loadProducts()
    }

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("$subCategory", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (products.isEmpty()) {
                Text("No products found in this category.", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(products) { product ->
                        ProductCard(product, navController, userRole, onDeleted = { loadProducts() })
                    }
                }
            }
        }
    }
}

@Composable
fun HotDealsScreen(navController: NavController) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userRole by remember { mutableStateOf("user") }
    val repo = FirebaseRepository()

    fun loadHotDeals() {
        isLoading = true
        repo.getHotDeals { all ->
            products = all
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        repo.getCurrentUserRole { userRole = it }
        loadHotDeals()
    }

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Today's Hot Deals", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (products.isEmpty()) {
                Text("No hot deals today.", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(products) { product ->
                        ProductCard(product, navController, userRole, onDeleted = { loadHotDeals() })
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, navController: NavController, userRole: String, onDeleted: () -> Unit) {
    val context = LocalContext.current
    val repo = FirebaseRepository()

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text("Name: ${product.name}", style = MaterialTheme.typography.titleLarge, color = Color.Black)
            Text("Price: ${product.price}", color = Color.Black)
            Text("Color: ${product.color}", color = Color.Black)
            
            if (product.type == "Motorbike") {
                Text("Engine: ${product.engineCapacity}", color = Color.Black)
                Text("Year: ${product.yearOfMake}", color = Color.Black)
                Text("Top Speed: ${product.topSpeed}", color = Color.Black)
            } else if (product.type == "Bicycle") {
                Text("Gears: ${product.gearSpeed}", color = Color.Black)
                Text("Brand: ${product.brand}", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Purchase/Booking Button
            Button(
                onClick = {
                    if (product.type == "Sparepart") {
                        navController.navigate("checkout/${product.id}/${product.name}/${product.price}")
                    } else {
                        navController.navigate("book_appointment/${product.id}/${product.name}")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                Text(if (product.type == "Sparepart") "Buy Now (40% Downpayment)" else "Book Showroom Visit", color = Color.Black)
            }

            if (userRole == "admin") {
                Spacer(modifier = Modifier.height(8.dp))

                // Edit / Delete Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = { /* Navigate to Edit */ }) {
                        Text("Edit Product", color = AppPurple)
                    }
                    TextButton(onClick = { 
                        repo.deleteProduct(product) { success, err ->
                            if (success) {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                onDeleted()
                            } else {
                                Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("Delete Product", color = AppRed)
                    }
                }
            }
        }
    }
}
