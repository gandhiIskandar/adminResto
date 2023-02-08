package com.daharansemar.adminresto.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val PREFRENCE_NAME = "token_pref"

val Context.dataStore:DataStore<Preferences> by preferencesDataStore(name = PREFRENCE_NAME)

class DataStoreRepository(private val context: Context) {

    companion object{
        val TOKEN = stringPreferencesKey("token")
    }


    suspend fun saveToken(token:String){
        context.dataStore.edit { data ->
            data[TOKEN] = token
        }




    }

  fun getToken(): Flow<String>{
        return context.dataStore.data.map { data ->
            data[TOKEN]?: "token tidak ada"
        }
    }


}