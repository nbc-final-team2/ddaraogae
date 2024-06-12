package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityLogInBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var accountCheck = false
    private val viewModel: LoginViewModel by viewModels()
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "구글 로그인에 실패했습니다.", e)
            }
        }
        else Log.e(TAG, "Google Result Error ${result}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Log.d("ginger", "여기부턴 로그인 페이지 입니다.")
        initGoogle()
        clickLoginButton()
        getStateLogin()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getCurrentUser()
    }


    private fun initGoogle() {
        //Google 로그인을 앱에 통합
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun clickLoginButton() = with(binding){
        //click LoginButton
        btLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            viewModel.signInEmail(email, password)
            if (!accountCheck) Toast.makeText(this@LoginActivity, R.string.login_error, Toast.LENGTH_SHORT).show()
        }

        //click google Login Button
        ibtLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            activityResultLauncher.launch(signInIntent)
        }

        //click SignUp Button
        tvLoginSignup.setOnClickListener {
            etLoginEmail.setText(null)
            etLoginPassword.setText(null)
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }
    }

    //로그인 한 이력이 있으면 바로 홈 페이지 이동
    private fun getStateLogin(){
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    currentUserUI(state.isCurrentUser)
                    checkVerifiedUpdateUI(state.successLogin, state.verificationState)
                    Log.d("ginger_액티비티 수신여부", "${state.successLogin} , ${state.verificationState}")
                    accountCheck = state.correctEmailAccount
                }
        }
    }


    //firebase에 값 넘겨주기, 로그인
    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.signInGoogle(idToken)
    }

    //로그인 상태가 true면 홈으로 이동
    private fun checkVerifiedUpdateUI(user: Boolean, emailVerified : Boolean) {
        if (user && emailVerified) {
            Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else if(user && !emailVerified) {
            viewModel.deleteAccount()
        }
    }
    private fun currentUserUI(user:Boolean){
        if(user) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    companion object {
        private const val TAG = "GoogleActivity"
    }
}