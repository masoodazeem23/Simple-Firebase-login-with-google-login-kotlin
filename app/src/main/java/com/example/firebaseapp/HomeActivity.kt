package com.example.firebaseapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.firebaseapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()



        binding.textView2.text = auth.currentUser?.let {
            it.email
        }

        binding.button2.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button3.setOnClickListener {
            updateUserProfile()
        }

        checkLoginStatus()
    }

    fun updateUserProfile(){
        auth.currentUser?.let{ user ->
            val username = binding.editTextTextPersonName.text.toString()
            val photoUri = Uri.parse("android.resource://$packageName/${R.drawable.image}")
            val updateProfile = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try{
                    user.updateProfile(updateProfile).await()
                    checkLoginStatus()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@HomeActivity,"Profile updated succesfully", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Log.d("exception 12 :", e.message.toString());
                        Toast.makeText(this@HomeActivity,e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    fun checkLoginStatus(){
        if(auth.currentUser != null) {
            CoroutineScope(Dispatchers.Main).launch {
                if (auth.currentUser?.photoUrl != null) {
                    binding.imageView.setImageURI(auth.currentUser?.photoUrl)
                    Log.d("checkLoginStatus 12 :", auth.currentUser?.photoUrl.toString());
                } else {
                   // binding.imageView.setImageResource(R.drawable.userimage)
                    Log.d("checkLoginStatus 12 :","iam at resource" );
                }
                binding.textView2.setText(auth.currentUser?.displayName)
            }
        }
    }




}