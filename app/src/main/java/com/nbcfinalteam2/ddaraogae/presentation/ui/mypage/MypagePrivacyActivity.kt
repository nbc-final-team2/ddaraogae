package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityPrivacyBinding
import java.io.FileNotFoundException

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

        try {
            val assetManager = this.assets.open("doc_privacy_rules")
            val htmlText = assetManager.bufferedReader().use { it.readText() }
            val spannedText = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvPrivacyContents.text = spannedText
        } catch (e: FileNotFoundException) {
            Log.e("MypagePrivacy", "file not found")
            binding.tvPrivacyContents.text = getString(R.string.msg_not_found_file)
        }
    }
}