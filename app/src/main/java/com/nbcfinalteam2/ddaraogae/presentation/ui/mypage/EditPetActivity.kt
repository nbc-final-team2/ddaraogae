package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityEditPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.uriToByteArray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPetBinding
    private val viewModel: EditPetViewModel by viewModels()

    //    private var imageFile: File = File("")
    private var imageUri: Uri? = null
    private lateinit var dogData: DogItemModel
    private lateinit var dogId: String

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
            imageUri = result.data?.data
            imageUri?.let {
                //imageFile = File(getRealPathFromURI(it))
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
        setContentView(binding.root)
        enableEdgeToEdge()
        uiSetting()
        binding.btBack.setOnClickListener { finish() }
        initView()
        changeDogData()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            view.updatePadding(0, insets.top, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initView() = with(binding) {
        val intent = intent
        dogData = intent.getParcelableExtra("dogData")!!
        if (dogData.gender == 1) rbFemale.isChecked = true
        else rbMale.isChecked = true
        Glide.with(this@EditPetActivity)
            .load(dogData.thumbnailUrl)
            .error(R.drawable.ic_dog_default_thumbnail)
            .fallback(R.drawable.ic_dog_default_thumbnail)
            .into(ivDogThumbnail)
        etName.setText(dogData.name)
        etAge.setText(dogData.age.toString())
        etBreed.setText(dogData.lineage)
        etMemo.setText(dogData.memo)
        //imageFile = File(dogData.thumbnailUrl)
        imageUri = dogData.thumbnailUrl?.toUri()
        dogId = dogData.id ?: ""
    }

    //강아지 정보 수정
    @SuppressLint("ResourceAsColor")
    private fun changeDogData() = with(binding) {
        var gender = 0
        ivDogThumbnail.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        rgGenderGroup.setOnCheckedChangeListener { _, id ->
            gender = if (id == rbFemale.id) 1 else 0
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
                //val image = imageFile.toString().ifEmpty { null }
                val image = imageUri.toString()

                val changeDog = DogItemModel(dogId, name, gender, age, breed, memo, image)
                val byteImage = uriToByteArray(imageUri, this@EditPetActivity)

                requestChangeDogData(changeDog, byteImage)

                lifecycleScope.launch {
                    viewModel.taskState.collectLatest { state ->
                        when (state) {
                            UpdateTaskState.Idle -> btnEditCompleted.isEnabled = true
                            UpdateTaskState.Loading -> btnEditCompleted.isEnabled = false
                            UpdateTaskState.Success -> finish()
                            is UpdateTaskState.Error -> btnEditCompleted.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    //강아지 정보 수정 함수
    private fun requestChangeDogData(changeDogData: DogItemModel, byteImage: ByteArray?) {
        viewModel.updateDog(changeDogData, byteImage)
    }

    //uri -> file 형태로 변환
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