package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.nbcfinalteam2.ddaraogae.databinding.FragmentMypageBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.ui.alarm.AlarmActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.dog.MyPetActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.loading.LoadingDialog
import com.nbcfinalteam2.ddaraogae.presentation.util.DialogButtonListener
import com.nbcfinalteam2.ddaraogae.presentation.util.InformDialogMaker
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MypageFragment : Fragment() {
    private var _binding:FragmentMypageBinding ?= null
    private val binding get() = _binding!!
    private val viewModel : MyPageViewModel by viewModels()
    private val dialogButtonListener by lazy {
        object : DialogButtonListener {
            override fun onPositiveButtonClicked() {
                viewModel.logOut()
            }
            override fun onNegativeButtonClicked() {
            }

        }
    }

    private var loadingDialog: LoadingDialog? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleToken : String
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    googleToken = account.idToken!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "구글 로그인에 실패했습니다.", e)
                }
            }else if(result.resultCode == AppCompatActivity.RESULT_CANCELED){
                startActivity(Intent.makeRestartActivityTask(requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)?.component).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                requireActivity().finishAffinity()
            } else Log.e(TAG, "Google Result Error ${result}")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickAboutAccountBtn()
        clickAboutPetBtn()
        clickPrivacyBtn()
        clickAboutFunctionBtn()
        initViewModel()

    }

    private fun clickAboutAccountBtn(){
        binding.tvSignOut.setOnClickListener {
            val dialogMaker = InformDialogMaker.newInstance(title = getString(R.string.inform), message = getString(R.string.inform_msg_mypage_logout))
            dialogMaker.show(requireActivity().supportFragmentManager, null)
            dialogMaker.registerCallBackLister(dialogButtonListener)
        }

        binding.tvSignDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.msg_delete_account)
            builder.setMessage(R.string.msg_delete_account_context)
            builder.setPositiveButton(R.string.mypage_delete_dog_thumbnail_positive) { _, _ ->
                viewModel.isGoogleUser()
            }
            builder.setNegativeButton(R.string.mypage_delete_dog_thumbnail_negative) { _, _ -> }
            builder.show()

        }
    }
    private fun clickAboutPetBtn(){

        binding.tvMyDogEdit.setOnClickListener {
            startActivity(Intent(requireActivity(), MyPetActivity::class.java))
        }
    }
    private fun clickPrivacyBtn(){
        binding.tvUseTerms.setOnClickListener {
            startActivity(Intent(requireActivity(), MypageTermsActivity::class.java))
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            startActivity(Intent(requireActivity(), MypagePrivacyActivity::class.java))
        }
        binding.tvAgreementPrivacyPolicy.setOnClickListener {
            startActivity(Intent(requireActivity(), MypageAgreementPrivacy::class.java))
        }
    }
    private fun clickAboutFunctionBtn() {
        binding.tvSetAlarm.setOnClickListener {
            startActivity(Intent(requireActivity(), AlarmActivity::class.java))
        }
    }

    private fun initViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.restartEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                when(it) {
                    is DefaultEvent.Failure -> {}
                    DefaultEvent.Success -> {
                        startActivity(Intent.makeRestartActivityTask(requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)?.component).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                        requireActivity().finishAffinity()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mypageEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { event->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mypageUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { state->
                if (state.isLoading) {
                    loadingDialog = LoadingDialog()
                    loadingDialog?.show(parentFragmentManager, null)
                } else {
                    loadingDialog?.dismiss()
                    loadingDialog = null
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isGoogleLogin.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { state->
               if (state){
                   viewModel.googleLogOut()
                   val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                       .requestIdToken(getString(R.string.default_web_client_id))
                       .requestEmail()
                       .build()
                   googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

                   val signInIntent = googleSignInClient.signInIntent
                   activityResultLauncher.launch(signInIntent)
               }
                else{
                   val builder = AlertDialog.Builder(requireContext())
                   builder.setTitle(R.string.email_delete_account_title)

                   val inputPassword = EditText(requireContext())
                   inputPassword.inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                   builder.setView(inputPassword)

                   builder.setPositiveButton(R.string.mypage_delete_dog_thumbnail_positive) { _, _ ->
                       val password = inputPassword.text.toString()
                       viewModel.deleteUser(password)
                   }
                   builder.setNegativeButton(R.string.mypage_delete_dog_thumbnail_negative) { _, _ -> }
                   builder.show()
               }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isGoogleLoginSuccess.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { state->
                if (state) {
                    viewModel.deleteUser(googleToken)
                }
            }
        }

    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.signInGoogle(idToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val TAG = "GoogleActivity"
    }
}