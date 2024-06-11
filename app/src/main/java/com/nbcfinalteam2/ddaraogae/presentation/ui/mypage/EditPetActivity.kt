package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

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
import com.nbcfinalteam2.ddaraogae.databinding.ActivityEditPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPetBinding
    private val viewModel: EditPetViewModel by viewModels()
    private var imageFile: File = File("")
    private lateinit var dogData: DogItemModel
    private lateinit var dogId : String

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
        binding = ActivityEditPetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btBack.setOnClickListener { finish() }
        changeDogData()
        initView()
    }

    private fun initView() = with(binding) {
        val intent = intent
        dogData = intent.getParcelableExtra("dogData")!!
        if(dogData.gender == 1) rbFemale.isChecked = true
        else rbMale.isChecked = true
        Glide.with(this@EditPetActivity)
            .load(dogData.thumbnailUrl)
            .into(ivDogThumbnail)
        etName.setText(dogData.name)
        etAge.setText(dogData.age.toString())
        etBreed.setText(dogData.lineage)
        etMemo.setText(dogData.memo)
        imageFile = File(dogData.thumbnailUrl)
        dogId = dogData.id
    }

    //강아지 정보 수정
    private fun changeDogData() = with(binding) {
        var gender = 0
        ivDogThumbnail.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        rgGenderGroup.setOnCheckedChangeListener { radioGroup, id ->
            if (id == rbFemale.id) gender = 1
            else gender = 0
        }
        btnEditCompleted.setOnClickListener {
            if (etName.text!!.isEmpty()) Toast.makeText(
                this@EditPetActivity,
                R.string.please_add_name,
                Toast.LENGTH_SHORT
            ).show()
            else {
                val name = etName.text.toString()
                val age = etAge.text.toString().toInt()
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                val image = if(imageFile.toString().isEmpty()) null else imageFile.toString()
                val changeDog = DogItemModel(dogId, name,gender, age, breed, memo, image)
                changePet(changeDog)
                finish()
            }
        }
    }

    //uri -> file 형태로 변환
    fun getRealPathFromURI(uri: Uri): String {
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

    //강아지 정보 수정 함수
    private fun changePet(changeDogData: DogItemModel) {
        viewModel.upDateDog(changeDogData)
    }
}