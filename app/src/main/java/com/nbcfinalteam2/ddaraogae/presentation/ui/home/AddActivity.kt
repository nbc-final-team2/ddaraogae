package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddActivity : AppCompatActivity() {

    private val binding: ActivityAddBinding by lazy {
        ActivityAddBinding.inflate(layoutInflater)
    }

    private val addViewModel: AddViewModel by viewModels()

    private var thumbnailUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            thumbnailUri = uri
            binding.ivDogThumbnail.setImageURI(uri)
        }
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
        setupListener()
        validationCheck()
    }

    private fun setupListener() {
        selectProfile()
        moveToBack()
    }

    private fun moveToBack() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun validationCheck() {
        binding.btnEditCompleted.setOnClickListener {
            val name = binding.etName.text.toString()
            val gender = binding.rgGenderGroup.checkedRadioButtonId

            when {
                gender == - 1 && name.isEmpty() -> {
                    Toast.makeText(this, "반려견의 이름과 성별을 선택해주세요!", Toast.LENGTH_SHORT).show()
                }
                name.isEmpty() -> {
                    Toast.makeText(this, "반려견 이름을 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
                gender == -1 -> {
                    Toast.makeText(this, "성별을 선택해 주세요!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    editDogCompleted()
                }
            }
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
                val thumbnailUrl = thumbnailUri.toString() // 여긴 프로필 클릭했을때 처리해야함

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

    private fun selectProfile() {
        binding.ivDogThumbnail.setOnClickListener {
            val mimeType = "image/*"
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))
        }
    }
}

