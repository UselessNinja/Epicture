package com.epitech.epicture

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.epitech.epicture.ui.favorites.FavoritesFragment
import com.epitech.epicture.ui.gallery.GalleryFragment
import com.epitech.epicture.ui.home.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var preferences: SharedPreferences? = null
    private var editor : SharedPreferences.Editor? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var searchView: SearchView
    private lateinit var _menu: Menu

    inner class MenuManager(actionMenu: Menu, val supportActionBar: androidx.appcompat.app.ActionBar?) {

        val search: SearchView = actionMenu.findItem(R.id.app_bar_search).actionView as SearchView

        init {
            if (!searchView.isIconified) {
                searchView.isIconified = true
            }
        }
    }

    fun getSearchView() : MenuManager {
        Log.d("sv", "menu get")
        return MenuManager(_menu, supportActionBar)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = applicationContext.getSharedPreferences("data", 0)
        editor = preferences?.edit()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

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

        drawerLayout = findViewById(R.id.drawer_layout)

        //Edit image, cover and name from nav_drawer
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        drawerProfile(navView)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger_24dp)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        _menu = menu

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val t = supportFragmentManager.beginTransaction()
        t.replace(R.id.nav_host_fragment, HomeFragment())
        t.commit()
        super.onPrepareOptionsMenu(menu)
        _menu = menu!!
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        actionBarDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                val t = supportFragmentManager.beginTransaction()
                t.replace(R.id.nav_host_fragment, HomeFragment())
                t.commit()
            }
            R.id.nav_gallery -> {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                val t = supportFragmentManager.beginTransaction()
                t.replace(R.id.nav_host_fragment, GalleryFragment())
                t.commit()
            }
            R.id.nav_favorites -> {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                val t = supportFragmentManager.beginTransaction()
                t.replace(R.id.nav_host_fragment, FavoritesFragment())
                t.commit()
            }
            R.id.nav_send_gallery -> {
                Upload.getImageFromGallery(this)
            }
            R.id.nav_send_camera -> {
                Upload.getImageFromCamera(this)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_disconnect -> {
                ImgurServices.pruneLogin(this)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Upload.onActivityResult(this, requestCode, resultCode, data)
    }

    /***
     * Documentation pour quelqu'un qui doit reprendre le projet
     */
}

