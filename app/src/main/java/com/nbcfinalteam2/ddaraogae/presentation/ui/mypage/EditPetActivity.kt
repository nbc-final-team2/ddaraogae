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
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.databinding.ActivityEditPetBinding
import java.io.File

class EditPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPetBinding
    private lateinit var imageFile: File
    private lateinit var dogData: DogModel

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
                    .load(imageUri)
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

        changeDogData()
        initView()
    }

    private fun initView() = with(binding) {
        val intent = intent
        dogData = intent.getSerializableExtra("dogData") as DogModel
        //gender 적용부
        if(dogData.gender == true) rbFemale.isChecked = true
        else rbMale.isChecked = true
        Glide.with(this@EditPetActivity)
            .load(dogData.thumbnailUrl)
            .into(ivDogThumbnail)
        etName.setText(dogData.name)
        etAge.setText(dogData.age.toString())
        etBreed.setText(dogData.lineage)
        etMemo.setText(dogData.memo)


    }

    private fun changeDogData() = with(binding) {
        var gender = false
        ivDogThumbnail.setOnClickListener {
            Log.d("test_click", "click")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        rgGenderGroup.setOnCheckedChangeListener { radioGroup, id ->
            if (id == rbFemale.id) gender = true
            else gender = false
        }
        btnEditCompleted.setOnClickListener {
            if (etName.text!!.isEmpty()) Toast.makeText(
                this@EditPetActivity,
                "이름은 필수 작성 항목입니다!",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val name = etName.text.toString()
                val age = etAge.text.toString().toInt()
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                //gender 추가 필요
                val changeDog = DogModel("", name,gender, age, breed, memo, imageFile.toString())
                Log.d("testDog", "${changeDog}")
                changePet(changeDog)
            }
        }
    }

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

    private fun changePet(changeDog: DogModel) {

    }
}