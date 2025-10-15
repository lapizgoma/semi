package com.example.tp_seminarios

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Principal : AppCompatActivity() {

    lateinit var toolbar : Toolbar

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal,menu)

        // NOTA (Juli): por alguna razón,
        // se necesita hacer esto para que los íconos se muestren si los Items están dentro del menú.
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lateinit var intent : Intent
        return when (item.itemId){
            R.id.lista_pokemones -> {
                intent = Intent(this, ListaPokemones::class.java)
                startActivity(intent)
                true
            }
            R.id.cerrar_session -> {
                Login.forgetSession (applicationContext);
                intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> return super.onOptionsItemSelected(item)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dismiss = intent.getBooleanExtra("dismiss", false)
        if (dismiss) {
            Login.clearSessionPersistenceNotification(this)
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.barra_principal)

        createChannel ("Session", "Session persistence status.")
    }



    private fun createChannel(
        channelName: String,
        channelDescription: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = getString(R.string.session_persistence_notif_name)
            // val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelName, name, importance)
            // mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}