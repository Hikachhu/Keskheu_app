package com.example.keskheu.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keskheu.AccesLocal
import com.example.keskheu.Question
import com.example.keskheu.R
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var accesLocal : AccesLocal
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private lateinit var linearLayoutManager: LinearLayoutManager


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        accesLocal = AccesLocal(activity?.applicationContext)
        val ButtonRefresh: Button= root.findViewById(R.id.Refresh_list)
        val ButtonNumber: Button= root.findViewById(R.id.BoutonNombre)
        val textView: TextView = root.findViewById(R.id.text_home)
        val ListeQuestion:RecyclerView = root.findViewById(R.id.NouvelleRecyclerView)
        if(accesLocal.number==0) {accesLocal.ajout(Question("Qu'est ce que cette app ?", 1))}
        homeViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })

        ListeQuestion.layoutManager = LinearLayoutManager(activity)
        ListeQuestion.setHasFixedSize(true)
        mAdapter =  CustomAdapter(BddToArray());
        ListeQuestion.adapter = mAdapter;

        ButtonRefresh.setOnClickListener { view ->Snackbar.make(view, "Lecture base de donnée, wait", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            accesLocal= AccesLocal(activity?.applicationContext)

            val registrationForm1 : JSONObject =  JSONObject()
            try {registrationForm1.put("subject", "lire_tous");}
            catch (e: JSONException) {e.printStackTrace();}

            val body: RequestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    registrationForm1.toString()
            );
            RequestSynchro(root, "http://ns328061.ip-37-187-112.eu:5000", body);     //<-Refresh my local database

        }

        ButtonNumber.setOnClickListener {
            val registrationForm1 : JSONObject =  JSONObject()
            try {registrationForm1.put("subject", "nombre");}
            catch (e: JSONException) {e.printStackTrace();}

            val body: RequestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    registrationForm1.toString()
            );

            view?.let { it1 -> Snackbar.make(it1, "Post creation body, wait", Snackbar.LENGTH_LONG).setAction(
                    "Action",
                    null
            ).show() }
            RequestNumber(root, "http://ns328061.ip-37-187-112.eu:5000", body);
        }
        return root
    }

    fun RequestSynchro(root: View, postUrl: String, postBody: RequestBody) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(postUrl)
            .post(postBody)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                Log.d("FAIL", e.message.toString())
                val responseText: TextView = root.findViewById(R.id.Reponse_Serveur)
                responseText.text = "Failed to Connect to Server. Please Try Again."
            }

            override fun onResponse(call: Call, response: Response) {
                var str_response = response.body()!!.string()
                val jsonstr: JSONObject = JSONObject(str_response)
                var ToutsLesEntree: JSONArray = jsonstr.getJSONArray("resultat")
                accesLocal = AccesLocal(getActivity()?.getApplicationContext())
                try {
                    for (i in 0 until ToutsLesEntree.length()) {
                        val Actual = ToutsLesEntree.getJSONArray(i)
                        val number: Int = Actual.getInt(0)
                        val question: String = Actual.getString(1)

                        if (accesLocal.number < number) {
                            accesLocal.ajout(Question(question, accesLocal.number + 1))
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun RequestNumber(root: View, postUrl: String, postBody: RequestBody) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(postUrl)
            .post(postBody)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                Log.d("FAIL", e.message.toString())
                val responseText: TextView = root.findViewById(R.id.Reponse_Serveur)
                responseText.text = "Failed to Connect to Server. Please Try Again."
            }

            override fun onResponse(call: Call, response: Response) {
                var str_response = response.body()!!.string()
                val jsonstr: JSONObject = JSONObject(str_response)
                var ToutesLesEntree: JSONArray = jsonstr.getJSONArray("nombre")
                Thread.sleep(3_000)
                try {
                    val Actuel = ToutesLesEntree.getJSONArray(0)
                    val numero: Int = Actuel.getInt(0)
                    view?.let {
                        Snackbar.make(it, "Nombre d'entrée=" + numero, Snackbar.LENGTH_LONG)
                                .setAction(
                                        "Action",
                                        null
                                ).show()
                    }
                    Thread.sleep(3_000)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    class CustomAdapter(private val dataSet: Array<String>) :
            RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView

            init {
                // Define click listener for the ViewHolder's View.
                textView = view.findViewById(R.id.textView)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.fragment_home, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.textView.text = dataSet[position]
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }

    fun BddToArray(): Array<String> {
        accesLocal = AccesLocal(getActivity()?.getApplicationContext())
        var tableau : Array<String> = arrayOf<String>()
        for(i in 1..accesLocal.number){
            tableau+=("_______")
        }
        return tableau
    }
}