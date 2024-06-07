package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityLogInBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity :AppCompatActivity(){
    private lateinit var binding : ActivityLogInBinding
    private lateinit var googleSignInClient:GoogleSignInClient
    private val viewModel:LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        googleLogin()
        getCurrentUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val accout = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(accout.idToken!!)
            }catch (e:ApiException){
                Log.w(TAG, "구글 로그인에 실패했습니다.", e)
            }
        }
    }
    private fun googleLogin(){
        //Id, Email request
        binding.ibtLoginGoogle.setOnClickListener {
            signIn()
        }
    }
    private fun getCurrentUser(){
        viewModel.getCurrentUser()
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    updateUI(state.currentUser)
                }
        }
    }
    //firebase에 값 넘겨주기, 로그인
    private fun firebaseAuthWithGoogle(idToken:String){
        viewModel.signInGoogle(idToken)
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    updateUI(state.successGoogleLogin)
                }
        }
    }
    private fun signIn(){
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun updateUI(user: Boolean){
        if(user) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else googleLogin()
    }
    companion object{
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}