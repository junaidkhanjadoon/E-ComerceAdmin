package com.example.e_commerceadmin.model

data class AddProductModel(
    val productName:String? = "",
    val productDescription:String = "",
    val productCoverImage:String = "",
    val productCategory:String = "",
    val productMRP:String = "",
    val productSP:String = "",
    val productId:String = "",
    val productImages:ArrayList<String>
)
