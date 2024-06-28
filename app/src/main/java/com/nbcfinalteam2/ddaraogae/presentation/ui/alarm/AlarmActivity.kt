package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAlarmBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.AlarmModel
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity: AppCompatActivity() {

    private val binding: ActivityAlarmBinding by lazy {
        ActivityAlarmBinding.inflate(layoutInflater)
    }

    private val alarmViewModel: AlarmViewModel by viewModels()
    @Inject lateinit var alarmManager: AlarmManager

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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        }
        else {
            ToastMaker.make(this, "알람 노출을 위한 알림 권한 부여가 필요합니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        uiSetting()

        checkPermission()

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

    private fun checkPermission() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms().not()) {
            AlertDialog.Builder(this)
                .setMessage("정확한 알람을 위한 알람&리마인더 설정 허용이 필요합니다.")
                .setPositiveButton(getString(R.string.alarm_dialog_move)) { _, _ ->
                    startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }.setNegativeButton(R.string.alarm_dialog_cancel) { _, _ -> }
                .show()
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