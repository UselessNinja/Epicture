package com.epitech.epicture

import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.epitech.epicture.ui.favorites.FavoritesFragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var preferences: SharedPreferences? = null
    private var editor : SharedPreferences.Editor? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        /* val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            var fragmentManager: FragmentManager = supportFragmentManager
            var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            fragmentTransaction.replace(R.id.nav_host_fragment, FavoritesFragment())
            fragmentTransaction.commit()
            /* view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show() */
        } */

        preferences = applicationContext.getSharedPreferences("data", 0)
        editor = preferences?.edit()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        //Edit image, cover and name from nav_drawer
        drawerProfile(navView)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_favorites,
                R.id.nav_send/*, R.id.nav_share, R.id.nav_tools*/
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)


        // TODO Make the searchbar work
        /*val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            // https://developer.android.com/training/search/setup
            // TODO SearchActivity and gallery
        }*/

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun drawerProfile(navView : NavigationView) {
        val hView : View = navView.getHeaderView(0)
        val navText : TextView = hView.findViewById(R.id.nav_name)
        val navEmail : TextView = hView.findViewById(R.id.nav_email)
        val navImage : ImageView = hView.findViewById(R.id.imageView)

        Log.d("DEBUG", "Displaying Avatar")
        ImgurServices.getAvatar(this, {
            runOnUiThread {
                Picasso.get().load(it.data.avatar).into(navImage)
                navText.text = it.data.username
                navEmail.text = preferences?.getString("email", null)
            }
        },{})
    }
}

