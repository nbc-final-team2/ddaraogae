package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

@AndroidEntryPoint
class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var imageFile: File = File("")
    private val viewModel: AddPetViewModel by viewModels()

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                imageResult.launch(intent)
            }
        }

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                imageFile = File(getRealPathFromURI(it))
                Glide.with(this)
                    .load(it)
                    .fitCenter()
                    .into(binding.ivDogThumbnail)

            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btBack.setOnClickListener { finish() }
        addPetData()
    }

    private fun addPetData() = with(binding) {
        var gender = 0

        ivDogThumbnail.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        //성별 선택
        rgGenderGroup.setOnCheckedChangeListener { radioGroup, id ->
            if (id == rbFemale.id) gender = 1
            else gender = 0
        }
        //완료 버튼 클릭 시 데이터 추가
        btnEditCompleted.setOnClickListener {
            if (etName.text!!.isBlank()) Toast.makeText(
                this@AddActivity,
                R.string.please_add_name,
                Toast.LENGTH_SHORT
            ).show()
            else {
                val name = etName.text.toString()
                val age = etAge.text?.toString()?.toInt()
                val breed = etBreed.text?.toString() ?: null
                val memo = etMemo.text?.toString() ?: null
                val image = if(imageFile.toString().isEmpty()) null else imageFile.toString()

                val newDog = DogItemModel("", name, gender, age, breed, memo, image)
                Log.d("testDog", "${newDog}")
                addPet(newDog)
                finish()

            }
        }
    }

    private fun addPet(newDog: DogItemModel) {
        viewModel.insertDog(newDog)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path!!
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }
}