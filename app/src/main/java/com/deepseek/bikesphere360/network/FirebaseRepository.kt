package com.deepseek.bikesphere360.network

import com.deepseek.bikesphere360.model.Product
import com.deepseek.bikesphere360.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import android.content.Context
import com.deepseek.bikesphere360.model.Appointment
import com.deepseek.bikesphere360.model.Order

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance("https://bike-sphere360-default-rtdb.firebaseio.com/")

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        
        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            onResult(false, "Email and Password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginWithUsername(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            onResult(false, "Username and Password cannot be empty")
            return
        }

        // Search for user by username in the Users node
        db.getReference("Users").orderByChild("username").equalTo(trimmedUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null) {
                            login(user.email, trimmedPassword, onResult)
                        } else {
                            onResult(false, "User data error")
                        }
                    } else {
                        onResult(false, "Username not found")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(false, error.message)
                }
            })
    }

    fun register(user: User, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = user.email.trim()
        val trimmedPassword = password.trim()
        val trimmedUsername = user.username.trim()

        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty() || trimmedUsername.isEmpty()) {
            onResult(false, "All fields are required")
            return
        }

        auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val updatedUser = user.copy(id = userId)
                    db.getReference("Users").child(userId).setValue(updatedUser)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, dbTask.exception?.message)
                            }
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun addProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        // Save to structured node: Products/Type/SubCategory/ID
        val ref = db.getReference("Products")
            .child(product.type)
            .child(product.subCategory)
            .push()
        
        val productWithId = product.copy(id = ref.key ?: "")
        ref.setValue(productWithId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }

    fun getProductsByCategory(type: String, subCategory: String, onResult: (List<Product>) -> Unit) {
        db.getReference("Products").child(type).child(subCategory)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                    onResult(products)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun getHotDeals(onResult: (List<Product>) -> Unit) {
        // Fetch all products and filter for hot deals
        db.getReference("Products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotDeals = mutableListOf<Product>()
                // Products -> Type -> SubCategory -> Product
                for (typeSnapshot in snapshot.children) {
                    for (subSnapshot in typeSnapshot.children) {
                        for (productSnapshot in subSnapshot.children) {
                            val product = productSnapshot.getValue(Product::class.java)
                            if (product?.isHotDeal == true) {
                                hotDeals.add(product)
                            }
                        }
                    }
                }
                onResult(hotDeals)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserRole(onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult("user")
        db.getReference("Users").child(uid).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(snapshot.getValue(String::class.java) ?: "user")
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult("user")
                }
            })
    }

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        db.getReference("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                onResult(users)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

    fun getAllOrders(onResult: (List<Order>) -> Unit) {
        db.getReference("Orders").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                onResult(orders)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

    fun getAllAppointments(onResult: (List<Appointment>) -> Unit) {
        db.getReference("Appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appts = snapshot.children.mapNotNull { it.getValue(Appointment::class.java) }
                onResult(appts)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

    fun deleteProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        db.getReference("Products")
            .child(product.type)
            .child(product.subCategory)
            .child(product.id)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.message)
            }
    }

    fun placeOrder(order: Order, onResult: (Boolean, String?) -> Unit) {
        val ref = db.getReference("Orders").push()
        val orderWithId = order.copy(id = ref.key ?: "")
        ref.setValue(orderWithId).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, null)
            else onResult(false, task.exception?.message)
        }
    }

    fun bookAppointment(appointment: Appointment, onResult: (Boolean, String?) -> Unit) {
        val ref = db.getReference("Appointments").push()
        val appointmentWithId = appointment.copy(id = ref.key ?: "")
        ref.setValue(appointmentWithId).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, null)
            else onResult(false, task.exception?.message)
        }
    }

    fun submitReport(description: String, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: "anonymous"
        val ref = db.getReference("Reports").push()
        val report = mapOf(
            "id" to (ref.key ?: ""),
            "userId" to userId,
            "description" to description,
            "timestamp" to System.currentTimeMillis()
        )
        ref.setValue(report).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }

    fun uploadImage(context: Context, uri: Uri, onResult: (url: String?, error: String?) -> Unit) {
        // Initialize Cloudinary if not already initialized
        try {
            val config = mapOf(
                "cloud_name" to "dcphfjmtj",
                "secure" to true
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // Already initialized or other error
        }

        MediaManager.get().upload(uri)
            .unsigned("bikesphere360_products")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    onResult(url, null)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    onResult(null, error.description)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    onResult(null, "Upload rescheduled")
                }
            })
            .dispatch()
    }
}
