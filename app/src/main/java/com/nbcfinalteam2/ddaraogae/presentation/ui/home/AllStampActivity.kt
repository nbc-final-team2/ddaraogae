package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAllStampBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllStampActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllStampBinding
    private val allStampViewModel: AllStampViewModel by viewModels()
    private lateinit var allStampAdapter: AllStampAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllStampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiSetting()
        setupAdapter()
        setupViewModel()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupAdapter() {
        allStampAdapter = AllStampAdapter()
        binding.rvStampArea.adapter = allStampAdapter
    }

    private fun setupViewModel() {
        lifecycleScope.launch {
            allStampViewModel.stampListState.collectLatest { stampList ->
                allStampAdapter.submitList(stampList)
            }
        }
    }
}
