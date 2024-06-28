package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.ActivityStampBinding
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StampActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStampBinding
    private val allStampViewModel: AllStampViewModel by viewModels()
    private lateinit var allStampAdapter: AllStampAdapter
    @Inject lateinit var itemChangedEventBus: ItemChangedEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiSetting()
        setupAdapter()
        setupViewModel()
        setupListener()
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

        lifecycleScope.launch {
            allStampViewModel.loadStampEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@StampActivity, event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }

        lifecycleScope.launch{
            itemChangedEventBus.itemChangedEvent.flowWithLifecycle(lifecycle).collectLatest {
                allStampViewModel.loadStampList()
            }
        }
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

