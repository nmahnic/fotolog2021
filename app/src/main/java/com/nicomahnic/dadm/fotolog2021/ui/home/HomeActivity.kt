package com.nicomahnic.dadm.fotolog2021.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.ui.login.MainActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


class HomeActivity:  AppCompatActivity() {

    private lateinit var navController: NavController
    private val firebaseAuth by lazy { FirebaseAuth.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.navHostFragment)
        navView.setupWithNavController(navController)
        navView.getHeaderView(0).btn_logout.setOnClickListener {
            AuthUI.getInstance().signOut(this).addOnSuccessListener {
                cleanPref()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        firebaseAuth.currentUser?.let { result ->
            setUserInfo(result.displayName!!, result.photoUrl.toString())
            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        }
    }

    private fun cleanPref(){
        val prefs = baseContext.getSharedPreferences(
            getString(R.string.pref_file),
            Context.MODE_PRIVATE
        ).edit()
        prefs.clear()
        prefs.apply()
    }

    private fun setUserInfo(username: String, uri: String){
        navView.getHeaderView(0).txt_nav_header.text = username
        Glide.with(this) //1
            .load(uri)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .skipMemoryCache(true) //2
            .diskCacheStrategy(DiskCacheStrategy.NONE) //3
            .transform(CircleCrop()) //4
            .into(navView.getHeaderView(0).img_nav_header)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}