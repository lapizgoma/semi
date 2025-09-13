package com.example.tp_seminarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tp_seminarios.data.Usuario
import com.example.tp_seminarios.database.AppDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnIniciarSession: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var etUserName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUserName = findViewById(R.id.inputUsername)
        etEmail = findViewById(R.id.inputEmail)
        etPassword = findViewById(R.id.inputPassword)
        etConfirmPassword = findViewById(R.id.inputConfirmPassword)
        btnIniciarSession = findViewById(R.id.btnIniciarSession)
        iniciarSession(btnIniciarSession)
        btnGoToRegister = findViewById(R.id.btnRegister)
        goToRegister(btnGoToRegister)
    }

    private fun iniciarSession(btnIniciarSession: Button) {

        btnIniciarSession.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun goToRegister(btnRegsiter: Button) {
        btnRegsiter.setOnClickListener {

            val username = etUserName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            when {
                username.isEmpty() -> {
                    etUserName.error = "El nombre de usuario no puede estar vacio"
                    etUserName.requestFocus()
                }

                email.isEmpty() -> {
                    etEmail.error = "El email no puede estar vacio"
                    etEmail.requestFocus()
                }

                !email.contains("@") -> {
                    etEmail.error = "El formato del email es errado"
                    etEmail.requestFocus()
                }

                password.isEmpty() -> {
                    etPassword.error = "La contraseña no puede estar vacia"
                    etPassword.requestFocus()
                }
                confirmPassword.isEmpty() -> {
                    etConfirmPassword.error = "La contraseña no puede estar vacia"
                    etConfirmPassword.requestFocus()
                }
                !password.equals(confirmPassword) -> {
                    etPassword.error = "Las contraseñas no son iguales"
                    etPassword.requestFocus()
                }

                else -> {
                    val intent = Intent(this, Principal::class.java)
                    val usuario = Usuario(etUserName.text.toString(),etPassword.text.toString(),etEmail.text.toString());
                    AppDatabase.getDataBase(applicationContext).usuarioDao().insertUsuario(usuario)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
