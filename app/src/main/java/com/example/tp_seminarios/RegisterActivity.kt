package com.example.tp_seminarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.example.tp_seminarios.data.Usuario
import com.example.tp_seminarios.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnIniciarSession: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var etUserName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var tvRegistrandoDato: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUserName = findViewById(R.id.inputUsername)
        etEmail = findViewById(R.id.inputEmail)
        etPassword = findViewById(R.id.inputPassword)
        etConfirmPassword = findViewById(R.id.inputConfirmPassword)
        btnIniciarSession = findViewById(R.id.btnIniciarSession)
        tvRegistrandoDato = findViewById(R.id.tvRegistrandoDatos)
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

                !email.contains("@") || !email.contains(".com") -> {
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
                    val usuario = Usuario(etUserName.text.toString(),etPassword.text.toString(),etEmail.text.toString());
                    registrandoDatosConHilos(usuario)
                }
            }

        }
    }
    private fun registrandoDatosConHilos(usuario: Usuario){
        lifecycleScope.launch {
            tvRegistrandoDato.setText("Cargando....")
            val userDb = withContext(Dispatchers.IO){
                delay(5000)
                AppDatabase.getDataBase(applicationContext).usuarioDao().getUsuarioPorEmail(usuario.email)
            }
            if(userDb != null){
                tvRegistrandoDato.setTextColor(AppCompatResources.getColorStateList(this@RegisterActivity,R.color.red))
                tvRegistrandoDato.setText("El usuario ya existe en el sistema")
            }else{
                tvRegistrandoDato.setText("El usuario ha sido creado con exito!!")
                AppDatabase.getDataBase(applicationContext).usuarioDao().insertUsuario(usuario)
                delay(5000)
                goToPrincipal()
            }

        }

    }

    private fun goToPrincipal(){
        val intent = Intent(this, Principal::class.java)
        startActivity(intent)
        finish()
    }
}
