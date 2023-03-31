package com.glittering.youxi.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glittering.youxi.ui.activity.LoginActivity
import com.glittering.youxi.databinding.FragmentHomeBinding
import com.glittering.youxi.ui.activity.IntroActivity
import com.glittering.youxi.ui.activity.ProfileUpdateActivity
import com.hjq.toast.Toaster

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    companion object {
        val instance: HomeFragment by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HomeFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val btnLogin: Button = binding.btnLogin
        val btnIntro: Button = binding.btnIntro
        val btnProfile: Button = binding.btnProfile
        val btnToast: Button = binding.btnToast
        btnLogin.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        btnIntro.setOnClickListener {
            val intent = Intent(context, IntroActivity::class.java)
            startActivity(intent)
        }
        btnProfile.setOnClickListener {
            val intent = Intent(context, ProfileUpdateActivity::class.java)
            startActivity(intent)
        }
        btnToast.setOnClickListener {
            Toaster.show("操作成功!")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}