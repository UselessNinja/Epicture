package com.epitech.epicture.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.epitech.epicture.MainActivity

/***
 * Generic class for the Fragments with the recycler views
 */
open class RecyclerViewFragment : Fragment() {

    lateinit var searchView : SearchView
    lateinit var menuManager : MainActivity.MenuManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = (activity as MainActivity)
        menuManager = activity.getMenuManager()
        searchView = menuManager.search
        searchView.setOnQueryTextListener(getSearchListener())
    }

    open fun getSearchListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO()
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                TODO()
            }

        }
    }

}