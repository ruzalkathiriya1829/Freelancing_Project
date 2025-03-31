package com.example.fuelfix.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.fuelfix.ModalClass.TopOffersSliderData
import com.example.fuelfix.R

class TopOffersSliderAdapter(val context: Context,
                             val topOffersSliderList: ArrayList<TopOffersSliderData>) : PagerAdapter() {
    override fun getCount(): Int {
        return topOffersSliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = layoutInflater.inflate(R.layout.top_offers_item_file, container, false)

        val imageView: ImageView = view.findViewById(R.id.imgBackground)
        val txtTopOffersHeading: TextView = view.findViewById(R.id.txtHeading)

        val topOffersSliderData: TopOffersSliderData = topOffersSliderList.get(position)
        txtTopOffersHeading.text = topOffersSliderData.slideHeading
        imageView.setImageResource(topOffersSliderData.slideImage)

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as LinearLayout)
    }
}