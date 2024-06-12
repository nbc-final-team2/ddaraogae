package com.nbcfinalteam2.ddaraogae.presentation.ui.main

import android.content.Intent
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
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import dagger.hilt.android.AndroidEntryPoint

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
    private val lat = "37.5602945934345" //위도 lat
    private val lng = "127.081526307691" //경도 lng
    private fun testApiCall() {
        viewModel.fetchStoreData(lat, lng)
        viewModel.storeData.observe(this) {
            val store = it
            Log.d("MainActivity", "store data: $store")
        }
    }
}