package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityPrivacyBinding

class MypagePrivacyActivity : AppCompatActivity(){
    private lateinit var binding :ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)

        binding.tvPrivacy.text = Html.fromHtml(getString(R.string.privacy_html))
        binding.btBack.setOnClickListener { finish() }
        setContentView(binding.root)
    }
}