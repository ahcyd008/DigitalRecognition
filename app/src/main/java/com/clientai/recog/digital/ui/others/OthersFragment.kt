package com.clientai.recog.digital.ui.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.clientai.recog.digital.databinding.FragmentOthersBinding

class OthersFragment : Fragment() {

    private var _binding: FragmentOthersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(OthersViewModel::class.java)

        _binding = FragmentOthersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textOthers
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}