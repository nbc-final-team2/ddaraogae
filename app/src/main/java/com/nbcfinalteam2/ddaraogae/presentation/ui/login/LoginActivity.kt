package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
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

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initGoogle()
        clickGoogleLoginButton()
        getStateGoogleLogin()
    }

    private fun initGoogle() {
        //Google 로그인을 앱에 통합
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //onStart()보다 먼저 호출 되어야 함
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RC_SIGN_IN) {
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
    }

    private fun clickGoogleLoginButton() {
        //Id, Email request
        binding.ibtLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            Log.d("cliockclcickc", "클릭 했슴돠")
            activityResultLauncher.launch(signInIntent)
        }
    }

    private fun getStateGoogleLogin(){
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    updateUI(state.successGoogleLogin)
                }
        }
    }


    //firebase에 값 넘겨주기, 로그인
    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.signInGoogle(idToken)
    }

    private fun updateUI(user: Boolean) {
        if (user) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}