package com.epitech.epicture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.epitech.epicture.jsonmodels.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.*
import java.io.IOException
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

object ImgurServices {

    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    private val host = "api.imgur.com"
    private val apiVersion = "3"

    private val client: OkHttpClient = OkHttpClient.Builder().build()

    // LOGIN MANAGEMENT

    /***
     * Request login data from imgur
     */
    fun requestLogin(context: Context) {
        Log.d("DEBUG", "Login is being requested")

        preferences = context.getSharedPreferences("data", 0)

        val url = "https://api.imgur.com/oauth2/authorize?client_id=" + preferences?.
            getString("clientID", null) + "&response_type=token"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(context, intent, null)
    }

    /***
     * Removes login data, acts like a disconnect
     */
    fun pruneLogin(context: Context) {
        preferences = context.getSharedPreferences("data", 0)
        editor = preferences?.edit()
        editor?.clear()?.apply()
    }

    /***
     * Saves the login data we get from the OAuth Query
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun saveLogin(intent: Intent, context: Context) {
        Log.d("DEBUG", "Login is being saved")
        preferences = context.getSharedPreferences("data", 0)
        editor = preferences?.edit()
        Log.d("DEBUG", intent.dataString)
        val queryResult = intent.dataString.split("#")[1].split("&")
        for ((index, s: String) in queryResult.withIndex()) {
            val data = s.split("=")
            when (index) {
                0 -> editor?.putString("authToken", data[1])
                3 -> editor?.putString("refreshToken", data[1])
                4 -> editor?.putString("accountName",data[1])
            }
        }
        editor?.commit()
    }

    /*** Verifies if the login data exists
     * @param resolve when it succeed
     * @param reject when it fails
     */
    fun verifyLogin(resolve: () -> Unit, reject: () -> Unit, context: Context) {
        Log.d("DEBUG", "Login is being verified")
        if (!checkPrefs(context))
            return (reject())
        refreshLogin(resolve, reject, context)
    }

    private fun checkPrefs(context: Context): Boolean {
        Log.d("DEBUG", "Checking prefs")
        preferences = context.getSharedPreferences("data", 0)
        if (preferences?.getString("accountName", "")?.isEmpty()!!)
            return false
        Log.d("DEBUG", "Username is good")
        if (preferences?.getString("authToken", "")?.isEmpty()!!)
            return false
        Log.d("DEBUG", "AuthToken is good")
        if (preferences?.getString("refreshToken", "")?.isEmpty()!!)
            return false
        Log.d("DEBUG", "RefreshToken is good")
        return true
    }

