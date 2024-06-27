package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAlarmBinding
import com.nbcfinalteam2.ddaraogae.presentation.receiver.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

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
                    val hour = binding.tpAlarm.hour
                    val minute = binding.tpAlarm.minute

                    val intent = Intent(requireContext().applicationContext, AlarmReceiver::class.java)
                    intent.putExtra("set_time", binding.tpAlarm.hour*60 + binding.tpAlarm.minute)
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
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