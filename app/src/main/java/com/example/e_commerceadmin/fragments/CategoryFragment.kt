package com.example.e_commerceadmin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.e_commerceadmin.R
import com.example.e_commerceadmin.adapters.CategoryAdapter
import com.example.e_commerceadmin.databinding.FragmentCategoryBinding
import com.example.e_commerceadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding

    private lateinit var dailog: Dialog
    private var imageUrl: Uri?=null

    private var launchGaleryActivity= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            imageUrl= it.data!!.data
            binding.categoryImgView.setImageURI(imageUrl)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCategoryBinding.inflate(layoutInflater)

        dailog= Dialog(requireContext())
        dailog.setContentView(R.layout.progres_layout)
        dailog.setCancelable(false)


        getData()

        binding.apply {
            categoryImgView.setOnClickListener{

                val intent= Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGaleryActivity.launch(intent)
            }
            btnCategory.setOnClickListener {

                validateData(binding.etCategoryName.text.toString())
            }
        }

        return binding.root
    }

    private fun getData() {
        val list=ArrayList<CategoryModel>()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener{
                list.clear()
                for (doc in it.documents){
                    val data=doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.recyclerCategory.adapter = CategoryAdapter(requireContext(), list)
            }
    }

    private fun validateData(categoryName: String) {
        if (categoryName.isEmpty()){
            Toast.makeText(requireContext(), "Please provide category Name", Toast.LENGTH_SHORT)
                .show()

        }else if (imageUrl == null){
            Toast.makeText(requireContext(), "please select An Image", Toast.LENGTH_SHORT).show()

        }else{
            uploadImage(categoryName)
        }

    }

    private fun uploadImage(categoryName: String) {
        dailog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("category/$fileName")
        refStorage.putFile(imageUrl!!).addOnSuccessListener {

            it.storage.downloadUrl.addOnSuccessListener { image->
                storeData(categoryName, imageUrl.toString())

            }
        }
            .addOnFailureListener{

                dailog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(categoryName: String, url: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String,Any>(
            "category" to categoryName,
            "img" to url
        )
        db.collection("categories").add(data)
            .addOnSuccessListener {
                dailog.dismiss()
                binding.categoryImgView.setImageDrawable(resources.getDrawable(R.drawable.preview))
                binding.etCategoryName.text=null
                getData()
                Toast.makeText(requireContext(), "Category added succesfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {

                dailog.dismiss()
                Toast.makeText(requireContext(), "Something went Wrong", Toast.LENGTH_SHORT).show()

            }


    }

}