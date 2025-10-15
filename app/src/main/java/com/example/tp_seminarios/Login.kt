package com.example.tp_seminarios

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tp_seminarios.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    companion object {
        val SESSION_PERSISTANCE_ID: Int = 101

        fun forgetSession (
            context: Context
        ) {
            val preferencias = context.getSharedPreferences(context.resources.getString(R.string.sp_credenciales), Context.MODE_PRIVATE)
            preferencias.edit().putString(context.resources.getString(R.string.usuario),"").apply()
            preferencias.edit().putString(context.resources.getString(R.string.password),"").apply()

            clearSessionPersistenceNotification (context)

        }

        fun persistSession (
            email : String,
            password : String,
            context: Context
        ) {
            val preferencias = context.getSharedPreferences(context.resources.getString(R.string.sp_credenciales),MODE_PRIVATE)
            preferencias.edit().putString(context.resources.getString(R.string.usuario), email).apply()
            preferencias.edit().putString(context.resources.getString(R.string.password), password).apply()
        }

        fun sessionPersistenceNotification(context: Context): Notification {
            // intents
            val forgetIntent = Intent(context, ForgetSessionReceiver::class.java)
            val forgetPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, forgetIntent, PendingIntent.FLAG_IMMUTABLE)

            val tapIntent = Intent(context, Principal::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val okIntent = Intent(context, Principal::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("dismiss", true)
            }
            val okPendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, okIntent, PendingIntent.FLAG_IMMUTABLE)

            // notificación
            return NotificationCompat.Builder(context, getString(context, R.string.session_persistence_id))
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Session status")
                .setContentText("Su sesión está persistida.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setContentIntent(PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE))
                .addAction(R.drawable.app_icon, "FORGET", forgetPendingIntent)
                .addAction(R.drawable.app_icon, "OK", okPendingIntent)
                .build()
        }

        // notificar
        fun notifySessionPersistance (context: Context)
        {
            if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Notification", "No se otorgaron permisos para las notificaciones.")
            return
        }
            NotificationManagerCompat
                .from (context)
                .notify (SESSION_PERSISTANCE_ID, sessionPersistenceNotification (context))
        }

        // despejar notificación
        fun clearSessionPersistenceNotification (context: Context) {
            NotificationManagerCompat
                .from (context)
                .cancel (SESSION_PERSISTANCE_ID)
        }
    }
    private lateinit var tvOlvidoPassword: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbCheckPassword: CheckBox
    private lateinit var btnIniciarSession: Button
    private lateinit var btnRegister: Button
    private lateinit var tvVerificandoBd: TextView

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
        tvVerificandoBd = findViewById(R.id.tvVerificandoBd)
        handlerOlvidoPassword(tvOlvidoPassword)
        btnRegister = findViewById(R.id.btnRegister)
        goToRegister(btnRegister)

        btnIniciarSession.setOnClickListener{iniciarSession(etEmail.text.toString().trim(),etPassword.text.toString().trim())}


        val preferencias = getSharedPreferences(resources.getString(R.string.sp_credenciales),MODE_PRIVATE)
        val emailGuardado = preferencias.getString(resources.getString(R.string.usuario), "")
        val passwordGuardada = preferencias.getString(resources.getString(R.string.password), "")

        if(emailGuardado?.isNotEmpty() ?: false && passwordGuardada?.isNotEmpty() ?: false){
            iniciarSession(emailGuardado, passwordGuardada)
            notifySessionPersistance (applicationContext)
        }
    }

    private fun handlerOlvidoPassword(tvOlvidoPassword: TextView){
        tvOlvidoPassword.setOnClickListener{
            val intent = Intent(this, ForgetPassword::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun iniciarSession(email: String, password: String){
            when{
                email.isEmpty() ->{
                    etEmail.error = "El email no puede estar vacio"
                    etEmail.requestFocus()
                }
                !email.contains("@") || !email.contains(".com") ->{
                    etEmail.error = "El formato del email es erroneo"
                    etEmail.requestFocus()
                }
                password.isEmpty() ->{
                    etPassword.error = "La contraseña no puede estar vacia"
                    etPassword.requestFocus()
                }
                else ->{
                    verificandoDatosHilos(email,password)
                }
            }
    }

    private fun goToRegister(btnRegsiter: Button) {
        btnRegsiter.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun verificandoDatosHilos(email: String, password: String){

        lifecycleScope.launch {
            try{
                tvVerificandoBd.setText("Verificando datos")
                val user = withContext(Dispatchers.IO){
                    // Simulamos el delay
                    delay(1000)
                    // Obtenemos el usuario desde la bd
                    AppDatabase.getDataBase(applicationContext).usuarioDao().getUsuarioPorEmailYPassword(email,password)
                }

                if (user != null)
                {
                    val intent = Intent(this@Login, Principal::class.java)
                    startActivity(intent)
                    tvVerificandoBd.setText("Verificacion con exito!")
                }
                else
                {
                    tvVerificandoBd.text = "Usuario o contraseña incorrectos"
                }
                if (cbCheckPassword.isChecked)
                {
                    persistSession (email, password, applicationContext)
                    notifySessionPersistance (applicationContext)
                }
            } catch (e: Exception){
                tvVerificandoBd.setText("Hubo un problema interno en la bd -> ${e.message}") // Excepcion personalizada
                }
            }
    }

    class ForgetSessionReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            if (context != null) {
                forgetSession(context)
            }
        }
    }

}