package com.nbcfinalteam2.ddaraogae.presentation.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityMainBinding
import com.nbcfinalteam2.ddaraogae.domain.repository.StoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 업체 검색 test code를 사용하기 위해서는 아래 EntryPoint code 주석을 해지해야 합니다.
 */
//@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_nav_container) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bnvNavBottom.setupWithNavController(navController)
    }

    /**
     * 업체 검색 test code 입니다.
     * 필요시 주석을 해재하여 사용하시고, 사용 후에는 삭제해주시면 되겠습니다.
     */
//    @Inject
//    lateinit var repository: StoreRepository
//    private fun testApiCall() {
//        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                val storeData = repository.getStoreData("37.5560294", "127.0872547")
//                Log.d("MainActivity", "store data: $storeData")
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Error fetching store data: ${e.message}")
//            }
//        }
//    }
}