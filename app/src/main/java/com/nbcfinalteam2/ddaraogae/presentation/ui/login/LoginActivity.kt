package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
    private val viewModel: LoginViewModel by viewModels()

    //private var isPossible = -1

    private lateinit var googleSignInClient: GoogleSignInClient
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "구글 로그인에 실패했습니다.", e)
                }
            } else Log.e(TAG, "Google Result Error ${result}")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }

        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        checkIsPossible()
        clickLoginButton()
        viewModel.getCurrentUser()
    }

    private fun checkIsPossible() {
        lifecycleScope.launch {
            viewModel.userState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    if (state == 0) successLogIn()
                    if (state == 1) viewModel.checkVerified()
                    if (state == 2) Toast.makeText(this@LoginActivity, R.string.login_fail, Toast.LENGTH_SHORT).show()
                    if (state == 3) sendEmail()
                    if (state > 10) Toast.makeText(
                        this@LoginActivity, R.string.login_unknown_error, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun sendEmail() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.login_dialog_title)
            .setMessage(R.string.login_dialog_message)
            .setPositiveButton(R.string.login_dialog_ok, DialogInterface.OnClickListener { _, _ ->
                viewModel.sendEmail()
                Toast.makeText(this@LoginActivity, R.string.login_send_email, Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton(R.string.login_dialog_no, DialogInterface.OnClickListener { _, _ ->
                viewModel.deleteAccount()
                Toast.makeText(this@LoginActivity, R.string.login_account_delete, Toast.LENGTH_SHORT).show()
            })
            .setCancelable(false)
        builder.show()
    }

    private fun clickLoginButton() = with(binding) {
        //click LoginButton
        btLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()
            if(email.isBlank() || password.isBlank()) Toast.makeText(this@LoginActivity, R.string.login_input_account, Toast.LENGTH_SHORT).show()
            else viewModel.signInEmail(email, password)
        }

        //click google Login Button
        ibtLoginGoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

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

    //firebase에 값 넘겨주기, 로그인
    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.signInGoogle(idToken)
    }

    private fun successLogIn() {
        Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "GoogleActivity"
    }
}