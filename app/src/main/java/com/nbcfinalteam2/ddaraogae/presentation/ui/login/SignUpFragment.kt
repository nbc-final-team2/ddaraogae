package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentSignUpBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.mypage.MypageAgreementPrivacy
import com.nbcfinalteam2.ddaraogae.presentation.ui.mypage.MypagePrivacyActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.mypage.MypageTermsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding:FragmentSignUpBinding?=null
    private val binding get() = _binding!!
    private val viewModel:SignUpViewModel by viewModels()

    private lateinit var email: String
    private lateinit var password: String

    private var correctEmail = false
    private var correctPassword = false
    private var correctPasswordCheck = false

    private var useTerms = false
    private var privacyPolicy = false
    private var agreementPrivacyPolicy = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSignUpState()
        checkAuthentication()
        clickSignupButton()
        checkAgreement()
        buttonState()

        binding.ibtBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.activity_login, LoginFragment())
                .commit()
        }
    }
    private fun checkSignUpState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { state ->
                    if (state == 0) logIn()
                    if (state == 1) Toast.makeText(requireContext(), R.string.signup_fail, Toast.LENGTH_SHORT).show()
                    if (state == 2) Toast.makeText(requireContext(), R.string.signup_email_check, Toast.LENGTH_SHORT).show()
                    if (state > 10) Toast.makeText(requireContext(), R.string.signup_fail, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun logIn() {
        Toast.makeText(requireContext(), R.string.signup_success, Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.activity_login, LoginFragment())
            .commit()
    }
    private fun checkAgreement(){
        var checkTotalBtn = false
        binding.cbTotalAgree.setOnCheckedChangeListener { _, isChecked ->
            if(checkTotalBtn){
                checkTotalBtn = false
            } else {
                binding.cbSignupTerms.isChecked = isChecked
                binding.cbSignupPersonalTerms.isChecked = isChecked
                binding.cbSignupPersonalAgreeTerms.isChecked = isChecked
            }

            checkTotalBtn = false
        }
        binding.cbSignupTerms.setOnCheckedChangeListener{ _, isChecked ->
            useTerms = isChecked
            checkTotalBtn = false
            if(!isChecked) {
                checkTotalBtn = true
                binding.cbTotalAgree.isChecked = false
            }
            if(useTerms && privacyPolicy && agreementPrivacyPolicy) binding.cbTotalAgree.isChecked = true
        }
        binding.cbSignupPersonalTerms.setOnCheckedChangeListener{ _, isChecked ->
            privacyPolicy = isChecked
            checkTotalBtn = false
            if(!isChecked) {
                checkTotalBtn = true
                binding.cbTotalAgree.isChecked = false
            }
            if(useTerms && privacyPolicy && agreementPrivacyPolicy) binding.cbTotalAgree.isChecked = true
        }
        binding.cbSignupPersonalAgreeTerms.setOnCheckedChangeListener{ _, isChecked ->
            agreementPrivacyPolicy = isChecked
            checkTotalBtn = false
            if(!isChecked) {
                checkTotalBtn = true
                binding.cbTotalAgree.isChecked = false
            }
            if(useTerms && privacyPolicy && agreementPrivacyPolicy) binding.cbTotalAgree.isChecked = true
        }
        binding.ibSignupTerms.setOnClickListener { startActivity(Intent(requireActivity(), MypageTermsActivity::class.java)) }
        binding.ibSignupPersonalTerms.setOnClickListener { startActivity(Intent(requireActivity(), MypagePrivacyActivity::class.java)) }
        binding.ibSignupPersonalAgreeTerms.setOnClickListener { startActivity(Intent(requireActivity(), MypageAgreementPrivacy::class.java)) }
    }


    //회원가입 버튼 클릭 시 동작
    private fun clickSignupButton() {
        binding.btSignup.setOnClickListener {
            if (!correctEmail || !correctPassword || !correctPasswordCheck) Toast.makeText(
                requireContext(),
                R.string.signup_account_warning,
                Toast.LENGTH_SHORT
            ).show()
            else if(!useTerms || !privacyPolicy || !agreementPrivacyPolicy) Toast.makeText(
                requireContext(),
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
            buttonState()
        }
        etSignupPassword.doOnTextChanged { _, _, _, _ ->
            correctPassword = checkPassword()
            buttonState()
        }
        etSignupPasswordCheck.doOnTextChanged { _, _, _, _ ->
            correctPasswordCheck = checkPasswordAgain()
            buttonState()
        }
    }
    private fun buttonState(){
        val bgShape = binding.btSignup.background as GradientDrawable
        if (correctEmail && correctPassword && correctPasswordCheck) {
            if(useTerms && privacyPolicy && agreementPrivacyPolicy) bgShape.setColor(resources.getColor(R.color.brown))
            else bgShape.setColor(resources.getColor(R.color.grey))
        } else bgShape.setColor(resources.getColor(R.color.grey))
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}