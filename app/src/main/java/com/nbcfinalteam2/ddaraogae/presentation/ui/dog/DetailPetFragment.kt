package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
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
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.ui.edit.EditPetActivity.Companion.DOGDATA
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.edit.EditPetActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.loading.LoadingDialog
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailPetFragment : Fragment() {
    private var _binding:FragmentDetailPetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailPetViewModel by activityViewModels()
    private var loadingDialog: LoadingDialog? = null
    @Inject
    lateinit var itemChangedEventBus: ItemChangedEventBus
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

        initView()
        initViewModel()

    }
    private fun initViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDogState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { dogInfo ->
                dogInfo?.let {
                    setView(dogInfo)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDogIdState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { getDogId ->
                getDogId?.let {
                    viewModel.getDogData(getDogId)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            itemChangedEventBus.itemChangedEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    viewModel.refreshDogInfo()
                }
        }
        lifecycleScope.launch {
            viewModel.nullEvent.flowWithLifecycle(lifecycle).collectLatest {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_my_pet, PetListFragment())
                    .commit()
                viewModel.saveDisplayState("petList")
            }
        }
        lifecycleScope.launch {
            viewModel.detailUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                if (state.isLoading) {
                    loadingDialog = LoadingDialog()
                    loadingDialog?.show(parentFragmentManager, null)
                } else {
                    loadingDialog?.dismiss()
                    loadingDialog = null
                }
            }
        }
    }

    private fun initView(){
        binding.btBack.setOnClickListener {
            viewModel.saveDisplayState("petList")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_my_pet, PetListFragment())
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

    private fun setView(getDogData: DogInfo) = with(binding) {
        //dogData = getDogData
        Glide.with(requireActivity())
            .load(getDogData.thumbnailUrl)
            .error(R.drawable.ic_dog_default_thumbnail)
            .fallback(R.drawable.ic_dog_default_thumbnail)
            .into(ivDogThumbnail)
        tvPetName.text = getDogData.name
        tvPetAge.text = getDogData.age?.toString()
        tvPetBreed.text = getDogData.lineage
        tvPetMemo.text = getDogData.memo
        if (getDogData.gender == 1) {
            tvFemale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_selected))
            tvMale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_unselected))
        } else {
            tvFemale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_unselected))
            tvMale.setBackgroundDrawable(requireActivity().resources.getDrawable(R.drawable.radio_button_selected))
        }
    }

}