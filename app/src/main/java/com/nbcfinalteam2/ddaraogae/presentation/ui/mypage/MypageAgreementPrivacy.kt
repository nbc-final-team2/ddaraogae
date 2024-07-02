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
import com.nbcfinalteam2.ddaraogae.databinding.ActivityAgreementPrivacyBinding
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.dateDateToString
import okio.FileNotFoundException
import java.util.Date

class MypageAgreementPrivacy : AppCompatActivity() {
    private lateinit var binding: ActivityAgreementPrivacyBinding
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
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        try {
            val assetManager = this.assets.open("doc_privacy_agreement")
            val htmlText = assetManager.bufferedReader().use { it.readText() }
            val spannedText = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvAgreementContents.text = spannedText

            val currentDate = Date()
            val signatureDate = dateDateToString(currentDate)
            binding.tvAgreementCurrentDate.text = signatureDate
        } catch (e: FileNotFoundException) {
            Log.e("MypageAgreementPrivacy", "file not found")
            binding.tvAgreementContents.text = getString(R.string.msg_not_found_file)
        }
    }
}