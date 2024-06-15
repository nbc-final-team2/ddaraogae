package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.DialogWalkHistoryMapBinding

class WalkHistoryMapDialog : DialogFragment() {
    private var _binding: DialogWalkHistoryMapBinding? = null
    private val binding get() = _binding!!
    private var walkHistoryMap: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWalkHistoryMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("셋업뷰", walkHistoryMap.toString())
        setupView()
    }

    private fun setupView() {
        Glide.with(this)
            .load(walkHistoryMap)
            .error(R.drawable.login_google)
            .fallback(R.drawable.img_sample_mini_map)
            .into(binding.ivMap)
    }

    fun setEnlargementOfImage(image: String) {
        Log.d("이미지확대", "setEnlargementOfImage")
        walkHistoryMap = image
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}