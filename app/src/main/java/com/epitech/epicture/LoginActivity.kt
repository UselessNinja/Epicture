package com.epitech.epicture

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {

    var preferences : SharedPreferences? = null
    var editor : SharedPreferences.Editor? = null

    //TODO Make OAuth Connextion page + find an alternative to WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // How to store data for login and app in general

        preferences = applicationContext.getSharedPreferences("data", 0)
        editor = preferences?.edit()

        editor?.putString("clientID", "df1dcc02eb879d4")
        // Encrypt SecretID for now not important but will be later
        editor?.putString("secretID", "deb08b2eef50b847b90e1df07aa0eaa3916617b6")
        editor?.putString("refreshToken", null)
        editor?.putString("authToken", null)

        editor?.putString("username", "Daalehner")
        editor?.putString("email", "gael2.dorckel@epitech.eu")
        editor?.putString("avatar", "https://cdn.discordapp.com/avatars/132917155468541952/5bdce7261dfd0fd3068cc309be35cb65.webp")

        editor?.commit()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
