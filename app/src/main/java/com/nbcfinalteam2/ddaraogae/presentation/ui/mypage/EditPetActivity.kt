package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityEditPetBinding
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.uriToByteArray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPetBinding
    private val viewModel: EditPetViewModel by viewModels()
    @Inject lateinit var itemChangedEventBus: ItemChangedEventBus

    private var dogData: DogInfo? = null

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
            viewModel.setImageUri(result.data?.data, uriToByteArray(result.data?.data, this))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromIntent()
        uiSetting()
        initView()
        initViewModel()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            view.updatePadding(0, insets.top, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun getDataFromIntent() {
        dogData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DOGDATA, DogInfo::class.java)
        } else {
            intent.getParcelableExtra(DOGDATA) as DogInfo?
        }
    }

    private fun initView() = with(binding) {
        lifecycleScope.launch {
            viewModel.taskState.collectLatest { state ->
                when (state) {
                    UpdateTaskState.Idle -> {
                        btnEditCompleted.isEnabled = false
                    }
                    UpdateTaskState.Loading -> {
                        btnEditCompleted.isEnabled = false
                    }
                    UpdateTaskState.Success -> {
                        btnEditCompleted.isEnabled = false
                        Toast.makeText(this@EditPetActivity, R.string.mypage_edit_msg_success_update, Toast.LENGTH_SHORT).show()
                    }
                    is UpdateTaskState.Error -> {
                        btnEditCompleted.isEnabled = true
                        Toast.makeText(this@EditPetActivity, R.string.mypage_edit_msg_fail_update, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dogData?.let {
            if (it.gender == 1) rbFemale.isChecked = true
            else rbMale.isChecked = true

            etName.setText(it.name)
            etAge.setText(it.age?.toString())
            etBreed.setText(it.lineage)
            etMemo.setText(it.memo)
            Glide.with(this@EditPetActivity)
                .load(
                    if (viewModel.editUiState.value.imageUri == null) it.thumbnailUrl else viewModel.editUiState.value.imageUri
                )
                .error(R.drawable.ic_dog_default_thumbnail)
                .fallback(R.drawable.ic_dog_default_thumbnail)
                .into(ivDogThumbnail)
        }

        btBack.setOnClickListener { finish() }

        ivDogThumbnail.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ivRemoveThumbnail.setOnClickListener {
            val builder = AlertDialog.Builder(this@EditPetActivity)
            builder.setMessage(R.string.mypage_delete_dog_thumbnail_message)
            builder.setPositiveButton(R.string.mypage_delete_dog_thumbnail_positive) { _, _ ->

                ivDogThumbnail.setImageResource(R.drawable.ic_dog_default_thumbnail)
                viewModel.setImageUri(null, null)
                ivRemoveThumbnail.visibility = View.GONE
            }
            builder.setNegativeButton(R.string.mypage_delete_dog_thumbnail_negative) { _, _ -> }
            builder.show()
        }

        btnEditCompleted.setOnClickListener {
            if (etName.text.toString().isEmpty()) {
                Toast.makeText(this@EditPetActivity, R.string.home_add_please_add_name, Toast.LENGTH_SHORT).show()
            }
            else {
                val dogId = dogData?.id
                val name = etName.text.toString()
                val gender = if(rgGenderGroup.checkedRadioButtonId==rbFemale.id) 1 else 0
                val age = if (etAge.text.toString().isEmpty()) null else etAge.text.toString().toInt()
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                val image = dogData?.thumbnailUrl.toString()

                val changedDog = DogInfo(dogId, name, gender, age, breed, memo, image)

                viewModel.updateDog(changedDog)
            }
        }
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.editUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                state.imageUri?.let { uri ->
                    Glide.with(binding.ivDogThumbnail)
                        .load(uri)
                        .error(R.drawable.ic_dog_default_thumbnail)
                        .fallback(R.drawable.ic_dog_default_thumbnail)
                        .fitCenter()
                        .into(binding.ivDogThumbnail)
                }

                binding.ivRemoveThumbnail.visibility = if (state.isThumbnailVisible || dogData?.thumbnailUrl != null) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.taskState.collectLatest { state ->
                when (state) {
                    UpdateTaskState.Idle -> binding.btnEditCompleted.isEnabled = true
                    UpdateTaskState.Loading -> binding.btnEditCompleted.isEnabled = false
                    UpdateTaskState.Success -> {
                        itemChangedEventBus.notifyItemChanged()
                        finish()
                    }
                    is UpdateTaskState.Error -> binding.btnEditCompleted.isEnabled = true
                }
            }
        }
    }

    companion object {
        const val DOGDATA = "DOGDATA"
    }
}