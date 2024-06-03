package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityWalkBinding

class WalkActivity : AppCompatActivity() {

    private val binding by lazy { ActivityWalkBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFragment(WalkFragment())
    }
    private fun setFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view_walk, frag)
            setReorderingAllowed(true)
            addToBackStack(null)
        }.commit()
    }
}