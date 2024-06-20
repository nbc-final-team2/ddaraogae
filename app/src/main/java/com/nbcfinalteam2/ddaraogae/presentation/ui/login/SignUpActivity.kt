package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivitySignUpBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.mypage.MypageAgreementPrivacy
import com.nbcfinalteam2.ddaraogae.presentation.ui.mypage.MypagePrivacyActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.mypage.MypageTermsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var email: String
    private lateinit var password: String
    
    private var correctEmail = false
    private var correctPassword = false
    private var correctPasswordCheck = false

    private var checkTerms = false
    private var checkPrivateTerms = false
    private var checkPrivateAgreeTerms = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        uiSetting()
        checkSignUpState()
        checkAuthentication()
        clickSignupButton()
        clickTerms()

        binding.ibtBack.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }
    }

    //email 중복 상태 체크
    private fun checkSignUpState() {
        lifecycleScope.launch {
            viewModel.userState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    if (state == 0) logIn()
                    if (state == 1)Toast.makeText(this@SignUpActivity, R.string.signup_fail, Toast.LENGTH_SHORT).show()
                    if (state == 2) Toast.makeText(this@SignUpActivity, R.string.signup_email_check, Toast.LENGTH_SHORT).show()
                    if (state > 10)Toast.makeText(this@SignUpActivity, R.string.signup_fail, Toast.LENGTH_SHORT).show()
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
            else if (!checkTerms || !checkPrivateTerms || !checkPrivateAgreeTerms)Toast.makeText(
                this,
                R.string.signup_check_terms,
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
    private fun clickTerms(){

        binding.cbSignupTerms.setOnCheckedChangeListener { _, isChecked ->
            checkTerms = isChecked
        }
        binding.cbSignupPersonalTerms.setOnCheckedChangeListener { _, isChecked ->
            checkPrivateTerms = isChecked
        }
        binding.cbSignupPersonalAgreeTerms.setOnCheckedChangeListener { _, isChecked ->
            checkPrivateAgreeTerms = isChecked
        }
        binding.ibSignupTerms.setOnClickListener {
            startActivity(Intent(this, MypageTermsActivity::class.java))

        }
        binding.ibSignupPersonalTerms.setOnClickListener {
            startActivity(Intent(this, MypagePrivacyActivity::class.java))
        }
        binding.ibSignupPersonalAgreeTerms.setOnClickListener {
            startActivity(Intent(this, MypageAgreementPrivacy::class.java))
        }
    }
}