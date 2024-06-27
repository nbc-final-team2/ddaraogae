package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmActivity: AppCompatActivity() {

    private val binding: ActivityAlarmBinding by lazy {
        ActivityAlarmBinding.inflate(layoutInflater)
    }

    private val alarmViewModel: AlarmViewModel by viewModels()

    private val alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter {
            alarmViewModel.deleteAlarm(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        uiSetting()

        binding.rvAlarmList.adapter = alarmAdapter

        binding.tvAddAlarm.setOnClickListener {
            val alarmDialog = AlarmSetDialogFragment(object : AlarmSetDialogFragment.AlarmDialogButtonListener {
                override fun onPositiveButtonClicked(time: Int) {
                    println(time)
                    alarmViewModel.insertAlarm(time)
                }

                override fun onNegativeButtonClicked() {
                }

            })

            alarmDialog.show(
                supportFragmentManager, "Dialog"
            )
        }

        lifecycleScope.launch {
            alarmViewModel.alarmUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                alarmAdapter.submitList(state.alarmList)
            }
        }
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

}