    /***
     * Refreshes the login data using the refresh token
     * @param resolve when it succeed
     * @param reject when it fails
     */
    fun refreshLogin(resolve: () -> Unit, reject: () -> Unit, context: Context) {
        Log.d("DEBUG", "Refreshing login")
        preferences = context.getSharedPreferences("data", 0)
        editor = preferences?.edit()

        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegment("oauth2")
            .addPathSegment("token")
            .build()

        val body = FormBody.Builder()
            .add("refresh_token", preferences?.getString("refreshToken", null)!!)
            .add("client_id", preferences?.getString("clientID", null)!!)
            .add("client_secret", preferences?.getString("secretID", null)!!)
            .add("grant_type", "refresh_token")
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer " + preferences?.getString("clientID", null)!!)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DEBUG", "Failure to refresh")
                return reject()
            }
            override fun onResponse(call: Call, response: Response) {
                val data = Gson().fromJson<JsonElement>(response.body!!.string(), JsonElement::class.java)
                editor?.putString("authToken", data.asJsonObject["access_token"].asString)
                editor?.putString("refreshToken", data.asJsonObject["refresh_token"].asString)
                editor?.putString("tokenExpireTime", data.asJsonObject["expires_in"].asString)
                editor?.putBoolean("authenticated", true)
                editor?.commit()
                Log.d("DEBUG", "Refresh successful")
                return resolve()
            }
        })
    }


    // GETTERS FOR API-RELATED DATA

    /***
     * Get images
     */
    fun getImages(context: Context, success: (ImgurModels<ArrayList<Image>>) -> Unit, failure: (Exception) -> Unit, page : String = "0") {
        preferences = context.getSharedPreferences("data", 0)

        if (!preferences?.getBoolean("authenticated", false)!!)
            throw IOException("You are not connected")

        Log.d("ImgurService", "getImages")
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegment(apiVersion)
            .addPathSegment("account")
            .addPathSegment(preferences?.getString("accountName", null)!!)
            .addPathSegment("images")
            .addPathSegment(page)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
            .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
            .header("User-Agent", "Epicture")
            .get()
            .build()

        val customResolve = { res: JsonElement ->
                val type = object : TypeToken<ImgurModels<ArrayList<Image>>>() {}.type
                val data = Gson().fromJson<ImgurModels<ArrayList<Image>>>(res.toString(), type)
                success(data)
        }
        asynchronousRequest(request, customResolve, failure)
    }

    /***
     * Search in the imgur api with query
     */
    fun search(context: Context, success: (ImgurModels<ArrayList<JsonElement>>) -> Unit, failure: (Exception) -> Unit,
               searchQuery: String, page: String = "0", sort: String = "time", window: String = "all") {
        preferences = context.getSharedPreferences("data", 0)

        if (!preferences?.getBoolean("authenticated", false)!!)
            throw IOException("You are not connected")

        Log.d("DEBUG", "search")
        Log.d("SEARCH", "Search: $searchQuery, Page: $page, Sort: $sort, Window: $window")
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegment(apiVersion)
            .addPathSegment("gallery")
            .addPathSegment("search")
            .addPathSegment(sort)
            .addPathSegment(window)
            .addPathSegment(page)
            .addQueryParameter("q", searchQuery)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
            .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
            .header("User-Agent", "Epicture")
            .get()
            .build()

        val customResolve = { res: JsonElement ->
            val type = object : TypeToken<ImgurModels<ArrayList<JsonElement>>>() {}.type
            val data = Gson().fromJson<ImgurModels<ArrayList<JsonElement>>>(res.toString(), type)
            success(data)
        }
        asynchronousRequest(request, customResolve, failure)
    }

    /***
     * Get favorites
     */
    fun getFavorites(context: Context, success: (ImgurModels<ArrayList<JsonElement>>) -> Unit, failure: (Exception) -> Unit, page: String) {
        preferences = context.getSharedPreferences("data", 0)

        if (!preferences?.getBoolean("authenticated", false)!!)
            throw IOException("You are not connected")

        Log.d("DEBUG", "getFavorites")

        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegment(apiVersion)
            .addPathSegment("account")
            .addPathSegment(preferences?.getString("accountName", null)!!)
            .addPathSegment("favorites")
            .addPathSegment(page)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
            .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
            .header("User-Agent", "Epicture")
            .get()
            .build()

        val customResolve = { res: JsonElement ->
            val type = object : TypeToken<ImgurModels<ArrayList<JsonElement>>>() {}.type
            val data = Gson().fromJson<ImgurModels<ArrayList<JsonElement>>>(res.toString(), type)
            success(data)
        }
        asynchronousRequest(request, customResolve, failure)
    }

    /***
     * Get the logged user avatar (authenticated)
     * @param success returns a model containing the Avatar
     * @param failure returns a java error
     */
    fun getAvatar(context: Context, success: (ImgurModels<Avatar>) -> Unit, failure: (Exception) -> Unit) {
        preferences = context.getSharedPreferences("data", 0)

        if (!preferences?.getBoolean("authenticated", false)!!)
            throw IOException("You are not connected")

        Log.d("DEBUG", "getAvatar")
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegment(apiVersion)
            .addPathSegment("account")
            .addPathSegment(preferences?.getString("accountName", null)!!)
            .addPathSegment("avatar")
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
            .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
            .header("User-Agent", "Epicture")
            .get()
            .build()

        val customResolve = { res: JsonElement ->
            try {
                val type = object : TypeToken<ImgurModels<Avatar>>() {}.type
                val data = Gson().fromJson<ImgurModels<Avatar>>(res.toString(), type)
                val model = Avatar(data.data.avatar, preferences?.getString("accountName", null)!!)
                success(ImgurModels(model, data.success, data.status))
            } catch (e: java.lang.Exception) {
                failure(e)
            }
        }
        asynchronousRequest(request, customResolve, failure)
    }

    /***
     * Start a OkHttp3 request asynchronously
     * @success returns the JsonElement containing the data
     * @failure returns an java exception
     */
    private fun asynchronousRequest(request: Request, success: (JsonElement) -> Unit, failure: (Exception) -> Unit) {
        Log.d("DEBUG", "Asynchronous Request")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val data = Gson().fromJson<JsonElement>(
                        response.body!!.string(),
                        JsonElement::class.java
                    )
                    val dataModel = data.asJsonObject
                    if (dataModel.get("success").asBoolean) {
                        Log.d("DEBUG", "Asynchronous Request Success")
                        return success(data)
                    }
                    Log.d("DEBUG", "Asynchronous Invalid response : $response")
                    return failure(java.lang.Exception("Invalid response : $response"))
                } catch (e: Exception) {
                    Log.d("DEBUG", e.toString())
                    return failure(e)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DEBUG", "Asynchronous Request Failed")
                return failure(e)
            }
        })
    }

    fun changeFavoriteState(context: Context, success: (JsonElement) -> Unit, failure: (Exception) -> Unit, id: String, type: Type) {
        preferences = context.getSharedPreferences("data", 0)

        if (!preferences?.getBoolean("authenticated", false)!!)
            throw IOException("You are not connected")

        var request: Request

        if (type == Type.ImagePost || type == Type.Image) {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment(apiVersion)
                .addPathSegment("image")
                .addPathSegment(id)
                .addPathSegment("favorite")
                .build()

            request = Request.Builder()
                .url(url)
                .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
                .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
                .header("User-Agent", "Epicture")
                .post("".toRequestBody(null))
                .build()

        } else {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment(apiVersion)
                .addPathSegment("album")
                .addPathSegment(id)
                .addPathSegment("favorite")
                .build()

            request = Request.Builder()
                .url(url)
                .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
                .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
                .header("User-Agent", "Epicture")
                .post("".toRequestBody(null))
                .build()
        }
        asynchronousRequest(request, success, failure)
    }

    fun upload(context: Context, success: (JsonElement) -> Unit, failure: (Exception) -> Unit, name: String, title: String, description: String, image: Bitmap) {
        preferences = context.getSharedPreferences("data", 0)

        if (!preferences?.getBoolean("authenticated", false)!!)
            throw IOException("You are not connected")

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)

        Thread(Runnable {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment(apiVersion)
                .addPathSegment("image")
                .build()

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", baos.toByteArray().toRequestBody("image/*jpg".toMediaTypeOrNull(), 0))
                .addFormDataPart("title", title)
                .addFormDataPart("description", description)
                .addFormDataPart("name", name)
                .build()

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Client-ID " + preferences?.getString("clientID", null))
                .header("Authorization", "Bearer " + preferences?.getString("authToken", null))
                .header("User-Agent", "Epicture")
                .post(body)
                .build()
            
            asynchronousRequest(request, success, failure)
        }).start()
    }
}