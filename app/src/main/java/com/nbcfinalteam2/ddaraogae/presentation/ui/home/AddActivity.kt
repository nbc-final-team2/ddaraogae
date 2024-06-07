package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

class AddActivity : AppCompatActivity() {

    private val binding: ActivityAddBinding by lazy {
        ActivityAddBinding.inflate(layoutInflater)
    }

    private val addViewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupListener()
        editDogCompleted()
    }

    private fun setupListener() {
        moveToBack()
    }

    private fun moveToBack() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun editDogCompleted() {
        binding.btnEditCompleted.setOnClickListener {
            with(binding) {
                val name = etName.text.toString()
                val gender = if (rbMale.isChecked) 0 else 1
                val age = etAge.text.toString().toIntOrNull()
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                val thumbnailUrl = "" // 여긴 프로필 클릭했을때 처리해야함

                val dogData = DogEntity(
                    id = "",
                    name = name,
                    gender = gender,
                    age = age,
                    lineage = breed,
                    memo = memo,
                    thumbnailUrl = thumbnailUrl
                )

                addViewModel.addDog(dogData)
                finish()
            }
        }
    }
}

