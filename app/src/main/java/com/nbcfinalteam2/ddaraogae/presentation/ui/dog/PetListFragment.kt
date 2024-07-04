package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentPetListBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.ui.add.AddActivity
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PetListFragment:Fragment() {
    private var _binding : FragmentPetListBinding? = null
    private val binding get() = _binding!!
    private val viewModel:DetailPetViewModel by activityViewModels()

    private val adapter:PetListAdapter by lazy {
        PetListAdapter{ item ->
            viewModel.selectDog(item)
            viewModel.saveDisplayState("detailPet")
            parentFragmentManager.beginTransaction()
                .add(R.id.fl_my_pet, DetailPetFragment())
                .commit()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setButtonAction()
        initViewModel()
        
    }

    override fun onStart() {
        super.onStart()
        viewModel.getDogList()
    }
    private fun setButtonAction() = with(binding){
        tvAdd.setOnClickListener {
            startActivity(Intent(this@PetListFragment.activity, AddActivity::class.java))
        }
        btBack.setOnClickListener {
            requireActivity().finish()
        }
    }
    private fun setAdapter() = with(binding){
        rcvPetList.adapter = adapter
        rcvPetList.layoutManager = LinearLayoutManager(this@PetListFragment.activity)
    }
    private fun initViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dogListState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { dogList ->
                adapter.submitList(dogList)
                if (dogList.isEmpty()) {
                    binding.tvEmptyList.visibility = AppCompatTextView.VISIBLE
                    binding.svPetList.visibility = ScrollView.INVISIBLE
                } else{
                    binding.svPetList.visibility = ScrollView.VISIBLE
                    binding.tvEmptyList.visibility = AppCompatTextView.INVISIBLE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }
    }

}