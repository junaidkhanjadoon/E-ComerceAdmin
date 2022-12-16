package com.example.e_commerceadmin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.e_commerceadmin.R
import com.example.e_commerceadmin.adapters.AddProductImageAdapter
import com.example.e_commerceadmin.databinding.FragmentAddProductBinding
import com.example.e_commerceadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding

    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddProductImageAdapter
    private var coverImage:Uri?= null
    private lateinit var dailog:Dialog
    private var coverImageURl:String?=null
    private lateinit var categoryList:ArrayList<String>

    private var launchGaleryActivity= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            coverImage= it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility=VISIBLE
        }
    }

    private var launchProductActivity= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            val imageUrl= it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentAddProductBinding.inflate(layoutInflater)

        list = ArrayList()
        listImages = ArrayList()
        dailog= Dialog(requireContext())
        dailog.setContentView(R.layout.progres_layout)
        dailog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener {
            val intent= Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)

        }
        binding.productImgBtn.setOnClickListener {
            val intent= Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGaleryActivity.launch(intent)

        }


        setProductCategory()

        adapter= AddProductImageAdapter(list)
        binding.productImgRecyclerView.adapter=adapter


        binding.productSubmitBtn.setOnClickListener {
            validateData()
        }
        return binding.root
    }

    private fun validateData() {
        if (binding.etProductNameAddProduct.text.toString().isEmpty()){
            binding.etProductNameAddProduct.requestFocus()
            binding.etProductNameAddProduct.error="please enter product Name"
        }
       else if (binding.etProductDescriptionAddProduct.text.toString().isEmpty()){
            binding.etProductDescriptionAddProduct.requestFocus()
            binding.etProductDescriptionAddProduct.error="please enter product Detail"
        }
        else if (binding.etProductSPAddProduct.text.toString().isEmpty()){
            binding.etProductSPAddProduct.requestFocus()
            binding.etProductSPAddProduct.error="please enter product Sales Price"
        }
        else if (binding.etProductMRPAddProduct.text.toString().isEmpty()){
            binding.etProductMRPAddProduct.requestFocus()
            binding.etProductMRPAddProduct.error="please enter product Market Retail Price"
        }else if (coverImage == null){
            Toast.makeText(requireContext(), "Please Select Cover Image", Toast.LENGTH_SHORT).show()
        }else if (listImages.size < 1){
            Toast.makeText(requireContext(), "Please Select Product Image", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage()
        }
    }

    private fun uploadImage(categoryName: String) {
            dailog.show()

            val fileName = UUID.randomUUID().toString()+".jpg"

            val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
            refStorage.putFile(coverImage!!).addOnSuccessListener {

                it.storage.downloadUrl.addOnSuccessListener { image->
                  coverImageURl=image.toString()
                    uploadProductImage()

                }
            }
                .addOnFailureListener{

                    dailog.dismiss()
                    Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()
                }
        }

    private var i = 0
    private fun uploadProductImage() {
            dailog.show()

            val fileName = UUID.randomUUID().toString()+".jpg"

            val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
            refStorage.putFile(list[i]).addOnSuccessListener {

                it.storage.downloadUrl.addOnSuccessListener { image->
                    coverImageURl=image.toString()
                    uploadProductImage()

                }
            }
                .addOnFailureListener{

                    dailog.dismiss()
                    Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()
                }
        }

    private fun setProductCategory(){
        categoryList= ArrayList()
        Firebase.firestore.collection("categories").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it.documents){
                val data=doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.category!!)

            }
            categoryList.add(0,"Select Category")

            val arrayAdapter=ArrayAdapter(requireContext(),R.layout.dropdown_item_layout,categoryList)
            binding.spinnerProductDropdown.adapter=arrayAdapter
        }
    }

}