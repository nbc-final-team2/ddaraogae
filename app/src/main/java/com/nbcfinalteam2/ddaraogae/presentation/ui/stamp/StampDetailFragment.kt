package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbcfinalteam2.ddaraogae.databinding.FragmentStampDetailBinding

class StampDetailFragment : Fragment() {
    private var _binding: FragmentStampDetailBinding? = null
    private val binding get() = _binding!!
    private val stampDetailViewModel: StampDetailViewModel by viewModels()
    private lateinit var stampDetailAdapter: StampDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStampDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        stampDetailAdapter = StampDetailAdapter()
        binding.rvStampDetailArea.adapter = stampDetailAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}