package com.deepseek.bikesphere360.network

import android.util.Base64
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MpesaService {
    private val client = OkHttpClient()
    private val consumerKey = "oBNPXPPx58f65DOTPeQL5yQhVdtrwmVpjH8PhfdUNGyQWvrw"
    private val consumerSecret = "osCLR8Qy3cIDGsLf3AtweeOInJe5Wud02wxxGGZSxaA7gsAxc5dWdgawvBIbE8II"
    private val shortCode = "174379"
    private val passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    
    private val baseUrl = "https://sandbox.safaricom.co.ke"

    fun getAccessToken(callback: (String?) -> Unit) {
        val keys = "$consumerKey:$consumerSecret"
        val auth = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
        
        val request = Request.Builder()
            .url("$baseUrl/oauth/v1/generate?grant_type=client_credentials")
            .get()
            .addHeader("Authorization", auth)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                if (bodyString != null) {
                    val json = JSONObject(bodyString)
                    callback(json.optString("access_token"))
                } else {
                    callback(null)
                }
            }
        })
    }

    fun initiateStkPush(phoneNumber: String, amount: Int, callback: (Boolean, String?) -> Unit) {
        getAccessToken { token ->
            if (token == null) {
                callback(false, "Failed to get access token")
                return@getAccessToken
            }

            val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val password = Base64.encodeToString(
                (shortCode + passKey + timestamp).toByteArray(),
                Base64.NO_WRAP
            )

            val json = JSONObject().apply {
                put("BusinessShortCode", shortCode)
                put("Password", password)
                put("Timestamp", timestamp)
                put("TransactionType", "CustomerPayBillOnline")
                put("Amount", amount)
                put("PartyA", phoneNumber)
                put("PartyB", shortCode)
                put("PhoneNumber", phoneNumber)
                put("CallBackURL", "https://mydomain.com/path") // Placeholder for Daraja API
                put("AccountReference", "BikeSphere360")
                put("TransactionDesc", "Product Payment")
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$baseUrl/mpesa/stkpush/v1/processrequest")
                .post(body)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false, e.message)
                }
                override fun onResponse(call: Call, response: Response) {
                    val resBodyString = response.body?.string()
                    if (response.isSuccessful && resBodyString != null) {
                        callback(true, null)
                    } else {
                        val errorMsg = try {
                            JSONObject(resBodyString ?: "{}").optString("errorMessage", "Error")
                        } catch (e: Exception) {
                            "Unknown M-Pesa error"
                        }
                        callback(false, errorMsg)
                    }
                }
            })
        }
    }
}
