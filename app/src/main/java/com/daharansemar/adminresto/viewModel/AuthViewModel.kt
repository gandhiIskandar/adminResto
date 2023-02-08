package com.daharansemar.adminresto.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daharansemar.adminresto.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewmodel : ViewModel() {

    val repo = Repository.getInstance()

    private val _statusLogin = MutableLiveData<Boolean>()
    val statusLogin: LiveData<Boolean>
        get() = _statusLogin

    fun loginAttempt(list: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.loginAttempt(list) {
                _statusLogin.postValue(it)
            }
        }


    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentUser {
                _statusLogin.postValue(it)
            }
        }

    }

    fun spontanCheckUser(invoker:(Boolean)->Unit){
        viewModelScope.launch(Dispatchers.Main) {
            repo.getCurrentUser {
                invoker.invoke(it)
            }
        }
    }

}

