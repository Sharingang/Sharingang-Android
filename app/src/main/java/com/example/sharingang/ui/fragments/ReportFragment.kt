package com.example.sharingang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sharingang.R
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.databinding.FragmentReportBinding
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

        setupRadioButtons()
        setupCancelButton()
        setupButtonOk()
        val reportingText = "${getString(R.string.eng_reporting)} $reportedUsername"
        binding.textReportedUsername.text = reportingText

        return binding.root
    }

    private fun setupRadioButtons() {
        binding.reportRadioGroup.setOnCheckedChangeListener { _, _ ->
            binding.buttonOk.isEnabled = true
        }
    }

    private fun setupCancelButton() {
        binding.buttonCancel.setOnClickListener { view ->
            view.findNavController().navigate(
                ReportFragmentDirections.actionReportFragmentToUserProfileFragment(reportedId)
            )
        }
    }

    private fun setupButtonOk() {
        binding.buttonOk.setOnClickListener { view ->
            val checkedGroup = binding.reportRadioGroup.checkedRadioButtonId
            val reason = getReasonName(checkedGroup)

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

    private fun getReasonName(radioButtonId: Int): String {
        return when (radioButtonId) {
            binding.radioUsername.id -> getString(R.string.eng_inappropriate_username)
            binding.radioProfilePicture.id -> getString(R.string.eng_inappropriate_profile_picture)
            binding.radioItem.id -> getString(R.string.eng_inappropriate_item)
            binding.radioOther.id -> getString(R.string.eng_other)
            else -> ""
        }
    }
}
