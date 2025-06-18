package com.healthanalytics.android.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.healthanalytics.android.data.models.questionnaire.Questionnaire
import com.healthanalytics.android.data.models.questionnaire.QuestionnaireNextQuestionData
import com.healthanalytics.android.utils.EncryptionUtils.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    val IS_LOGIN = booleanPreferencesKey("is_login")
    
    // User Details Keys
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PHONE = stringPreferencesKey("user_phone")
    val USER_ADDRESS = stringPreferencesKey("user_address")
    val USER_PINCODE = stringPreferencesKey("user_pincode")
    val USER_STATE = stringPreferencesKey("user_state")
    val USER_DISTRICT = stringPreferencesKey("user_district")
    val USER_COUNTRY = stringPreferencesKey("user_country")
    val ADDRESS_LINE_1 = stringPreferencesKey("address_line_1")
    val ADDRESS_LINE_2 = stringPreferencesKey("address_line_2")
    val CITY = stringPreferencesKey("city")
    val STATE = stringPreferencesKey("state")
    val PINCODE = stringPreferencesKey("pincode")
    val COUNTRY = stringPreferencesKey("country")
    val ADDRESS_ID = stringPreferencesKey("address_id")
    val GENDER = stringPreferencesKey("gender")
    val NEXT_QUESTIONNAIRES_KEY = stringPreferencesKey("next_questionnaires_data")

}

/**
 * Repository for managing user preferences using DataStore.
 */
class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN_KEY]
        }
        .catch { _ ->
            // Log the error or handle it as needed
            emit(null)
        }

    val isLogin: Flow<Boolean?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGIN]
        }
        .catch { _ ->
            emit(null)
        }

    // User Details Flows
    val userName: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_NAME] }
        .catch { emit(null) }

    val userEmail: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_EMAIL] }
        .catch { emit(null) }

    val userPhone: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_PHONE] }
        .catch { emit(null) }

    val userAddress: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_ADDRESS] }
        .catch { emit(null) }

    val userPincode: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_PINCODE] }
        .catch { emit(null) }

    val userState: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_STATE] }
        .catch { emit(null) }

    val userDistrict: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_DISTRICT] }
        .catch { emit(null) }

    val userCountry: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_COUNTRY] }
        .catch { emit(null) }

    // Address getters
    val addressLine1: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.ADDRESS_LINE_1] }
    
    val addressLine2: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.ADDRESS_LINE_2] }
    
    val city: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.CITY] }
    
    val state: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.STATE] }
    
    val pincode: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.PINCODE] }
    
    val country: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.COUNTRY] }
    
    val addressId: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.ADDRESS_ID] }

    val gender: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.GENDER] }

    val nextQuestionnaire: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.NEXT_QUESTIONNAIRES_KEY] }
        .catch {
            emit("[]")
        }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun saveIsLogin(isLogin: Boolean) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.IS_LOGIN] = isLogin
        }
    }

    // Save User Details Methods
    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }

    suspend fun saveUserPhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_PHONE] = phone
        }
    }

    suspend fun saveUserAddress(address: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ADDRESS] = address
        }
    }

    suspend fun saveUserPincode(pincode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_PINCODE] = pincode
        }
    }

    suspend fun saveUserState(state: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_STATE] = state
        }
    }

    suspend fun saveUserDistrict(district: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_DISTRICT] = district
        }
    }

    suspend fun saveUserCountry(country: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_COUNTRY] = country
        }
    }

    suspend fun saveGender(gender: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GENDER] = gender
        }
    }

    suspend fun saveNextQuestionnaireData(nextQuestionnaire:String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NEXT_QUESTIONNAIRES_KEY] = nextQuestionnaire
        }
    }

    // Convenience method to save all user details at once
    suspend fun saveUserDetails(
        name: String,
        email: String,
        phone: String,
        address: String,
        pincode: String,
        state: String,
        district: String,
        country: String
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_PHONE] = phone
            preferences[PreferencesKeys.USER_ADDRESS] = address
            preferences[PreferencesKeys.USER_PINCODE] = pincode
            preferences[PreferencesKeys.USER_STATE] = state
            preferences[PreferencesKeys.USER_DISTRICT] = district
            preferences[PreferencesKeys.USER_COUNTRY] = country
        }
    }

    // Address setters
    suspend fun saveAddress(
        addressLine1: String?,
        addressLine2: String?,
        city: String?,
        state: String?,
        pincode: String?,
        country: String?,
        addressId: String?
    ) {
        dataStore.edit { preferences ->
            if (addressLine1 != null) preferences[PreferencesKeys.ADDRESS_LINE_1] = addressLine1
            if (addressLine2 != null) preferences[PreferencesKeys.ADDRESS_LINE_2] = addressLine2
            if (city != null) preferences[PreferencesKeys.CITY] = city
            if (state != null) preferences[PreferencesKeys.STATE] = state
            if (pincode != null) preferences[PreferencesKeys.PINCODE] = pincode
            if (country != null) preferences[PreferencesKeys.COUNTRY] = country
            if (addressId != null) preferences[PreferencesKeys.ADDRESS_ID] = addressId
        }
    }

    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
} 