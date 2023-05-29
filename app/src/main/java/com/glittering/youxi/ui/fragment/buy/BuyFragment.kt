package com.glittering.youxi.ui.fragment.buy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glittering.youxi.databinding.FragmentBuyBinding
import com.glittering.youxi.ui.activity.SearchActivity

class BuyFragment : Fragment() {

    private var _binding: FragmentBuyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        val instance: BuyFragment by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BuyFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val buyViewModel =
            ViewModelProvider(this).get(BuyViewModel::class.java)

        _binding = FragmentBuyBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val intent = Intent(requireActivity(), SearchActivity::class.java)
                intent.putExtra("key", binding.editTextSearch.text.toString())
                startActivity(intent)
            }
            true
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}