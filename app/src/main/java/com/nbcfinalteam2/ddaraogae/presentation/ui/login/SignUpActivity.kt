package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
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
    private var signUpState = false
    private var correctEmail = false
    private var correctPassword = false
    private var correctPasswordCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkAuthentication()
        clickSignupButton()
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
    private fun clickSignupButton(){
        binding.btSignup.setOnClickListener {
            val email = binding.etSignupEmail.text.toString().trim()
            val password = binding.etSignupPassword.text.toString().trim()
            if(!correctEmail) Toast.makeText(this, R.string.signup_email_warning, Toast.LENGTH_SHORT).show()
            else if (!correctPassword) Toast.makeText(this, R.string.signup_password_warning, Toast.LENGTH_SHORT).show()
            else if (!correctPasswordCheck) Toast.makeText(this, R.string.signup_password_check_warning, Toast.LENGTH_SHORT).show()
            else signUp(email, password)
        }
    }
    private fun signUp(email : String, password:String){
        viewModel.signUp(email, password)
        lifecycleScope.launch {
            viewModel.userState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    signUpState = state
                    if (signUpState) {
                        Toast.makeText(this@SignUpActivity, R.string.signup_success, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    else {
                        Toast.makeText(this@SignUpActivity, R.string.signup_fail, Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
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