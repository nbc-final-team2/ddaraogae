package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.databinding.FragmentMypageBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.ui.dog.MyPetActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.loading.LoadingDialog
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageFragment : Fragment() {
    private var _binding:FragmentMypageBinding ?= null
    private val binding get() = _binding!!
    private val viewModel : MyPageViewModel by viewModels()

    private var loadingDialog: LoadingDialog? = null

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
        initViewModel()

    }

    private fun clickAboutAccountBtn(){
        binding.tvSignOut.setOnClickListener {
            viewModel.logOut()
        }

        binding.tvSignDelete.setOnClickListener {
            viewModel.deleteUser()
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

    private fun initViewModel() {

        lifecycleScope.launch {
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

        lifecycleScope.launch {
            viewModel.mypageEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { event->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }

        lifecycleScope.launch {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}