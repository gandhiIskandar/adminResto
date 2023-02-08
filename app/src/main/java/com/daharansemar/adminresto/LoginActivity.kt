package com.daharansemar.adminresto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daharansemar.adminresto.databinding.ActivityLoginBinding
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.viewModel.AuthViewmodel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding

    private val authViewmodel: AuthViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        with(binding) {
            login.setOnClickListener {
                if (checkEt()) {
                    authViewmodel.loginAttempt(
                        listOf(
                            emailLogin.text.toString(),
                            passwordLogin.text.toString()
                        )
                    )
                } else {
                    Utilization.toastMaker(this@LoginActivity, "Lengkapi email dan password").show()
                }


            }
        }

        setContentView(binding.root as  View)

        authViewmodel.statusLogin.observe(this, Observer {
            toMainActivity()
        })


    }

    override fun onStart() {
        super.onStart()

        authViewmodel.spontanCheckUser {
            if(it){
                toMainActivity()
            }
        }
    }

    private fun toMainActivity(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun checkEt(): Boolean {
        var pass = true
        if (binding.emailLogin.text!!.isEmpty() || binding.passwordLogin.text!!.isEmpty()) {
            pass = false
        }
        return pass
    }
}