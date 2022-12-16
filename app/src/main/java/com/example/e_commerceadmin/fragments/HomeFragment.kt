package com.example.e_commerceadmin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerceadmin.R
import com.example.e_commerceadmin.activity.AllOrderActivity
import com.example.e_commerceadmin.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.btnAddCategory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }
        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productFragment)
        }
        binding.btnAddSlider.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sliderFragment)
        }
        binding.btnAddOrderDetail.setOnClickListener {
            startActivity(Intent(requireContext(), AllOrderActivity::class.java))
        }
        return binding.root
    }
}