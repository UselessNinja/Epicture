package com.epitech.epicture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Button
import androidx.cardview.widget.CardView
import com.epitech.epicture.ImgurServices

class LoginActivity : AppCompatActivity() {

    var preferences : SharedPreferences? = null
    var editor : SharedPreferences.Editor? = null


    //TODO Make OAuth Connextion page + find an alternative to WebView

    /***
     * Start the Authentication process for a new user or when the refresh token expires
     */
    private fun firstTime() {
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            ImgurServices.requestLogin(this)
        }
    }

    /***
     * Start the main activity and closes the login activity
     */
    private fun mainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        preferences = applicationContext.getSharedPreferences("data", 0)
        editor = preferences?.edit()

        editor?.putString("clientID", "df1dcc02eb879d4")
        // Encrypt SecretID for now not important but will be later
        editor?.putString("secretID", "deb08b2eef50b847b90e1df07aa0eaa3916617b6")

        /*
        editor?.putString("refreshToken", null)
        editor?.putString("authToken", null)
        editor?.putString("accountName", "Daalehner")
        editor?.putString("accountID", null)
        editor?.putString("email", "gael2.dorckel@epitech.eu")
        editor?.putString("avatar", "https://cdn.discordapp.com/avatars/132917155468541952/5bdce7261dfd0fd3068cc309be35cb65.webp")
        */

        editor?.commit()

        ImgurServices.verifyLogin({
            mainActivity()
        }, {
            firstTime()
        }, this)
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("DEBUG", "NewIntent")
        super.onNewIntent(intent)
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                ImgurServices.saveLogin(intent, this)
                preferences = applicationContext.getSharedPreferences("data", 0)
                Log.d("DEBUG", "Username: " + preferences?.getString("accountName", "null"))
                Log.d("DEBUG", "accountID: " + preferences?.getString("authToken", "null"))
                Log.d("DEBUG", "refreshToken: " + preferences?.getString("refreshToken", "null"))
                mainActivity()
            }
        }
    }
}
