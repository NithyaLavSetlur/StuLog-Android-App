package com.example.stulogandroidapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.activities.SignUpActivity
import com.example.stulogandroidapp.activities.HomeActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        db = AppDatabase.getDatabase(this)

        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtSignUp = findViewById<TextView>(R.id.txtSignUp)

        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            val user = db.userDao().authenticate(username, password)
            if (user != null) {
                // Save logged-in username to SharedPreferences
                val sharedPref = getSharedPreferences("StuLogPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("loggedInUsername", user.username)
                    apply()
                }

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }

        }

        txtSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
