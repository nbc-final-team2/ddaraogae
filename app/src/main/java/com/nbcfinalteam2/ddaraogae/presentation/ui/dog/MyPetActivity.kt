package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityMyPetBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPetActivity :AppCompatActivity() {
    private lateinit var binding : ActivityMyPetBinding
    private val viewModel:DetailPetViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPetBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        uiSetting()
        setFragment()

        setContentView(binding.root)
    }
    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
    private fun setFragment(){
        lifecycleScope.launch {
            viewModel.loadDisplayEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    "detailPet" -> supportFragmentManager.beginTransaction()
                        .add(R.id.fl_my_pet, DetailPetFragment())
                        .commit()
                    else -> supportFragmentManager.beginTransaction()
                        .add(R.id.fl_my_pet, PetListFragment())
                        .commit()
                }
            }
        }
    }
}