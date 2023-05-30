package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.provider.MediaStore
import android.transition.Slide
import android.util.Log
import android.view.Window
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.NewOrderResponse
import com.glittering.youxi.data.OrderInfoResponse
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UpdateOrderResponse
import com.glittering.youxi.databinding.ActivityNewOrderBinding
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.URIPathHelper
import com.gyf.immersionbar.ktx.fitsTitleBar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class NewOrderActivity : BaseActivity<ActivityNewOrderBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.enterTransition = Slide()
        window.exitTransition = Slide()
        super.onCreate(savedInstanceState)

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.close)
            it.setNavigationOnClickListener { finishAfterTransition() }
        }
        fitsTitleBar(binding.toolbar)

        val orderId = intent.getIntExtra("order_id", -1)
        if (orderId != -1) {
            //加载信息，进入编辑模式
            val orderService = ServiceCreator.create<OrderService>()
            orderService.getOrderInfo(orderId)
                .enqueue(object : retrofit2.Callback<OrderInfoResponse> {
                    override fun onResponse(
                        call: Call<OrderInfoResponse>,
                        response: Response<OrderInfoResponse>
                    ) {
                        if (response.body() != null) {
                            println(response.body().toString())
                            if (response.body()!!.code == 200) {
                                val order = response.body()!!.data[0]
                                val option = RequestOptions()
                                    .placeholder(R.drawable.error)
                                    .error(R.drawable.error)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                Glide.with(applicationContext)
                                    .load(order.order_picture)
                                    .apply(option)
                                    .into(binding.ivAdd)
                                binding.etTitle.setText(order.order_title)
                                binding.etPrice.setText(order.order_price.toString())
                                binding.etDesc.setText(order.order_description)
                            } else ToastFail(response.message())
                        } else ToastFail(getString(R.string.toast_response_error))
                    }

                    override fun onFailure(call: Call<OrderInfoResponse>, t: Throwable) {
                        t.printStackTrace()
                        ToastFail(t.toString())
                    }
                })
            binding.btnPublish.text = "保存"
            binding.toolbar.title = "修改账号资料"
        }

        var photoBmp: File? = null
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    binding.ivAdd.setImageBitmap(
                        MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            uri
                        )
                    )
                    photoBmp = File(URIPathHelper().getPath(this, uri))
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    //ToastShort("未选择图片")
                }
            }
        binding.ivAdd.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPublish.setOnClickListener {
            if (orderId == -1) {
                //新建
                if (photoBmp == null) {
                    ToastFail("请添加图片")
                    return@setOnClickListener
                }
                //TODO: waiting for check
                val orderService = ServiceCreator.create<OrderService>()
                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "picture",
                        photoBmp!!.name,
                        RequestBody.create(
                            MediaType.parse("image/*"),
                            photoBmp!!
                        )
                    )
                    .addFormDataPart("title", binding.etTitle.text.toString())
                    .addFormDataPart("description", binding.etDesc.text.toString())
                    .addFormDataPart(
                        "game",
                        binding.etGameName.text.toString()
                    )
                    .addFormDataPart("price", binding.etPrice.text.toString())
                    .addFormDataPart("resources", binding.etAccountInfo.text.toString())
                    .build()
                orderService.newOrder(requestBody)
                    .enqueue(object : retrofit2.Callback<NewOrderResponse> {
                        override fun onResponse(
                            call: Call<NewOrderResponse>,
                            response: Response<NewOrderResponse>
                        ) {
                            val body = response.body()
                            if (body?.code == 200) {
                                ToastSuccess(body.message)
                                finish()
                            } else {
                                if (body != null) {
                                    ToastFail(body.message)
                                } else {
                                    ToastFail(getString(R.string.toast_response_error))
                                }
                            }
                        }

                        override fun onFailure(
                            call: Call<NewOrderResponse>,
                            t: Throwable
                        ) {
                            t.printStackTrace()
                            ToastFail(t.toString())
                        }

                    })
            } else {
                //编辑
                if (photoBmp == null) {
                    ToastFail("请添加图片")
                    return@setOnClickListener
                }
                //TODO: waiting for check
                val orderService = ServiceCreator.create<OrderService>()
                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "picture",
                        photoBmp!!.name,
                        RequestBody.create(
                            MediaType.parse("image/*"),
                            photoBmp!!
                        )
                    )
                    .addFormDataPart("title", binding.etTitle.text.toString())
                    .addFormDataPart("description", binding.etDesc.text.toString())
                    .addFormDataPart(
                        "game",
                        binding.etGameName.text.toString()
                    )
                    .addFormDataPart("price", binding.etPrice.text.toString())
                    .addFormDataPart("resources", binding.etAccountInfo.text.toString())
                    .addFormDataPart("order_id", orderId.toString())
                    .build()
                orderService.updateOrder(requestBody)
                    .enqueue(object : retrofit2.Callback<UpdateOrderResponse> {
                        override fun onResponse(
                            call: Call<UpdateOrderResponse>,
                            response: Response<UpdateOrderResponse>
                        ) {
                            val body = response.body()
                            if (body?.code == 200) {
                                ToastSuccess(body.message)
                                finish()
                            } else {
                                if (body != null) {
                                    ToastFail(body.message)
                                } else {
                                    ToastFail(getString(R.string.toast_response_error))
                                }
                            }
                        }

                        override fun onFailure(
                            call: Call<UpdateOrderResponse>,
                            t: Throwable
                        ) {
                            t.printStackTrace()
                            ToastFail(t.toString())
                        }

                    })
            }
        }
    }
}