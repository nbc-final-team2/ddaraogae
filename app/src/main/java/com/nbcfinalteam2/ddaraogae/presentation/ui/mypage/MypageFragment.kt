package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentMypageBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.ui.add.AddActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.dog.DetailPetActivity
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
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(R.string.msg_logout)
            builder.setPositiveButton(R.string.mypage_delete_dog_thumbnail_positive) { _, _ ->
                viewModel.logOut()
            }
            builder.setNegativeButton(R.string.mypage_delete_dog_thumbnail_negative) { _, _ -> }
            builder.show()
        }

        binding.tvSignDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.msg_delete_account)
            builder.setMessage(R.string.msg_delete_account_context)
            builder.setPositiveButton(R.string.mypage_delete_dog_thumbnail_positive) { _, _ ->
                viewModel.deleteUser()
            }
            builder.setNegativeButton(R.string.mypage_delete_dog_thumbnail_negative) { _, _ -> }
            builder.show()

        }
    }
    private fun clickAboutPetBtn(){
        binding.tvMyDogAdd.setOnClickListener {
            startActivity(Intent(requireActivity(), AddActivity::class.java))
        }
        binding.tvMyDogEdit.setOnClickListener {
            startActivity(Intent(requireActivity(), DetailPetActivity::class.java))
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