package com.nbcfinalteam2.ddaraogae.presentation.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCancelable(false) // 터치 못하도록 설정
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}