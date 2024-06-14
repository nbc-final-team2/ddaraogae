package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.uriToByteArray
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    //private var imageFile: File = File("")
    private var imageUri: Uri? = null
    private var byteImage: ByteArray? = null
    private val viewModel: AddPetViewModel by viewModels()

    private val galleryPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                imageResult.launch(intent)
            }
        }

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

//                imageFile = File(getRealPathFromURI(it))
                Glide.with(this)
                    .load(uri)
                    .fitCenter()
                    .into(binding.ivDogThumbnail)

                byteImage = uriToByteArray(uri, this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener { finish() }
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
        rgGenderGroup.setOnCheckedChangeListener { _, id ->
            gender = if (id == rbFemale.id) 1 else 0
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
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                val age =
                    if (etAge.text.toString().isEmpty()) null else etAge.text.toString().toInt()
                //val image = imageFile.toString().ifEmpty { null }
                val image = ""

                val newDog = DogItemModel("", name, gender, age, breed, memo, image)

                addPet(newDog, byteImage)
                finish()
            }
        }
    }

    //강아지 추가 함수
    private fun addPet(newDog: DogItemModel, byteImage: ByteArray?) {
        viewModel.insertDog(newDog, byteImage)
    }

    //uri -> file로 변환
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