package com.epitech.epicture.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epitech.epicture.ImgurServices
import com.epitech.epicture.R
import com.epitech.epicture.adapters.SearchAdapter
import com.epitech.epicture.jsonmodels.Converter
import com.epitech.epicture.jsonmodels.ImgurPost
import com.epitech.epicture.ui.RecyclerViewFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : RecyclerViewFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var root: View
    private lateinit var adapter: SearchAdapter
    private var loading : Boolean = false
    private var images: ArrayList<ImgurPost> = ArrayList()
    private var page: Int = 0
    private var query: String = "cats"

    private var searching: Boolean = false
        set(value) {
            val old = field
            field = value
            if (value != old) {
                page = 0
                images.clear()
                loadPages()
                searching = false
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchView.visibility = SearchView.VISIBLE
        menuManager.searchItem.isVisible = true
        menuManager.filter.visibility = Spinner.INVISIBLE
        menuManager.filterItem.isVisible = false
        root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createRecyclerView()
    }

    private fun createRecyclerView () {
        adapter = SearchAdapter(context!!, images)
        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        loadPages()
        infiniteScroll()
        menuManager.refresh.isVisible = true
        menuManager.refresh.setOnMenuItemClickListener {
            if (query != "cats")
                loadPages()
            return@setOnMenuItemClickListener true
        }
    }

    private fun pruneUnreadableImages() {
        if (images.isEmpty()) {
            recycler_view.visibility = View.GONE
            empty.visibility = View.VISIBLE
        } else {
            recycler_view.visibility = View.VISIBLE
            empty.visibility = View.GONE
        }
    }

    private fun loadPages() {
        if (loading)
            return
        images.clear()
        loading = true
        for (index in 0..page)
            getImagesFromPage(index, query) {
                if(index == page) {
                    activity?.runOnUiThread {
                        Log.d("DEBUG", "DataSetChanged")
                        pruneUnreadableImages()
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                    loading = false
                }
            }
    }

    private fun infiniteScroll() { recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1) && !loading) {
                    val size = images.size
                    loading = true
                    getImagesFromPage(page + 1, query) {
                        Log.d("INFSCROLL", images.toString())
                        Log.d("INFSCROLL", "$size > " + images.size.toString())
                        if (images.size > size) {
                            page += 1
                        }
                        loading = false
                        activity?.runOnUiThread {
                            Log.d("DEBUG", "DataSetChanged")
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun getImagesFromPage(page: Int, searchQuery : String = "cats", callback: () -> Unit = {}) {
        ImgurServices.search( requireContext(), { resp ->
            try {
                for(image in resp.data) {
                    images.add(Converter.jsonElementToImgurPost(image))
                    Log.d("IMAGE", Converter.jsonElementToImgurPost(image).toString())
                }
                images.distinctBy { it.id }
            } catch (e : Exception) {
                Log.e("ERROR", "Failed to load images at page $page -> $e")
            }
                callback()
            }, {
                Log.e("ERROR", "Failed to load images at page $page -> $it")
                callback()
            }, searchQuery, page.toString(), "viral")
    }

    override fun getSearchListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                Log.d("SEARCHVIEW", searchQuery)
                query = searchQuery!!
                searching = true
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
            
        }
    }


}