package com.epitech.epicture.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.epitech.epicture.MainActivity

open class RecyclerViewFragment : Fragment() {

    lateinit var searchView : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //searchView = MainActivity().getSearchView()
        //searchView.setOnQueryTextListener(getSearchListener())
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