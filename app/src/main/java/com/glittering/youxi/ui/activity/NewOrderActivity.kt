package com.glittering.youxi.ui.activity

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import com.glittering.youxi.data.bean.Order
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
import com.glittering.youxi.data.service.OrderService
import com.glittering.youxi.data.service.ServiceCreator
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
import java.io.FileOutputStream

class NewOrderActivity : BaseActivity<ActivityNewOrderBinding>() {
    override val fitSystemWindows: Boolean
        get() = false
    var photoFile: File? = null

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
                .enqueue(object : retrofit2.Callback<BaseDataResponse<List<Order>>> {
                    override fun onResponse(
                        call: Call<BaseDataResponse<List<Order>>>,
                        response: Response<BaseDataResponse<List<Order>>>
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

                                val drawable = binding.ivAdd.drawable as BitmapDrawable
                                val bitmap = drawable.bitmap
                                photoFile = File(applicationContext.cacheDir, "image.png")
                                photoFile!!.createNewFile()
                                val outputStream = FileOutputStream(photoFile)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                outputStream.flush()
                                outputStream.close()

                                binding.etTitle.setText(order.order_title)
                                binding.etPrice.setText(order.order_price.toString())
                                binding.etDesc.setText(order.order_description)
                            } else ToastFail(response.message())
                        } else ToastFail(getString(R.string.toast_response_error))
                    }

                    override fun onFailure(call: Call<BaseDataResponse<List<Order>>>, t: Throwable) {
                        t.printStackTrace()
                        ToastFail(t.toString())
                    }
                })
            binding.btnPublish.text = "保存"
            binding.toolbar.title = "修改账号资料"
        }

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
                    photoFile = File(URIPathHelper().getPath(this, uri))
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
                if (photoFile == null) {
                    ToastFail("请添加图片")
                    return@setOnClickListener
                }
                val orderService = ServiceCreator.create<OrderService>()
                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "picture",
                        photoFile!!.name,
                        RequestBody.create(
                            MediaType.parse("image/*"),
                            photoFile!!
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
                    .enqueue(object : retrofit2.Callback<BaseResponse> {
                        override fun onResponse(
                            call: Call<BaseResponse>,
                            response: Response<BaseResponse>
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
                            call: Call<BaseResponse>,
                            t: Throwable
                        ) {
                            t.printStackTrace()
                            ToastFail(t.toString())
                        }

                    })
            } else {
                //编辑
                if (photoFile == null) {
                    ToastFail("请添加图片")
                    return@setOnClickListener
                }
                //TODO: waiting for check
                val orderService = ServiceCreator.create<OrderService>()
                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "picture",
                        photoFile!!.name,
                        RequestBody.create(
                            MediaType.parse("image/*"),
                            photoFile!!
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
                    .addFormDataPart("order_id", orderId.toString()).build()
                orderService.updateOrder(requestBody)
                    .enqueue(object : retrofit2.Callback<BaseResponse> {
                        override fun onResponse(
                            call: Call<BaseResponse>, response: Response<BaseResponse>
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
                            call: Call<BaseResponse>, t: Throwable
                        ) {
                            t.printStackTrace()
                            ToastFail(t.toString())
                        }

                    })
            }
        }
    }
}