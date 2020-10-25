package com.epitech.epicture.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epitech.epicture.adapters.GalleryAdapter
import com.epitech.epicture.ImgurServices
import com.epitech.epicture.R
import com.epitech.epicture.jsonmodels.Converter
import com.epitech.epicture.jsonmodels.FilterType
import com.epitech.epicture.jsonmodels.ImgurPost
import com.epitech.epicture.ui.RecyclerViewFragment
import kotlinx.android.synthetic.main.fragment_home.*

/***
 * Class containing everything related to the Favorites
 */
class FavoritesFragment : RecyclerViewFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var root: View
    private lateinit var adapter: GalleryAdapter
    private var loading : Boolean = false
    private var images: ArrayList<ImgurPost> = ArrayList()
    private var page: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchView.visibility = SearchView.VISIBLE
        menuManager.searchItem.isVisible = true
        menuManager.filter.visibility = Spinner.VISIBLE
        menuManager.filterItem.isVisible = true
        menuManager.filter.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (images.size == 0)
                    return
                adapter.filter(FilterType.NONE)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (images.size == 0)
                    return
                when(position) {
                    0 -> adapter.filter(FilterType.NONE)
                    1 -> adapter.filter(FilterType.IMAGE)
                    2 -> adapter.filter(FilterType.VIDEO)
                }
            }
        }
        root = inflater.inflate(R.layout.fragment_favorites, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createRecyclerView()
    }

    /***
     * Creates a recyclerview inside of the fragement and initialize the refresh feature
     */
    private fun createRecyclerView() {
        adapter = GalleryAdapter(context!!, images)
        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        loadPages()
        infiniteScroll()
        menuManager.refresh.isVisible = true
        menuManager.refresh.setOnMenuItemClickListener {
            loadPages()
            return@setOnMenuItemClickListener true
        }
    }

    /***
     * removes unreadable images from the dataset
     */
    private fun pruneUnreadableImages() {
        if (images.isEmpty()) {
            recycler_view.visibility = View.GONE
            empty.visibility = View.VISIBLE
        } else {
            recycler_view.visibility = View.VISIBLE
            empty.visibility = View.GONE
        }
    }

    /***
     * loads new pages
     */
    private fun loadPages() {
        if (loading)
            return
        images.clear()
        loading = true
        for (index in 0..page)
            getFavoritesFromPage(index) {
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

    /***
     * instantiate the infinite scroll feature
     */
    private fun infiniteScroll() { recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (!recyclerView.canScrollVertically(1) && !loading) {
                val size = images.size
                loading = true
                getFavoritesFromPage(page + 1) {
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

    /***
     * populates the dataset for a specific page to be used in the recyclerview using the imgur api
     * @param page
     */
    private fun getFavoritesFromPage(page: Int, callback: () -> Unit = {}) {
        ImgurServices.getFavorites( requireContext(), { resp ->
            try {
                for(image in resp.data) {
                    images.add(Converter.jsonElementToImgurPost(image))
                    Log.d("FAVORITE", Converter.jsonElementToImgurPost(image).toString())
                }
                images.distinctBy { it.id }
            } catch (e : Exception) {
                Log.e("ERROR", "Failed to load images at page $page -> $e")
            }
            callback()
        }, {
            Log.e("ERROR", "Failed to load images at page $page -> $it")
            callback()
        }, page.toString())
    }

    override fun getSearchListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                adapter.filter.filter(searchQuery)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        }
    }
}