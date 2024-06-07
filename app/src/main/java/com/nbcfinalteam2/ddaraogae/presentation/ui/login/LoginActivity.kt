package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.nbcfinalteam2.ddaraogae.databinding.ActivityLogInBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity
import kotlin.math.sign

class LoginActivity :AppCompatActivity(){
    private lateinit var binding : ActivityLogInBinding
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var auth:FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        googleLogin()
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
        auth = Firebase.auth
        //Id, Email request
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("849448350578-obmt630heqpkg7pummgc6agrmlgs4ffa.apps.googleusercontent.com")
            //.requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
            //.requestServerAuthCode("551233417458-mea8ul175n5lc2hmdrtg5937f9b5a6q3.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.ibtLoginGoogle.setOnClickListener {

        }
        binding.ibtLoginGoogle.setOnClickListener {
            signIn()
        }
    }

    //firebase에 값 넘겨주기, 로그인
    private fun firebaseAuthWithGoogle(idToken:String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    Log.d("googleSignupSuccess", "${user}")
                    updateUI(user)
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun updateUI(user: FirebaseUser?){
        if(user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else googleLogin()
    }
    companion object{
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}