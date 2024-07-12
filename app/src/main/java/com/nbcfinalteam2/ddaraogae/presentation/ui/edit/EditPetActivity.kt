package com.nbcfinalteam2.ddaraogae.presentation.ui.edit

import android.Manifest
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityEditPetBinding
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.shared.KeyboardCleaner
import com.nbcfinalteam2.ddaraogae.presentation.ui.loading.LoadingDialog
import com.nbcfinalteam2.ddaraogae.presentation.util.DialogButtonListener
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.uriToByteArray
import com.nbcfinalteam2.ddaraogae.presentation.util.InformDialogMaker
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPetBinding
    private val viewModel: EditPetViewModel by viewModels()
    @Inject lateinit var itemChangedEventBus: ItemChangedEventBus

    private val keyboardCleaner: KeyboardCleaner by lazy {
        KeyboardCleaner(this)
    }

    private var dogData: DogInfo? = null
    private var loadingDialog: LoadingDialog? = null
    private val dialogButtonListener by lazy {
        object : DialogButtonListener {
            override fun onPositiveButtonClicked() {
                if (binding.ivRemoveThumbnail.isActivated) {
                    viewModel.setImageUri(null, null)
                }
                if (binding.tvDelete.isActivated) {
                    viewModel.deleteSelectedDogData(dogData?.id)
                }
            }


            override fun onNegativeButtonClicked() {

            }
        }
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
        binding = ActivityEditPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromIntent()
        uiSetting()
        initView()
        initViewModel()
        buttonState()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
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

        if (!viewModel.editUiState.value.isInit) {
            dogData?.let {
                if (it.gender == 1) rbFemale.isChecked = true
                else rbMale.isChecked = true

                etName.setText(it.name)
                etAge.setText(it.age?.toString())
                etBreed.setText(it.lineage)
                etMemo.setText(it.memo)
                viewModel.setImageUrl(it.thumbnailUrl)
            }
        }

        btBack.setOnClickListener {
            finish()
        }

        ivDogThumbnail.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ivRemoveThumbnail.setOnClickListener {
            val dialogMaker = InformDialogMaker.newInstance(title = getString(R.string.inform), message = getString(R.string.inform_msg_mypage_delete_dog_thumbnail))
            dialogMaker.show(supportFragmentManager, null)
            dialogMaker.registerCallBackLister(dialogButtonListener)
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
                val image = viewModel.editUiState.value.imageSource?.let {
                    when(it) {
                        is ImageSource.ImageUri -> null
                        is ImageSource.ImageUrl -> it.value
                    }
                }
                val changedDog = DogInfo(dogId, name, gender, age, breed, memo, image)
                viewModel.updateDog(changedDog)
            }
        }
        tvDelete.setOnClickListener {
            val dialogMaker = InformDialogMaker.newInstance(title = getString(R.string.inform), message = getString(R.string.inform_msg_detail_pet_delete))
            dialogMaker.show(supportFragmentManager, null)
            dialogMaker.registerCallBackLister(dialogButtonListener)
        }
    }
    private fun buttonState() = with(binding){
        val bgShape = binding.btnEditCompleted.background as GradientDrawable
        etName.doOnTextChanged{ text,_,_,_ ->
                var name = text.isNullOrBlank()
                if (!name)  bgShape.setColor(resources.getColor(R.color.brown))
                else bgShape.setColor(resources.getColor(R.color.grey))

        }
    }



    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.editUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                if (state.isLoading) {
                    loadingDialog = LoadingDialog()
                    loadingDialog?.show(supportFragmentManager, null)
                } else {
                    loadingDialog?.dismiss()
                    loadingDialog = null
                }

                val uri = when (state.imageSource) {
                    is ImageSource.ImageUri -> state.imageSource.value
                    is ImageSource.ImageUrl -> state.imageSource.value
                    else -> null
                }
                Glide.with(binding.ivDogThumbnail)
                    .load(uri ?: R.drawable.ic_dog_default_thumbnail)
                    .error(R.drawable.ic_dog_default_thumbnail)
                    .fallback(R.drawable.ic_dog_default_thumbnail)
                    .fitCenter()
                    .into(binding.ivDogThumbnail)

                binding.ivRemoveThumbnail.visibility = if (state.isThumbnailVisible) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.updateEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@EditPetActivity, event.msg)
                    DefaultEvent.Success -> {
                        itemChangedEventBus.notifyItemChanged()
                        ToastMaker.make(this@EditPetActivity, R.string.mypage_edit_msg_success_update)
                        finish()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deleteEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@EditPetActivity, event.msg)
                    DefaultEvent.Success -> {
                        itemChangedEventBus.notifyItemChanged()
                        ToastMaker.make(this@EditPetActivity, R.string.detail_pet_delete_complete)
                        binding.svEditPet.fullScroll(ScrollView.FOCUS_UP)
                        finish()
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_UP) keyboardCleaner.setPrevFocus(currentFocus)
        val result = super.dispatchTouchEvent(ev)
        keyboardCleaner.handleTouchEvent(ev)
        return result
    }

    companion object {
        const val DOGDATA = "DOGDATA"
    }
}