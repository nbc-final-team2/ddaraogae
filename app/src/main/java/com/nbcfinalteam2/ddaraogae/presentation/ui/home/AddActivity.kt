package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.Manifest
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.shared.SharedEventViewModel
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.uriToByteArray
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    //private var imageFile: File = File("")
    private var byteImage: ByteArray? = null
    private val viewModel: AddPetViewModel by viewModels()
    private val sharedEventViewModel: SharedEventViewModel by viewModels()

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
            viewModel.setImageUri(result.data?.data, uriToByteArray(result.data?.data, this))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiSetting()
        initView()
        initViewModel()
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            view.updatePadding(0, insets.top, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initView() = with(binding) {
        ivDogThumbnail.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                val gender = if(rgGenderGroup.checkedRadioButtonId==rbFemale.id) 1 else 0
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                val age =
                    if (etAge.text.toString().isEmpty()) null else etAge.text.toString().toInt()
                //val image = imageFile.toString().ifEmpty { null }
                val image = ""

                val newDog = DogItemModel("", name, gender, age, breed, memo, image)

                viewModel.insertDog(newDog)
            }
        }
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.addUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                state.imageUri?.let { uri ->
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

//                imageFile = File(getRealPathFromURI(it))
                    Glide.with(binding.ivDogThumbnail)
                        .load(uri)
                        .error(R.drawable.ic_dog_default_thumbnail)
                        .fallback(R.drawable.ic_dog_default_thumbnail)
                        .fitCenter()
                        .into(binding.ivDogThumbnail)

                }
            }
        }

        lifecycleScope.launch {
            viewModel.insertEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@AddActivity, event.msg)
                    DefaultEvent.Loading -> {}
                    DefaultEvent.Success -> {
                        sharedEventViewModel.notifyDogListChanged()
                        finish()
                    }
                }
            }
        }
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