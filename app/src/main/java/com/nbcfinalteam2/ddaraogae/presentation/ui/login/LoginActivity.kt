package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityLoginBinding
import com.nbcfinalteam2.ddaraogae.presentation.shared.KeyboardCleaner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val keyboardCleaner: KeyboardCleaner by lazy {
        KeyboardCleaner(this)
    }

    private lateinit var binding:ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
//        splashScreen.setKeepOnScreenCondition{
//            false
//        }
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_login, LoginFragment())
            .commit()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_UP) keyboardCleaner.setPrevFocus(currentFocus)
        val result = super.dispatchTouchEvent(ev)
        keyboardCleaner.handleTouchEvent(ev)
        return result
    }
}