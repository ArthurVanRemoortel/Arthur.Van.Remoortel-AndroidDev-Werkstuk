package com.example.arthurvanremoortel_werkstuk.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthurvanremoortel_werkstuk.databinding.FragmentExploreBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ExploreFragment : Fragment() {

    private lateinit var dashboardViewModel: ExploreViewModel
    private var _binding: FragmentExploreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}