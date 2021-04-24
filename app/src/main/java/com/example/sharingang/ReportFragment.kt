package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sharingang.databinding.FragmentReportBinding
import com.example.sharingang.users.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ReportFragment : Fragment() {

    private val args: ReportFragmentArgs by navArgs()
    private lateinit var binding: FragmentReportBinding
    private lateinit var reporterId: String
    private lateinit var reportedId: String
    private lateinit var reportedUsername: String
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        reporterId = args.reporterId
        reportedId = args.reportedId
        reportedUsername = args.reportedName


        val radioButtons = listOf(
            binding.radioUsername, binding.radioProfilePicture, binding.radioItem, binding.radioOther
        )
        setupRadioButtons(radioButtons)
        setupCancelButton()
        setupButtonOk(radioButtons)
        binding.textReportedUsername.text = "Reporting $reportedUsername"

        return binding.root
    }

    private fun setupRadioButtons(radioButtons: List<RadioButton>) {
        for(radioButton: RadioButton in radioButtons) {
            radioButton.setOnCheckedChangeListener { _, _ ->
                binding.buttonOk.isEnabled =
                    binding.radioUsername.isChecked || binding.radioProfilePicture.isChecked ||
                    binding.radioItem.isChecked || binding.radioOther.isChecked
            }
        }
    }

    private fun setupCancelButton() {
        binding.buttonCancel.setOnClickListener { view ->
            view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToUserProfileFragment(reportedId)
            )
        }
    }

    private fun setupButtonOk(radioButtons: List<RadioButton>) {
        binding.buttonOk.setOnClickListener { view ->
            var reason = ""
            for (radioButton: RadioButton in radioButtons) {
                if (radioButton.isChecked) {
                    reason = getName(radioButton)
                }
            }
            val reportDescription = binding.reportDescription.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val reporterUser = userRepository.get(reporterId)
                val reportedUser = userRepository.get(reportedId)
                userRepository.report(
                    reporterUser = reporterUser!!,
                    reportedUser = reportedUser!!,
                    reason = reason,
                    description = reportDescription,
                )
            }
            view.findNavController().navigate(
                ReportFragmentDirections.actionReportFragmentToUserProfileFragment(
                    reportedId
                )
            )
        }
    }

    private fun getName(radioButton: RadioButton): String {
        return when (radioButton) {
            binding.radioUsername -> "Unappropriate Username"
            binding.radioProfilePicture -> "Unappropriate Profile Picture"
            binding.radioItem -> "Unappropriate Item"
            binding.radioOther -> "Other"
            else -> ""
        }
    }

}