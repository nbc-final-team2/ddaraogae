package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpActivity:AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    private val viewModel:SignUpViewModel by viewModels()
    private lateinit var email: String
    private lateinit var password: String
    private var signUpState = false
    private var correctEmail = false
    private var correctPassword = false
    private var correctPasswordCheck = false
    private var verificationState = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkSignUpState()
        checkAuthentication()
        clickEmailAuthentication()
        clickSignupButton()

        binding.ibtBack.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
    }


    //email 중복 상태 체크
    private fun checkSignUpState(){
        lifecycleScope.launch {
            viewModel.emailState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    if (!state) Toast.makeText(
                        this@SignUpActivity,
                        R.string.signup_email_check,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etSignupEmail.isEnabled = true
                    binding.etSignupPassword.isEnabled = true
                    binding.etSignupPasswordCheck.isEnabled = true

                    binding.btAuthentication.visibility = AppCompatButton.VISIBLE
                    binding.btAuthenticationCheck.visibility = AppCompatButton.INVISIBLE
                }
        }
        lifecycleScope.launch {
            viewModel.userState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    signUpState = state
                }
        }

    }

    private fun checkAuthentication() = with(binding){
        etSignupEmail.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                correctEmail = checkEmail()
                Log.d("signup_email", "${correctEmail}")
            }
        })
        etSignupPassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                correctPassword = checkPassword()
                Log.d("signup_pass", "${correctPassword}")
            }
        })
        etSignupPasswordCheck.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                correctPasswordCheck = checkPasswordAgain()
                Log.d("signup_pass_check", "${correctPasswordCheck}")
            }
        })
    }

    //이메일 인증 버튼 클릭
    private fun clickEmailAuthentication(){
        //이메일 인증 메일 보내기
        binding.btAuthentication.setOnClickListener {
            email = binding.etSignupEmail.text.toString().trim()
            password = binding.etSignupPassword.text.toString().trim()

            if(!correctEmail) Toast.makeText(this, R.string.signup_email_warning, Toast.LENGTH_SHORT).show()
            else if (!correctPassword) Toast.makeText(this, R.string.signup_password_warning, Toast.LENGTH_SHORT).show()
            else if (!correctPasswordCheck) Toast.makeText(this, R.string.signup_password_check_warning, Toast.LENGTH_SHORT).show()
            else {
                //아이디, 비밀번호 값 수정 금지
                binding.etSignupEmail.isEnabled = false
                binding.etSignupPassword.isEnabled = false
                binding.etSignupPasswordCheck.isEnabled = false

                viewModel.signUp(email, password)
                Toast.makeText(this, R.string.signup_send_email, Toast.LENGTH_SHORT).show()

                binding.btAuthentication.visibility = AppCompatButton.INVISIBLE
                binding.btAuthenticationCheck.visibility = AppCompatButton.VISIBLE
            }
        }
        //인증이 확인됐으면 로그아웃 , 인증되지 않았으면 계정 삭제
        binding.btAuthenticationCheck.setOnClickListener {
            viewModel.isCurrentUserEmailVerified()
            lifecycleScope.launch {
                viewModel.verificationState.flowWithLifecycle(lifecycle)
                    .collectLatest { state ->
                        verificationState =  state
                        if (state) {
                            Toast.makeText(
                                this@SignUpActivity,
                                R.string.signup_email_authentication_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btAuthenticationCheck.visibility = AppCompatButton.INVISIBLE
                            binding.btSignup.isEnabled = true
                            binding.btSignup.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@SignUpActivity, R.color.brown))
                        }
                        else {
                            Toast.makeText(
                                this@SignUpActivity,
                                R.string.signup_email_authentication_fail,
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.etSignupEmail.isEnabled = true
                            binding.etSignupPassword.isEnabled = true
                            binding.etSignupPasswordCheck.isEnabled = true

                            viewModel.deleteAccount()

                            binding.btAuthentication.visibility = AppCompatButton.VISIBLE
                            binding.btAuthenticationCheck.visibility = AppCompatButton.INVISIBLE
                        }
                    }
            }
        }
    }
    //회원가입 버튼 클릭 시 동작
    private fun clickSignupButton(){
        binding.btSignup.setOnClickListener {
            if (signUpState) {
                Toast.makeText(this@SignUpActivity, R.string.signup_success, Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                Toast.makeText(this@SignUpActivity, R.string.signup_fail, Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
    }

    //email, password 유효성 검사
    private fun checkEmail() : Boolean{
        val email = binding.etSignupEmail.text.toString().trim()
        val emailPatternCheck = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (emailPatternCheck){
            binding.tvSignupEmailWarning.visibility = AppCompatTextView.INVISIBLE
            return true
        }else{
            binding.tvSignupEmailWarning.visibility = AppCompatTextView.VISIBLE
            return false
        }
        return false
    }
    private fun checkPassword() : Boolean{
        val password = binding.etSignupPassword.text.toString().trim()
        val passwordPattern =  "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
        val passwordPatternCheck = Pattern.matches(passwordPattern, password)
        if (passwordPatternCheck){
            binding.tvSignupPasswordWarning.visibility = AppCompatTextView.INVISIBLE
            return true
        } else {
            binding.tvSignupPasswordWarning.visibility = AppCompatTextView.VISIBLE
            return false
        }
    }
    private fun checkPasswordAgain():Boolean {
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