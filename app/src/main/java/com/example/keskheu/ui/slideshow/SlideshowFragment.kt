package com.example.keskheu.ui.slideshow

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
import com.example.keskheu.R
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import java.io.IOException

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private lateinit var accesLocal : AccesLocal


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel = ViewModelProvider(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        val textView: TextView = root.findViewById(R.id.text_slideshow)
        slideshowViewModel.lu.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val textView2: TextView = root.findViewById(R.id.Nbr_question)
        slideshowViewModel.NbrQuestion.observe(viewLifecycleOwner, Observer {
            textView2.text = it
        })

        val textView3: TextView = root.findViewById(R.id.DemandeQuestion)
        slideshowViewModel.DemandeQuestion.observe(viewLifecycleOwner, Observer {
            textView3.text = it
        })

        val DemandeUtilisateur = root.findViewById<EditText>(R.id.EntreDemandeQuestion)

        val boutton = root.findViewById<Button>(R.id.Boutton_DemandeQuestion)

        boutton.setOnClickListener {
        accesLocal = AccesLocal(getActivity()?.getApplicationContext())

        val value :String
        val finalValue:Int
        var  AAfficher:String
        try {
            value = DemandeUtilisateur.getText().toString().trim()
            finalValue = Integer.parseInt(value)
            if(finalValue<1||finalValue>accesLocal.getNumber()) AAfficher="Erreur valeur d'entrée"
            else    AAfficher = "La question numero: " + finalValue + " est " + accesLocal.rcmpNumbers(finalValue)
        }catch (ex:Exception){
            AAfficher="Erreur type d'entrée"
        }
        Snackbar.make(it, AAfficher, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                val responseTextRegister: TextView = root.findViewById(R.id.Reponse_Serveur)
                try {
                    val responseString: String = response.body().toString()
                    if (responseString == "success") {
                        responseTextRegister.text = "Registration completed successfully."
                    } else if (responseString == "username") {
                        responseTextRegister.text = "Username already exists. Please chose another username."
                    } else {
                        responseTextRegister.text = "Something went wrong. Please try again later."
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}