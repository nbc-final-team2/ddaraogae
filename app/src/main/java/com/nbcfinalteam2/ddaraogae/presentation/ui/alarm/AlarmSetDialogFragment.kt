package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentAlarmSetDialogBinding
import com.nbcfinalteam2.ddaraogae.presentation.receiver.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AlarmSetDialogFragment(
    private val alarmDialogButtonListener: AlarmDialogButtonListener
): DialogFragment() {

    private var _binding: FragmentAlarmSetDialogBinding? = null
    private val binding: FragmentAlarmSetDialogBinding get() = _binding!!

    private val alarmManger: AlarmManager by lazy {
        requireActivity().applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmSetDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            val hour = binding.tpAlarm.hour
            val minute = binding.tpAlarm.minute
            alarmDialogButtonListener.onPositiveButtonClicked(
                binding.tpAlarm.hour*60 + binding.tpAlarm.minute
            )

            val intent = Intent(requireContext().applicationContext, AlarmReceiver::class.java)
            intent.putExtra("set_time", binding.tpAlarm.hour*60 + binding.tpAlarm.minute)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            alarmDialogButtonListener.onNegativeButtonClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface AlarmDialogButtonListener {
        fun onPositiveButtonClicked(time: Int)
        fun onNegativeButtonClicked()
    }

}