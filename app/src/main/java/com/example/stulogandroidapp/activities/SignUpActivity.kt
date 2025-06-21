package com.example.stulogandroidapp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        db = AppDatabase.getDatabase(this)

        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                db.userDao().insertUser(User(username = username, password = password))
                Toast.makeText(this, "Account created. Go back to sign in.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
