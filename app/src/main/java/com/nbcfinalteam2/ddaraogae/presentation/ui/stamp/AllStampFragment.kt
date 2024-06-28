package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.FragmentAllStampBinding
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AllStampFragment : Fragment() {
    private var _binding: FragmentAllStampBinding? = null
    private val binding get() = _binding!!

    private val allStampViewModel: AllStampViewModel by viewModels()
    private lateinit var allStampAdapter: AllStampAdapter
    @Inject
    lateinit var itemChangedEventBus: ItemChangedEventBus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllStampBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupViewModel()
        setupListener()
    }

    private fun setupAdapter() {
        allStampAdapter = AllStampAdapter()
        binding.rvStampArea.adapter = allStampAdapter
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            allStampViewModel.stampListState.collectLatest { stampList ->
                allStampAdapter.submitList(stampList)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            allStampViewModel.loadStampEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { event ->
                when (event) {
                    is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            itemChangedEventBus.itemChangedEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                allStampViewModel.loadStampList()
            }
        }
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}