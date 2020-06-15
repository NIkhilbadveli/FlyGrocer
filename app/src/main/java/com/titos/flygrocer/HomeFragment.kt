package com.titos.flygrocer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_home, container, false)

        val shopFragment = ShopFragment()
        val fragmentManager = activity?.supportFragmentManager!!

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1))
        imageList.add(SlideModel(R.drawable.banner2))
        imageList.add(SlideModel(R.drawable.banner3))
        imageList.add(SlideModel(R.drawable.banner4))

        val imageSlider = layoutView.findViewById<ImageSlider>(R.id.bannerSlider)
        imageSlider.setImageList(imageList)

        layoutView.findViewById<CardView>(R.id.superGrocerContainer).setOnClickListener {

                fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container,shopFragment,"ShopFragment")
                .addToBackStack(null)
                .commit()
        }

        layoutView.findViewById<CardView>(R.id.foodGrocerContainer).setOnClickListener {

            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container,shopFragment,"ShopFragment")
                .addToBackStack(null)
                .commit()
        }

        layoutView.findViewById<CardView>(R.id.greenGrocerContainer).setOnClickListener {

            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container,shopFragment,"ShopFragment")
                .addToBackStack(null)
                .commit()
        }

        return layoutView
    }


}