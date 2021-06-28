package com.nicomahnic.dadm.fotolog2021.ui.login.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.nicomahnic.dadm.fotolog2021.R

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    private val SPLASH_TIME_OUT:Long = 3000 // 2 sec

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        after(SPLASH_TIME_OUT) {
            val action = SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginScreenFragment()
            view.findNavController().navigate(action)
        }
    }

    companion object Run {
        fun after(delay: Long, process: () -> Unit) {
            Handler(Looper.getMainLooper()).postDelayed({
                process()
            }, delay)
        }
    }

}