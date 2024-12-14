package com.example.capstoneproject4.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject4.MainActivity
import com.example.capstoneproject4.R
import com.example.capstoneproject4.data.local.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).hideToolbarAndBottomNavigation()

        dataStoreManager = DataStoreManager(requireContext())

        val logoImageView: ImageView = view.findViewById(R.id.logoImageView)
        val finalImageView: ImageView = view.findViewById(R.id.finalImageView)

        // Langkah 1: Slide up animasi untuk logo awal
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        logoImageView.startAnimation(slideUp)

        slideUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Langkah 2: Rotasi dan scale up
                val rotateScaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_scale_up)
                logoImageView.startAnimation(rotateScaleUp)

                rotateScaleUp.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        // Langkah 3: Scale down
                        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
                        logoImageView.startAnimation(scaleDown)

                        scaleDown.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {
                                // Langkah 4: Tampilkan gambar akhir dengan fade-in
                                logoImageView.visibility = View.GONE
                                finalImageView.visibility = View.VISIBLE

                                val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                                finalImageView.startAnimation(fadeIn)

                                fadeIn.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation) {}

                                    override fun onAnimationEnd(animation: Animation) {
                                        // Navigasi setelah delay dan pengecekan Remember Me
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            lifecycleScope.launch {
                                                val isRemembered = dataStoreManager.isRememberMeEnabled().first()
                                                val isLoggedIn = dataStoreManager.isLoggedIn().first()

                                                if (isRemembered && isLoggedIn) {
                                                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                                                } else {
                                                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                                                }
                                            }
                                        }, 1000)
                                    }

                                    override fun onAnimationRepeat(animation: Animation) {}
                                })
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}