package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityDetailPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.shared.SharedEvent
import com.nbcfinalteam2.ddaraogae.presentation.shared.SharedEventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPetBinding
    private val adapter: DetailPetAdapter by lazy {
        DetailPetAdapter{ item ->
            viewModel.selectDog(item)
        }
    }

    private var dogData = DogInfo("", "", 0)
    private val viewModel: DetailPetViewModel by viewModels()
    @Inject lateinit var sharedEventViewModel: SharedEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    private fun initView() {
        binding.btBack.setOnClickListener { finish() }

        binding.btDelete.setOnClickListener { viewModel.deleteSelectedDogData() }

        binding.tvEdit.setOnClickListener {
            viewModel.selectedDogState.value?.let {
                val intent = Intent(this@DetailPetActivity, EditPetActivity::class.java)
                intent.putExtra("dogData", it)
                startActivity(intent)
            }
        }

        setAdapter()
    }

    private fun setAdapter() = with(binding) {
        rvDogArea.adapter = adapter
        rvDogArea.layoutManager = LinearLayoutManager(this@DetailPetActivity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.dogListState.flowWithLifecycle(lifecycle).collectLatest { dogList ->
                if (dogList.isEmpty()) {
                    binding.clPetListEmpty.visibility = ConstraintLayout.VISIBLE
                    binding.scDetailPet.visibility = ScrollView.INVISIBLE
                    binding.tvEdit.visibility = AppCompatTextView.INVISIBLE

                } else {
                    binding.clPetListEmpty.visibility = ConstraintLayout.INVISIBLE
                    binding.scDetailPet.visibility = ScrollView.VISIBLE
                    binding.tvEdit.visibility = AppCompatTextView.VISIBLE
                }
                adapter.submitList(dogList)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDogState.flowWithLifecycle(lifecycle).collectLatest { dogInfo ->
                dogInfo?.let {
                    setView(dogInfo)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.deleteEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> {}
                    DefaultEvent.Loading -> {}
                    DefaultEvent.Success -> sharedEventViewModel.notifyDogRefreshEvent()
                }
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.dogRefreshEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    SharedEvent.Occur -> viewModel.refreshDogList()
                }
            }
        }
    }

    //강아지 정보 입력
    private fun setView(getDogData:DogInfo) = with(binding) {
        dogData = getDogData
        Glide.with(this@DetailPetActivity)
            .load(dogData.thumbnailUrl?.toUri())
            .error(R.drawable.ic_dog_default_thumbnail)
            .fallback(R.drawable.ic_dog_default_thumbnail)
            .into(ivDogThumbnail)
        tvPetName.text = dogData.name
        tvPetAge.text = dogData.age?.toString()
        tvPetBreed.text = dogData.lineage
        tvPetMemo.text = dogData.memo
        if (dogData.gender == 1) {
            tvFemale.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this.root.context,
                    R.color.soft_orange
                )
            )
            tvMale.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.root.context, R.color.white))
        } else {
            tvMale.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this.root.context,
                    R.color.soft_orange
                )
            )
            tvFemale.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this.root.context,
                    R.color.white
                )
            )
        }
    }
}