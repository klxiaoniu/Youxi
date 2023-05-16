package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.EmailRequest
import com.glittering.youxi.data.EmailResponse
import com.glittering.youxi.data.PersonalInfo
import com.glittering.youxi.data.PersonalInfoResponse
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UpdatingResponse
import com.glittering.youxi.data.UserService
import com.glittering.youxi.databinding.ActivityProfileUpdateBinding
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.URIPathHelper
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileUpdateActivity : BaseActivity<ActivityProfileUpdateBinding>() {
    lateinit var viewModel: ProfileUpdateViewModel
    lateinit var userInfo: PersonalInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reverseColorIfDark(listOf(binding.back))
        binding.back.setOnClickListener { finish() }
        viewModel = ViewModelProvider(this).get(ProfileUpdateViewModel::class.java)
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    val photoBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    viewModel.image =
                        File(URIPathHelper().getPath(this, uri))
                    binding.ivAvatar.setImageBitmap(photoBmp)
                    Log.d("PhotoPicker", "image: " + viewModel.image)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    //ToastShort("未选择图片")
                }
            }
        binding.chooseIcon.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.cardView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnSendCode.setOnClickListener {
            val userService = ServiceCreator.create<UserService>()
            val emailRequest = EmailRequest(
                binding.email.text.toString()
            )
            val json = FormBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(emailRequest)
            )
            userService.queryEmailCode(json).enqueue(object : Callback<EmailResponse> {
                override fun onResponse(
                    call: Call<EmailResponse>,
                    response: Response<EmailResponse>
                ) {
                    val res = response.body()
                    ToastSuccess(res?.message.toString())
                }

                override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastFail(t.toString())
                }
            })
        }
        binding.submit.setOnClickListener {
            val userService = ServiceCreator.create<UserService>()
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "photo",
                    viewModel.image?.name,
                    if (viewModel.image != null) RequestBody.create(
                        MediaType.parse("image/*"),
                        viewModel.image!!
                    ) else RequestBody.create(
                        MediaType.parse("image/*"),
                        ""  //空文件
                    )
                )
                .addFormDataPart("name", binding.nickname.text.toString())
                .addFormDataPart("real_name", binding.name.text.toString())
                .addFormDataPart(
                    "email",
                    binding.email.text.toString().let { if (it == userInfo.email) "" else it })
                .addFormDataPart("ecode", binding.ecode.text.toString())
                .build()
            userService.updating(requestBody).enqueue(object : Callback<UpdatingResponse> {
                override fun onResponse(
                    call: Call<UpdatingResponse>,
                    response: Response<UpdatingResponse>
                ) {
                    val res = response.body()
                    if (res != null) {
                        if (res.code == 200) {
                            ToastSuccess(res.message)
                            finish()
                        } else ToastFail(res.message)
                    } else {
                        ToastFail(getString(R.string.toast_response_error))
                    }
                }

                override fun onFailure(call: Call<UpdatingResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastFail(t.toString())
                }
            })
        }

        val userService = ServiceCreator.create<UserService>()
        userService.getPersonalInfo().enqueue(object : Callback<PersonalInfoResponse> {
            override fun onResponse(
                call: Call<PersonalInfoResponse>,
                response: Response<PersonalInfoResponse>
            ) {
                val res = response.body()
                if (res?.code == 200) {
                    userInfo = res.data[0]
                    binding.nickname.setText(userInfo.name)
                    binding.name.setText(userInfo.real_name)
                    binding.email.setText(userInfo.email)
                    //binding.ivAvatar.setImageDrawable(DrawableUtil().byteToDrawable(res.data.avatar))
                    val options = RequestOptions()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                    Glide.with(applicationContext)
                        .load(userInfo.photo)
                        .apply(options)
                        .into(binding.ivAvatar)
                } else ToastFail(res?.message.toString())
            }

            override fun onFailure(call: Call<PersonalInfoResponse>, t: Throwable) {
                t.printStackTrace()
                ToastFail(t.toString())
            }
        })
    }
}