package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAlarmBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.AlarmModel
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
        AlarmAdapter(object : AlarmAdapter.AlarmItemListener {
            override fun onItemClicked(item: AlarmModel) {
                val alarmDialog = AlarmSetDialogFragment(object : AlarmSetDialogFragment.AlarmDialogButtonListener {
                    override fun onPositiveButtonClicked(time: Int) {
                        alarmViewModel.updateAlarm(item.id, time)
                    }

                    override fun onNegativeButtonClicked() {
                    }

                }, item.setHour*60 + item.setMinute + if(item.isPm) 720 else 0)

                alarmDialog.show(
                    supportFragmentManager, null
                )
            }

            override fun onDeleteClicked(id: Int) {
                alarmViewModel.deleteAlarm(id)
            }

        })
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
                supportFragmentManager, null
            )
        }

        lifecycleScope.launch {
            alarmViewModel.alarmUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                alarmAdapter.submitList(state.alarmList)
                binding.rvAlarmList.isVisible = state.alarmList.isNotEmpty()
                binding.tvEmptyList.isVisible = state.alarmList.isEmpty()
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