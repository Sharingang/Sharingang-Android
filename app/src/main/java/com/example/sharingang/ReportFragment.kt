package com.example.sharingang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.sharingang.databinding.FragmentReportBinding


class ReportFragment : Fragment() {

    private val args: ReportFragmentArgs by navArgs()
    private lateinit var binding: FragmentReportBinding
    private lateinit var reporterId: String
    private lateinit var reportedId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container,false)
        reporterId = args.reporterId
        reportedId = args.reportedId

        return binding.root
    }

}