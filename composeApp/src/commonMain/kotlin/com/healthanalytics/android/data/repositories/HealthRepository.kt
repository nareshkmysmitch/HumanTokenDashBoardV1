package com.healthanalytics.android.data.repositories

import com.healthanalytics.android.data.models.AuthResponse
import com.healthanalytics.android.data.models.Biomarker
import com.healthanalytics.android.data.models.CartItem
import com.healthanalytics.android.data.models.Product
import com.healthanalytics.android.data.network.ApiClient

class HealthRepository {
    private val apiClient = ApiClient()
    
    suspend fun sendOtp(phone: String): Result<AuthResponse> {
        return try {
            val response = apiClient.sendOtp(phone)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyOtp(phone: String, otp: String): Result<AuthResponse> {
        return try {
            val response = apiClient.verifyOtp(phone, otp)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getHealthData(token: String): Result<List<Biomarker>> {
        return try {
            val response = apiClient.getHealthData(token)
            if (response.status == "success" && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProducts(token: String): Result<List<Product>> {
        return try {
            val response = apiClient.getProducts(token)
            if (response.status == "success" && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCartItems(token: String): Result<List<CartItem>> {
        return try {
            val response = apiClient.getCartItems(token)
            if (response.status == "success" && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}