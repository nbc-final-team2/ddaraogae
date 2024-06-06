package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {

    private val binding: ActivityAddBinding by lazy {
        ActivityAddBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.etMemo
    }
}