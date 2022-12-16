package com.example.e_commerceadmin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.e_commerceadmin.R
import com.example.e_commerceadmin.databinding.FragmentSliderBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.util.UUID


class SliderFragment : Fragment() {

    private lateinit var binding: FragmentSliderBinding
    private lateinit var dailog:Dialog
    private var imageUrl:Uri?=null

    private var launchGaleryActivity= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            imageUrl= it.data!!.data
            binding.sliderImgView.setImageURI(imageUrl)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSliderBinding.inflate(layoutInflater)

        dailog= Dialog(requireContext())
        dailog.setContentView(R.layout.progres_layout)
        dailog.setCancelable(false)

        binding.apply {
            sliderImgView.setOnClickListener{

                val intent=Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGaleryActivity.launch(intent)
            }
            btnUploadSlider.setOnClickListener {

                if (imageUrl != null){
                    uploadImage(imageUrl!!)
                }else{
                    Toast.makeText(requireContext(), "Please Select Image", Toast.LENGTH_SHORT).show()
                    
                }
            }
        }

        return binding.root
    }

    private fun uploadImage(uri: Uri) {
        dailog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage =FirebaseStorage.getInstance().reference.child("slider/$fileName")
        refStorage.putFile(uri).addOnSuccessListener {

            it.storage.downloadUrl.addOnSuccessListener { image->
                storeData(image.toString())

            }
        }
            .addOnFailureListener{

                dailog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(image: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String,Any>(
            "img" to image
        )
        db.collection("slider").document("item").set(data)
            .addOnSuccessListener {
                dailog.dismiss()
                Toast.makeText(requireContext(), "Slider Updated succesfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {

                dailog.dismiss()
                Toast.makeText(requireContext(), "Something went Wrong", Toast.LENGTH_SHORT).show()

            }

    }

}