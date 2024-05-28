package com.avinash.sociopulse.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avinash.sociopulse.R
import com.avinash.sociopulse.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        * As soon as user started, taking him/her to the login fragment
        * */
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragmentContainer, LoginFragment())
            .commit()
    }
}