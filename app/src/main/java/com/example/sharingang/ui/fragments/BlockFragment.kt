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
import com.example.sharingang.databinding.FragmentBlockBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BlockFragment : Fragment() {

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var binding: FragmentBlockBinding
    private val args: BlockFragmentArgs by navArgs()
    private lateinit var blockerId: String
    private lateinit var blockedId: String
    private lateinit var blockedName: String

    private lateinit var reasonNameMap: HashMap<Int, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlockBinding.inflate(inflater, container, false)
        blockerId = args.blockerId
        blockedId = args.blockedId
        blockedName = args.blockedName
        binding.textBlockedUsername.text = blockedName
        reasonNameMap = hashMapOf(
            binding.radioRude.id to getString(R.string.rude),
            binding.radioScam.id to getString(R.string.scamming),
            binding.radioOther.id to getString(R.string.eng_other)
        )
        setupRadioButtonsListener()
        setupCancelButton()
        setupOkButton()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupRadioButtonsListener() {
        binding.blockRadioGroup.setOnCheckedChangeListener { _, _ ->
            binding.buttonOk.isEnabled = true
        }
    }

    private fun setupCancelButton() {
        binding.buttonCancel.setOnClickListener { view ->
            view.findNavController().navigate(
                BlockFragmentDirections
                    .actionBlockFragmentToUserProfileFragment(blockedId)
            )
        }
    }

    private fun setupOkButton() {
        binding.buttonOk.setOnClickListener { view ->
            val checkedGroup = binding.blockRadioGroup.checkedRadioButtonId
            val reason = reasonNameMap[checkedGroup] ?: ""

            val blockDescription = binding.blockDescription.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                userRepository.block(
                    blockerId = blockerId,
                    blockedId = blockedId,
                    reason = reason,
                    description = blockDescription,
                )
            }
            view.findNavController().navigate(
                BlockFragmentDirections.actionBlockFragmentToUserProfileFragment(blockedId)
            )
        }
    }
}