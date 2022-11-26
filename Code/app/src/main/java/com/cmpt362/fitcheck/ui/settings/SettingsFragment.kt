package com.cmpt362.fitcheck.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.ui.authentication.LoginActivity
import com.cmpt362.fitcheck.databinding.FragmentSettingsBinding
import com.cmpt362.fitcheck.firebase.Firebase

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Logging a user out when Logout button is clicked
        binding.root.findViewById<Button>(R.id.logout_button).setOnClickListener(){
            // Logic for logging a user out
            Firebase.signOut()

            // User logged out so end current activity and go back to Login activity
            activity?.finish()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}