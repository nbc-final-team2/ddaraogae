package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityDetailPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPetBinding
    private lateinit var adapter: DetailPetAdapter
    private var dogData = DogItemModel("", "", 0)
    private val viewModel: DetailPetViewModel by viewModels()
    private var dogDataList = listOf<DogItemModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
    }

    private fun initData() = with(binding) {
        adapter = DetailPetAdapter(this@DetailPetActivity)
        rvDogArea.adapter = adapter
        rvDogArea.layoutManager =
            LinearLayoutManager(this@DetailPetActivity, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    adapter.items = state.listPet
                    dogDataList = state.listPet
                    initView(0)
                }
        }
       // viewModel.loadPetList()

        adapter.setItemClickListener(object : DetailPetAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                initView(position)
            }
        })
    }

    private fun initView(position: Int) = with(binding) {
        dogData = dogDataList[position]
        Glide.with(this@DetailPetActivity)
            .load(dogDataList[position].thumbnailUrl)
            .into(ivDogThumbnail)
        tvPetName.text = dogDataList[position].name
        tvPetAge.text = dogDataList[position].age.toString()
        tvPetBreed.text = dogDataList[position].lineage
        tvPetMemo.text = dogDataList[position].memo
        if (dogDataList[position].gender == 1) {
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
    }

}