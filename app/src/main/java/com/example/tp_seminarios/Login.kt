package com.example.tp_seminarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.io.path.Path

class Login : AppCompatActivity() {

    private lateinit var tvOlvidoPassword: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbCheckPassword: CheckBox
    private lateinit var btnIniciarSession: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvOlvidoPassword = findViewById(R.id.olvidoPassword);
        btnIniciarSession = findViewById(R.id.btnIniciarSession)
        etEmail = findViewById(R.id.inputEmail)
        etPassword = findViewById(R.id.inputPassword)
        cbCheckPassword = findViewById(R.id.cbRecordarPassword)
        handlerOlvidoPassword(tvOlvidoPassword)
        iniciarSession(btnIniciarSession)

    }

    private fun handlerOlvidoPassword(tvOlvidoPassword: TextView){
        tvOlvidoPassword.setOnClickListener{
            val intent = Intent(this, ForgetPassword::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun iniciarSession(btnIniciarSession: Button){

        btnIniciarSession.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when{
                email.isEmpty() ->{
                    etEmail.error = "El email no puede estar vacio"
                    etEmail.requestFocus()
                }
                !email.contains("@") ->{
                    etEmail.error = "El formato del email es errado"
                    etEmail.requestFocus()
                }
                password.isEmpty() ->{
                    etPassword.error = "La contraseña no puede estar vacia"
                    etPassword.requestFocus()
                }
                else ->{
                    if(cbCheckPassword.isChecked){
                        Toast.makeText(this,"Usuario recordado", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, Principal::class.java)
                    startActivity(intent)
                }
            }

        }
    }
}