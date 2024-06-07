package com.nbcfinalteam2.ddaraogae.presentation.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityMainBinding
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 업체 검색 test code를 사용하기 위해서는 아래 EntryPoint code 주석을 해지해야 합니다.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        initBottomNavigation()
        testApiCall() //test fun
    }

    private fun initBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_nav_container) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bnvNavBottom.setupWithNavController(navController)
    }

    /**
     * 업체 검색 test code 입니다.
     * 필요시 주석을 해재하여 사용하시고, 사용 후에는 삭제해주시면 되겠습니다.
     * 삭제 시 TestViewModel도 함께 삭제해주셔야 합니다.
     */
    private val viewModel: TestViewModel by viewModels()
    private fun testApiCall() {
        val lat = "" //위도 lat
        val lng = "" //경도 lng
        viewModel.fetchStoreData(lat, lng)

        viewModel.storeData.observe(this) {
            val data = it
            Log.d("MainActivity", "store data: $data")

        }
    }
}