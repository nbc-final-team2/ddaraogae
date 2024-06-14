package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var email: String
    private lateinit var password: String

    private var signUpState = -1
    private var correctEmail = false
    private var correctPassword = false
    private var correctPasswordCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkSignUpState()
        checkAuthentication()
        clickSignupButton()

        binding.ibtBack.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
    }


    //email 중복 상태 체크
    private fun checkSignUpState() {
        lifecycleScope.launch {
            viewModel.userState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    signUpState = state
                    if (signUpState == 0) logIn()
                    if (signUpState == 1)Toast.makeText(this@SignUpActivity, R.string.signup_fail, Toast.LENGTH_SHORT).show()
                    if (signUpState == 2) Toast.makeText(this@SignUpActivity, R.string.signup_email_check, Toast.LENGTH_SHORT).show()
                    if (signUpState > 10)Toast.makeText(this@SignUpActivity, R.string.signup_fail, Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun logIn() {
        Toast.makeText(this@SignUpActivity, R.string.signup_success, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        finish()
    }


    //회원가입 버튼 클릭 시 동작
    private fun clickSignupButton() {
        binding.btSignup.setOnClickListener {
            if (!correctEmail || !correctPassword || !correctPasswordCheck) Toast.makeText(
                this,
                R.string.signup_account_warning,
                Toast.LENGTH_SHORT
            ).show()
            else {
                viewModel.signUp(email, password)


            }

        }

    }

private fun checkAuthentication() = with(binding) {
    etSignupEmail.doOnTextChanged { _, _, _, _ ->
        correctEmail = checkEmail()
    }
    etSignupPassword.doOnTextChanged { CharSequence, _, _, _ ->
        correctPassword = checkPassword()
    }
    etSignupPasswordCheck.doOnTextChanged { CharSequence, _, _, _ ->
        correctPasswordCheck = checkPasswordAgain()
    }
}

//email, password 유효성 검사
private fun checkEmail(): Boolean {
    email = binding.etSignupEmail.text.toString().trim()
    val emailPatternCheck = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    if (emailPatternCheck) {
        binding.tvSignupEmailWarning.visibility = AppCompatTextView.INVISIBLE
        return true
    } else {
        binding.tvSignupEmailWarning.visibility = AppCompatTextView.VISIBLE
        return false
    }
    return false
}

private fun checkPassword(): Boolean {
    password = binding.etSignupPassword.text.toString().trim()
    val passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
    val passwordPatternCheck = Pattern.matches(passwordPattern, password)
    if (passwordPatternCheck) {
        binding.tvSignupPasswordWarning.visibility = AppCompatTextView.INVISIBLE
        return true
    } else {
        binding.tvSignupPasswordWarning.visibility = AppCompatTextView.VISIBLE
        return false
    }
}

private fun checkPasswordAgain(): Boolean {
    val password = binding.etSignupPassword.text.toString().trim()
    val passwordCheck = binding.etSignupPasswordCheck.text.toString().trim()
    if (passwordCheck == password) {
        binding.tvSignupPasswordCheckWarning.visibility = AppCompatTextView.INVISIBLE
        return true
    } else {
        binding.tvSignupPasswordCheckWarning.visibility = AppCompatTextView.VISIBLE
        return false
    }
}
}