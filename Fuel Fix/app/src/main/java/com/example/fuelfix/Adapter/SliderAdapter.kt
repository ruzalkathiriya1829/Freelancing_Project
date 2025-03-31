package com.example.fuelfix.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.fuelfix.R
import com.example.fuelfix.ModalClass.SliderData

class SliderAdapter(val context: Context,
                    val sliderList: ArrayList<SliderData>): PagerAdapter()
{
    override fun getCount(): Int {
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = layoutInflater.inflate(R.layout.welcome_slider_item, container, false)

        val imageView: ImageView = view.findViewById(R.id.idIVSlider)
        val sliderDescTV: TextView = view.findViewById(R.id.idTVSliderDescription)
        val sliderDescTV2: TextView = view.findViewById(R.id.idTVSliderDescription2)

        val sliderData: SliderData = sliderList.get(position)
        sliderDescTV.text = sliderData.slideDescription
        sliderDescTV2.text = sliderData.slideDescription2
        imageView.setImageResource(sliderData.slideImage)

        // on below line we are adding our view to container.
        container.addView(view)

        // on below line we are returning our view.
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // this is a destroy view method
        // which is use to remove a view.
        // this is a destroy view method
        // which is use to remove a view.
        container.removeView(`object` as LinearLayout)
    }
}