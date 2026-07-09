package com.deepseek.bikesphere360.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deepseek.bikesphere360.model.Product
import com.deepseek.bikesphere360.navigations.ROUTE_HOME
import com.deepseek.bikesphere360.network.FirebaseRepository
import com.deepseek.bikesphere360.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController) {
    var type by remember { mutableStateOf("Motorbike") }
    var subCategory by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isHotDeal by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val subCategories = when (type) {
        "Motorbike" -> listOf("Sports motorbikes", "Road bikes", "Electric Motorbikes")
        "Bicycle" -> listOf("Racing Bicycles", "Mountain bikes")
        "Sparepart" -> listOf("Motorbike Spareparts", "Bicycle Spareparts")
        else -> emptyList()
    }

    var expanded by remember { mutableStateOf(false) }
    var engineCapacity by remember { mutableStateOf("") }
    var yearOfMake by remember { mutableStateOf("") }
    var topSpeed by remember { mutableStateOf("") }
    var gearSpeed by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }

    val repo = FirebaseRepository()

    BikeSphereScreen {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Add New Product", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            // Type Selection
            Text("Product Type:", color = Color.White)
            Row {
                RadioButton(selected = type == "Motorbike", onClick = { type = "Motorbike"; subCategory = "" })
                Text("Motorbike", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                RadioButton(selected = type == "Bicycle", onClick = { type = "Bicycle"; subCategory = "" })
                Text("Bicycle", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                RadioButton(selected = type == "Sparepart", onClick = { type = "Sparepart"; subCategory = "" })
                Text("Sparepart", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-Category Selection (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = subCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Sub-Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    subCategories.forEach { sub ->
                        DropdownMenuItem(
                            text = { Text(sub) },
                            onClick = {
                                subCategory = sub
                                expanded = false
                            }
                        )
                    }
                }
            }

            TextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
            TextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
            TextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = AppPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image from Gallery", color = Color.White)
            }

            imageUri?.let {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isHotDeal = !isHotDeal },
                colors = CardDefaults.cardColors(containerColor = if (isHotDeal) AppGreen.copy(alpha = 0.2f) else Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isHotDeal) AppGreen else Color.Gray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Checkbox(
                        checked = isHotDeal,
                        onCheckedChange = { isHotDeal = it },
                        colors = CheckboxDefaults.colors(checkedColor = AppGreen)
                    )
                    Text(
                        "Set as Today's Hot Deal",
                        color = if (isHotDeal) AppGreen else Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }

            if (type == "Motorbike") {
                TextField(value = engineCapacity, onValueChange = { engineCapacity = it }, label = { Text("Engine Capacity") }, modifier = Modifier.fillMaxWidth())
                TextField(value = yearOfMake, onValueChange = { yearOfMake = it }, label = { Text("Year of Make") }, modifier = Modifier.fillMaxWidth())
                TextField(value = topSpeed, onValueChange = { topSpeed = it }, label = { Text("Top Speed") }, modifier = Modifier.fillMaxWidth())
            } else if (type == "Bicycle") {
                TextField(value = gearSpeed, onValueChange = { gearSpeed = it }, label = { Text("Gear Speed") }, modifier = Modifier.fillMaxWidth())
                TextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (imageUri == null || subCategory.isEmpty() || name.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    repo.uploadImage(context, imageUri!!) { url, error ->
                        if (url != null) {
                            val product = Product(
                                type = type, subCategory = subCategory, name = name, price = price,
                                color = color, imageUrl = url, isHotDeal = isHotDeal,
                                engineCapacity = engineCapacity, yearOfMake = yearOfMake, topSpeed = topSpeed,
                                gearSpeed = gearSpeed, brand = brand
                            )
                            repo.addProduct(product) { success, dbError ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Product Saved Successfully!", Toast.LENGTH_LONG).show()
                                    navController.navigate(ROUTE_HOME)
                                } else {
                                    Toast.makeText(context, "Database Error: $dbError", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Image upload failed: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Save Product", color = Color.White)
            }
        }
    }
}
