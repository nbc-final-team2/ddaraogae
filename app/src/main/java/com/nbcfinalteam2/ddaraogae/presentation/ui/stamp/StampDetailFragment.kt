package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.FragmentStampDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StampDetailFragment : Fragment() {
    private var _binding: FragmentStampDetailBinding? = null
    private val binding get() = _binding!!
    private val stampDetailViewModel: StampDetailViewModel by viewModels()
    private lateinit var stampDetailAdapter: StampDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStampDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupViewModel()
        getStampInfo()
        setupListener()
    }

    private fun setupAdapter() {
        stampDetailAdapter = StampDetailAdapter()
        binding.rvStampDetailArea.adapter = stampDetailAdapter
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            stampDetailViewModel.stampDetailState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { stampInfo ->
                    stampInfo?.let {
                        binding.tvStampName.text = it.title
                        binding.tvStampDescription.text = it.description
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            stampDetailViewModel.stampListState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { stampList ->
                    stampDetailAdapter.submitList(stampList)
                    if (stampList.isEmpty()) {
                        binding.cvEmptyData.visibility = View.VISIBLE
                    } else {
                        binding.cvEmptyData.visibility = View.INVISIBLE
                    }
                }
        }
    }


    private fun getStampInfo() {
        val stampId = arguments?.getInt("stampId") ?: 0
        stampDetailViewModel.loadStampDetail(stampId)
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}