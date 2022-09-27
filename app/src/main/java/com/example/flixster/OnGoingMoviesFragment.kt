package com.example.flixster

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import org.json.JSONObject


private const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"

class OnGoingMoviesFragment : Fragment(), FragmentInteractionListener { // OnGoingMoviesFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ongoing_movies_list, container, false)
        val progressBar = view.findViewById<View>(R.id.progress) as ContentLoadingProgressBar
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        val context = view.context
        recyclerView.layoutManager = LinearLayoutManager(context)
        updateAdapter(progressBar, recyclerView)
        return view
    }

    /*
    * Updates the RecyclerView adapter with new data.  This is where the
    * networking magic happens!
    */
    private fun updateAdapter(progressBar: ContentLoadingProgressBar, recyclerView: RecyclerView) {
        progressBar.show()

        // Create and set up an AsyncHTTPClient() here
        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api-key"] = API_KEY

        client[
                "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json",
                params,
                object: JsonHttpResponseHandler() {
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String,
                        throwable: Throwable?
                    ) {
                        progressBar.hide()
                        throwable?.message?.let {
                            Log.e("BestSellerBooksFragment", response)
                        }
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        //Log.d("BestSellerBooksFragment", json.toString())

                        val gson = Gson()

                        val resultsJSON : JSONObject = json.jsonObject.get("results") as JSONObject
                        val booksRawJSON : String = resultsJSON.get("books").toString()

                        val arrayBookType = object : TypeToken<List<OnGoingMovie>>() {}.type
                        val movies : List<OnGoingMovie> = gson.fromJson(booksRawJSON, arrayBookType)

                        recyclerView.adapter = OnGoingMoviesAdapter(movies, this@OnGoingMoviesFragment)

                        progressBar.hide()
                        Log.d("BestSellerBooksFragment", booksRawJSON)
                    }

                }
        ]

        // Using the client, perform the HTTP request

        /* Uncomment me once you complete the above sections!
        {
            /*
             * The onSuccess function gets called when
             * HTTP response status is "200 OK"
             */
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                // The wait for a response is over
                progressBar.hide()
                //TODO - Parse JSON into Models
                val models : List<BestSellerBook> = null // Fix me!
                recyclerView.adapter = BestSellerBooksRecyclerViewAdapter(models, this@BestSellerBooksFragment)
                // Look for this in Logcat:
                Log.d("BestSellerBooksFragment", "response successful")
            }
            /*
             * The onFailure function gets called when
             * HTTP response status is "4XX" (eg. 401, 403, 404)
             */
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                t: Throwable?
            ) {
                // The wait for a response is over
                progressBar.hide()
                // If the error is not null, log it!
                t?.message?.let {
                    Log.e("BestSellerBooksFragment", errorResponse)
                }
            }
        }]
        */

    }

    override fun onItemClick(item: OnGoingMovie) {
        Toast.makeText(context,"Test: " + item.title, Toast.LENGTH_SHORT).show()
    }


}