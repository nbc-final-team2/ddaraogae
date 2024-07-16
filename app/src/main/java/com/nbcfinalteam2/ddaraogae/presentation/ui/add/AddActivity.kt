package com.nbcfinalteam2.ddaraogae.presentation.ui.add

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MotionEvent
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
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAddBinding
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.shared.KeyboardCleaner
import com.nbcfinalteam2.ddaraogae.presentation.ui.loading.LoadingDialog
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.uriToByteArray
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    private val viewModel: AddPetViewModel by viewModels()
    @Inject lateinit var itemChangedEventBus: ItemChangedEventBus

    private var loadingDialog: LoadingDialog? = null

    private val keyboardCleaner: KeyboardCleaner by lazy {
        KeyboardCleaner(this)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.setImageUri(uri, uriToByteArray(uri, this))
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
        buttonState()
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initView() = with(binding) {

        ivDogThumbnail.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        ivRemoveThumbnail.setOnClickListener {
            val builder = AlertDialog.Builder(this@AddActivity)
            builder.setMessage(R.string.mypage_delete_dog_thumbnail_message)
            builder.setPositiveButton(R.string.mypage_delete_dog_thumbnail_positive) { _, _ ->
                viewModel.setImageUri(null, null)
            }
            builder.setNegativeButton(R.string.mypage_delete_dog_thumbnail_negative) { _, _ -> }
            builder.show()
        }

        //완료 버튼 클릭 시 데이터 추가
        btnEditCompleted.setOnClickListener {
            if (etName.text.toString().isBlank()) {
                Toast.makeText(this@AddActivity, R.string.home_add_please_add_name, Toast.LENGTH_SHORT).show()
            }
            else {
                val name = etName.text.toString()
                val gender = if(rgGenderGroup.checkedRadioButtonId==rbFemale.id) 1 else 0
                val breed = etBreed.text.toString()
                val memo = etMemo.text.toString()
                val age =
                    if (etAge.text.toString().isEmpty()) null else etAge.text.toString().toInt()
                val image = ""

                val newDog = DogInfo("", name, gender, age, breed, memo, image)

                viewModel.insertDog(newDog)
            }
        }
    }
    private fun buttonState() = with(binding){
        val bgShape = binding.btnEditCompleted.background as GradientDrawable
        bgShape.setColor(resources.getColor(R.color.grey))
        etName.doOnTextChanged{ text,_,_,_ ->
            var name = text.isNullOrBlank()
            if (!name)  bgShape.setColor(resources.getColor(R.color.brown))
            else bgShape.setColor(resources.getColor(R.color.grey))

        }
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.addUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                if (state.isLoading) {
                    loadingDialog = LoadingDialog()
                    loadingDialog?.show(supportFragmentManager, null)
                } else {
                    loadingDialog?.dismiss()
                    loadingDialog = null
                }

                state.imageUri?.let { uri ->
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                Glide.with(binding.ivDogThumbnail)
                    .load(viewModel.addUiState.value.imageUri)
                    .error(R.drawable.ic_dog_default_thumbnail)
                    .fallback(R.drawable.ic_dog_default_thumbnail)
                    .fitCenter()
                    .into(binding.ivDogThumbnail)

                binding.ivRemoveThumbnail.visibility = if (state.isThumbnailVisible) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.insertEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@AddActivity, event.msg)
                    DefaultEvent.Success -> {
                        itemChangedEventBus.notifyItemChanged()
                        Toast.makeText(this@AddActivity, R.string.home_add_msg_success_insert, Toast.LENGTH_SHORT).show()
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

}