package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityMyPetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPetActivity :AppCompatActivity() {
    private lateinit var binding : ActivityMyPetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPetBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_my_pet, PetListFragment())
            .commit()

        uiSetting()

        setContentView(binding.root)
    }
    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}