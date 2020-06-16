package com.titos.flygrocer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_home, container, false)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1))
        imageList.add(SlideModel(R.drawable.banner2))
        imageList.add(SlideModel(R.drawable.banner3))
        imageList.add(SlideModel(R.drawable.banner4))

        val imageSlider = layoutView.findViewById<ImageSlider>(R.id.bannerSlider)
        imageSlider.setImageList(imageList)

        layoutView.findViewById<CardView>(R.id.superGrocerContainer).setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        layoutView.findViewById<CardView>(R.id.foodGrocerContainer).setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        layoutView.findViewById<CardView>(R.id.greenGrocerContainer).setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        return layoutView
    }


}