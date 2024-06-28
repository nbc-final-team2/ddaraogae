package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentDetailPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.ui.edit.EditPetActivity.Companion.DOGDATA
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.edit.EditPetActivity
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPetFragment : Fragment() {
    private var _binding:FragmentDetailPetBinding? = null
    private val binding get() = _binding!!

    private var dogData = DogInfo("", "", 0)

    private val viewModel: DetailPetViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSelectedDogData()
        initView()

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.selectedDogIdState.flowWithLifecycle(lifecycle).collectLatest { dogId ->
                dogId?.let {
                    viewModel.getDogData(dogId)
                }
            }
        }
    }
    private fun initView(){
        binding.btBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
        binding.tvEdit.setOnClickListener {
            viewModel.selectedDogState.value?.let {
                val intent = Intent(requireActivity(), EditPetActivity::class.java)
                intent.putExtra(DOGDATA, it)
                startActivity(intent)
            }
        }
    }
    private fun getSelectedDogData(){
        lifecycleScope.launch {
            viewModel.selectedDogState.flowWithLifecycle(lifecycle).collectLatest { dogInfo ->
                dogInfo?.let {
                    setView(dogInfo)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loadEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }
    }

    private fun setView(getDogData: DogInfo) = with(binding) {
        dogData = getDogData
        Glide.with(requireActivity())
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