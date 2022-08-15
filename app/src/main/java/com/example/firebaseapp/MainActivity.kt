package com.example.firebaseapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebaseapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener{
            registerUser()
        }

        binding.tvLoginScreen.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button4.setOnClickListener {
            val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.weclient_id))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(this,option)
            //val signInIntent:Intent=signInClient.signInIntent
            signInClient.signInIntent.also {
                startActivityForResult(it,REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE && data?.getData()!=null){
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    fun googleAuthForFirebase(account:GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                auth.signInWithCredential(credential).await()
                intent()
                Toast.makeText(this@MainActivity,"Successfully Logged In",Toast.LENGTH_SHORT).show()

            }
            catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun intent(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        checkLoginStatus()
    }

    private fun registerUser(){
        var email = binding.etEmail.text.toString()
        var password = binding.etPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoginStatus()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
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