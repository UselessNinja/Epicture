package com.epitech.epicture.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.fragment.app.Fragment

open class RecyclerViewFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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