package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nbcfinalteam2.ddaraogae.databinding.ActivitySignUpBinding

class SignUpActivity:AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}