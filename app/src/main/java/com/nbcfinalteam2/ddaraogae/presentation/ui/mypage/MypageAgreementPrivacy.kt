package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAgreementPrivacyBinding

class MypageAgreementPrivacy :AppCompatActivity() {
    private lateinit var binding:ActivityAgreementPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgreementPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btBack.setOnClickListener { finish() }
        uiSetting()
    }
    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            view.updatePadding(0, insets.top, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}