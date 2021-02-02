package com.example.keskheu.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.keskheu.AccesLocal
import com.example.keskheu.Question
import com.example.keskheu.R
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var accesLocal : AccesLocal
    private lateinit var question : Question
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProvider(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        val username = root.findViewById<EditText>(R.id.Question_utilisateur)
        val boutton = root.findViewById<Button>(R.id.button_entree_utlisateur)

        boutton.setOnClickListener {
            view -> Snackbar.make(view, "Votre question est: " + username.getText().toString(), Snackbar.LENGTH_LONG).setAction("Action", null).show()
            Thread.sleep(3_000)
            val QuestionUtilisation : Question = Question(username.getText().toString(),1)

            val registrationForm1 : JSONObject =  JSONObject()
            try {
                registrationForm1.put("subject", "ecrire");
                registrationForm1.put("Question", username.getText().toString());
            } catch (e: JSONException) {
                e.printStackTrace();
            }

            val body:RequestBody  = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm1.toString());

            postRequest(root,"http://ns328061.ip-37-187-112.eu:5000", body);

        }
        return root
    }
    fun postRequest(root: View, postUrl: String?, postBody: RequestBody?) {
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
                try {
                    val responseString: String? = response.body()?.string()
                    Thread.sleep(3_000)

                    when (responseString) {
                        "success" -> {
                            view?.let {Snackbar.make(it,"yes|"+responseString , Snackbar.LENGTH_LONG).setAction("Action",null).show()}
                        }
                        "failure" -> {
                            view?.let {Snackbar.make(it,"fail|"+responseString , Snackbar.LENGTH_LONG).setAction("Action",null).show()}
                        }
                        else -> {
                            view?.let {Snackbar.make(it,"other|"+responseString , Snackbar.LENGTH_LONG).setAction("Action",null).show()}
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}