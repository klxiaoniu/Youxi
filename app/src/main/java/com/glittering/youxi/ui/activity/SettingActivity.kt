package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivitySettingBinding
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.UserStateUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.ktx.fitsTitleBar

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    override val fitSystemWindows = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            fitsTitleBar(it)
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            if (!UserStateUtil.getInstance().isLogin()) {
                findPreference<PreferenceCategory>("account")?.isVisible = false
            }
            findPreference<Preference>("skin")?.onPreferenceChangeListener = this
            findPreference<Preference>("password")?.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), ProfileUpdateActivity::class.java)
                startActivity(intent)
                true
            }
            findPreference<Preference>("realname")?.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), ProfileUpdateActivity::class.java)
                startActivity(intent)
                true
            }
            findPreference<Preference>("about")?.setOnPreferenceClickListener {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("关于游兮")
                    .setMessage("游兮致力于为广大游戏玩家提供一个安全、便捷的游戏交易平台。\n\nMade by Glittering with love")
                    .setPositiveButton("确定", null)
                    .show()
                DialogUtil.stylize(dialog)
                true
            }
            findPreference<Preference>("logout")?.setOnPreferenceClickListener {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定") { _, _ ->
                        UserStateUtil.getInstance().logout()
                        requireActivity().finish()
                    }
                    .setNegativeButton("取消", null)
                    .show()
                DialogUtil.stylize(dialog)
                true
            }
            if (UserStateUtil.getInstance().isAdmin()) {
                findPreference<PreferenceCategory>("management")?.isVisible = true
                findPreference<Preference>("verify")?.setOnPreferenceClickListener {
                    val intent = Intent(requireContext(), VerifyActivity::class.java)
                    startActivity(intent)
                    true
                }
                findPreference<Preference>("exception")?.setOnPreferenceClickListener {
                    val intent = Intent(requireContext(), ExceptionActivity::class.java)
                    startActivity(intent)
                    true
                }
                findPreference<Preference>("user_report")?.setOnPreferenceClickListener {
                    val intent = Intent(requireContext(), ReportHandleActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
        }


        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            AppCompatDelegate.setDefaultNightMode(
                when (newValue) {
                    "0" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    "1" -> AppCompatDelegate.MODE_NIGHT_YES
                    "2" -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
            return true
        }

    }
}