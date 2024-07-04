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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDogIdState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { dogId ->
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
                viewModel.saveDisplayState("detailPet")
                val intent = Intent(requireActivity(), EditPetActivity::class.java)
                intent.putExtra(DOGDATA, it)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
    private fun getSelectedDogData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDogState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { dogInfo ->
                dogInfo?.let {
                    setView(dogInfo)
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
            tvFemale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_selected))
            tvMale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_unselected))
        } else {
            tvFemale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_unselected))
            tvMale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_selected))
        }
    }

}