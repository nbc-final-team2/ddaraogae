package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.os.Bundle
import android.text.Html
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityPrivacyBinding

class MypagePrivacyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        binding.btBack.setOnClickListener { finish() }
        setContentView(binding.root)
        uiSetting()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}