package com.example.capstoneproject4.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.model.OnBoardingPage

class OnboardingFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter
    private lateinit var indicators: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).hideToolbarAndBottomNavigation()

        viewPager = view.findViewById(R.id.onboardingViewPager)
        indicators = view.findViewById(R.id.indicator)

        val pages = listOf(
            OnBoardingPage(R.drawable.onboarding_1, "Rawat Kulitmu Setiap Hari", "Mulai hari dengan perawatan yang memberikan rasa percaya diri."),
            OnBoardingPage(R.drawable.onboarding_2, "Temukan Produk yang Tepat", "Kami merekomendasikan produk skincare yang sesuai dengan kebutuhan kulitmu."),
            OnBoardingPage(R.drawable.onboarding_3, "Kenali Kondisi Kulitmu", "Analisis kondisi kulitmu dan dapatkan solusi terbaik.")
        )

        adapter = OnboardingAdapter(pages)
        viewPager.adapter = adapter

        // Add PageChangeListener to update the indicator
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })

        // Update the indicator for the initial page
        updateIndicator(viewPager.currentItem)

        val nextButton: Button = view.findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }
        }

        // Change button text dynamically and show the button only on the last page
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                nextButton.text = if (position == adapter.itemCount - 1) "Finish" else "Next"
                nextButton.visibility = if (position == adapter.itemCount - 1) View.VISIBLE else View.GONE
            }
        })
    }

    // Function to update indicator based on the current page
    private fun updateIndicator(position: Int) {
        for (i in 0 until indicators.childCount) {
            val view = indicators.getChildAt(i)
            if (view is View) {
                view.setBackgroundResource(
                    if (i == position) R.drawable.indicator_selected else R.drawable.indicator_unselected
                )
            }
        }
    }
}

