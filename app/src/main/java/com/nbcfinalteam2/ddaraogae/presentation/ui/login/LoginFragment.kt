package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentLogInBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding:FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    if (state == 2) Toast.makeText(requireContext(), R.string.login_fail, Toast.LENGTH_SHORT).show()
                    if (state == 3) sendEmail()
                    if (state > 10) Toast.makeText(
                        requireContext(), R.string.login_unknown_error, Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun sendEmail() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.login_dialog_title)
            .setMessage(R.string.login_dialog_message)
            .setPositiveButton(R.string.login_dialog_ok, DialogInterface.OnClickListener { _, _ ->
                viewModel.sendEmail()
                Toast.makeText(requireContext(), R.string.login_send_email, Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton(R.string.login_dialog_no, DialogInterface.OnClickListener { _, _ ->
                viewModel.deleteAccount()
                Toast.makeText(requireContext(), R.string.login_account_delete, Toast.LENGTH_SHORT).show()
            })
            .setCancelable(false)
        builder.show()
    }
    private fun clickLoginButton() = with(binding) {
        //click LoginButton
        btLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()
            if(email.isBlank() || password.isBlank()) Toast.makeText(requireContext(), R.string.login_input_account, Toast.LENGTH_SHORT).show()
            else viewModel.signInEmail(email, password)
        }

        //click google Login Button
        ibtLoginGoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            val signInIntent = googleSignInClient.signInIntent
            activityResultLauncher.launch(signInIntent)
        }

        //click SignUp Button
        tvLoginSignup.setOnClickListener {
            etLoginEmail.setText(null)
            etLoginPassword.setText(null)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.activity_login, SignUpFragment())
                .commit()
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.signInGoogle(idToken)
    }

    private fun successLogIn() {
        Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show()
        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }
    companion object {
        private const val TAG = "GoogleActivity"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}