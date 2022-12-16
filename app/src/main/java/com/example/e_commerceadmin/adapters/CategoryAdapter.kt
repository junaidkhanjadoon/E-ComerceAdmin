package com.example.e_commerceadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.e_commerceadmin.R
import com.example.e_commerceadmin.databinding.ItemCategoryLayoutBinding
import com.example.e_commerceadmin.model.CategoryModel

class CategoryAdapter(var context: Context, var list: ArrayList<CategoryModel>):
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View):RecyclerView.ViewHolder(view){

        var binding=ItemCategoryLayoutBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_layout,parent,false))

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.tvItemCategory.text = list[position].category
        Glide.with(context).load(list[position].img).into(holder.binding.imgItemCetogry)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}