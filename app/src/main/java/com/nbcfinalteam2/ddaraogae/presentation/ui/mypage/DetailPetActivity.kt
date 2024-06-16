package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityDetailPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetailPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPetBinding
    private val adapter: DetailPetAdapter by lazy {
        DetailPetAdapter{ item ->
            onItemClick(item)
        }
    }
    private fun onItemClick(dogData: DogItemModel) {
        setView(dogData)
    }
    private var dogData = DogItemModel("", "", 0)
    private val viewModel: DetailPetViewModel by viewModels()
    private var dogDataList = listOf<DogItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiSetting()
        setAdapter()
        checkPetListEmpty()

        binding.btBack.setOnClickListener { finish() }

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    withContext(Dispatchers.Main) {
                        adapter.submitList(state.listPet)
                    }
                }
        }
    }

    //edit -> detail로 돌아왔을 때 데이터를 반영하기 위함
    override fun onResume() {
        super.onResume()
        viewModel.getDogList()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            view.updatePadding(0, insets.top, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    //user의 반려견 정보가 없으면 빈 창을 보여줌
    private fun checkPetListEmpty(){
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    if(state.listPetEmpty){
                        binding.clPetListEmpty.visibility = ConstraintLayout.VISIBLE
                        binding.scDetailPet.visibility = ScrollView.INVISIBLE
                        binding.tvEdit.visibility = AppCompatTextView.INVISIBLE

                    } else {
                        binding.clPetListEmpty.visibility = ConstraintLayout.INVISIBLE
                        binding.scDetailPet.visibility = ScrollView.VISIBLE
                        binding.tvEdit.visibility = AppCompatTextView.VISIBLE
                    }
                }
        }
    }

    //adapter에 반려견 정보 전달
    private fun setAdapter() = with(binding) {
        rvDogArea.adapter = adapter
        rvDogArea.layoutManager = LinearLayoutManager(this@DetailPetActivity, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    adapter.submitList(state.listPet)
                    dogDataList = state.listPet
                    setView(state.pet)
                }
        }
    }

    //초기, 클릭시 강아지 정보 입력
    private fun setView(getDogData:DogItemModel) = with(binding) {
        dogData = getDogData
        Glide.with(this@DetailPetActivity)
            .load(dogData.thumbnailUrl?.toUri())
            .error(R.drawable.ic_dog_default_thumbnail)
            .fallback(R.drawable.ic_dog_default_thumbnail)
            .into(ivDogThumbnail)
        tvPetName.text = dogData.name
        tvPetAge.text = dogData.age.toString()
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

        tvEdit.setOnClickListener {
            val intent = Intent(this@DetailPetActivity, EditPetActivity::class.java)
            intent.putExtra("dogData", dogData)
            startActivity(intent)
        }

        btDelete.setOnClickListener { dogData.id?.let { id -> deleteDogData(id) } }
    }

    //강아지 정보 삭제 함수
    private fun deleteDogData(dogId:String){
        viewModel.deleteDogData(dogId)
        viewModel.getDogList()
    }
}