package com.glittering.youxi.ui.fragment.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.PersonalInfoResponse
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UserService
import com.glittering.youxi.databinding.FragmentMeBinding
import com.glittering.youxi.ui.activity.DebugActivity
import com.glittering.youxi.ui.activity.LoginActivity
import com.glittering.youxi.ui.activity.SettingActivity
import com.glittering.youxi.utils.DarkUtil.Companion.reverseColorIfDark
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.applicationContext
import com.glittering.youxi.utils.getToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        reverseColorIfDark(listOf(binding.icSetting))
        binding.userinfo.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.icSetting.setOnLongClickListener {
            val intent = Intent(context, DebugActivity::class.java)
            startActivity(intent)
            true
        }
        binding.icSetting.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
    }

    private fun updateUserInfo() {
        if (getToken() != "") {
            val userService = ServiceCreator.create<UserService>()
            userService.getPersonalInfo().enqueue(object : Callback<PersonalInfoResponse> {
                override fun onResponse(
                    call: Call<PersonalInfoResponse>,
                    response: Response<PersonalInfoResponse>
                ) {
                    val res = response.body()
                    if (res?.code == 200) {
                        val userInfo = res.data[0]
                        binding.tvNickname.text = userInfo.name
                        binding.tvSignature.text = "欢迎来到游兮"
                        val options = RequestOptions()
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.error)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                        Glide.with(applicationContext)
                            .load(userInfo.photo)
                            .apply(options)
                            .into(binding.ivAvatar)

                        binding.userinfo.setOnClickListener {
//                            val intent = Intent(context, LoginActivity::class.java)
//                            startActivity(intent)
                            //TODO:跳转到个人页面
                            ToastSuccess("跳转到个人页面")
                        }

                    } else ToastFail(res?.message.toString())
                }

                override fun onFailure(call: Call<PersonalInfoResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastFail(applicationContext.getString(R.string.toast_response_error))
                }
            })
        } else {
            binding.ivAvatar.setImageResource(R.drawable.ic_default_avatar)
            binding.tvNickname.text = "请登录"
            binding.tvSignature.text = "享受更多功能"
        }
    }


}