package com.glittering.youxi.ui.fragment.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.glittering.youxi.databinding.FragmentMeBinding
import com.glittering.youxi.ui.activity.LoginActivity

class MeFragment : Fragment() {
    private var _binding: FragmentMeBinding? = null

    private val binding get() = _binding!!

    companion object {
        val instance: MeFragment by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cardView.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        return root
    }


}