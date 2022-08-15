package com.example.firebaseapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebaseapp.databinding.ActivityLoginBinding
import com.example.firebaseapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener{
            loginUser()
        }
    }



    private fun loginUser(){
        var email = binding.etEmail.text.toString()
        var password = binding.etPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoginStatus()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@LoginActivity,e.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun checkLoginStatus(){
        if(auth.currentUser == null){
            binding.tvStatus.text = "You are not logged in"
            binding.tvStatus.setTextColor(R.color.red)
            binding.tvStatus.visibility = View.VISIBLE
        }
        else{
            binding.tvStatus.text = "Welcome! You are logged in"
            binding.tvStatus.visibility = View.VISIBLE
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}