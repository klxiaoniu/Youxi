package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.glittering.youxi.data.*
import com.glittering.youxi.databinding.ActivityProfileUpdateBinding
import com.glittering.youxi.utils.URIPathHelper
import com.google.gson.Gson
import com.xiaoniu.fund.ToastShort
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProfileUpdateViewModel::class.java)
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    val photoBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    viewModel.image =
                        File(URIPathHelper().getPath(this, uri))
                    Log.d("PhotoPicker", "image: " + viewModel.image)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    //ToastShort("未选择图片")
                }
            }
        binding.chooseIcon.setOnClickListener {
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
                    ToastShort(res?.message.toString())
                }

                override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastShort(t.toString())
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
                .addFormDataPart("email", binding.email.text.toString())
                .addFormDataPart("ecode", binding.ecode.text.toString())
                .build()
            userService.updating(requestBody).enqueue(object : Callback<UpdatingResponse> {
                override fun onResponse(
                    call: Call<UpdatingResponse>,
                    response: Response<UpdatingResponse>
                ) {
                    val res = response.body()
                    ToastShort(res?.message.toString())
                }

                override fun onFailure(call: Call<UpdatingResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastShort(t.toString())
                }
            })
        }
    }
}