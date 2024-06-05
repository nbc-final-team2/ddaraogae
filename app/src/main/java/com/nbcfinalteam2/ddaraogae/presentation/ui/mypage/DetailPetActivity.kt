package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nbcfinalteam2.ddaraogae.databinding.ActivityDetailPetBinding

class DetailPetActivity:AppCompatActivity() {
    lateinit var binding:ActivityDetailPetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}