package com.glittering.youxi.ui.activity

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.glittering.youxi.MyBroadcastReceiver
import com.glittering.youxi.MyWebSocketClient
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.PersonalInfo
import com.glittering.youxi.data.bean.SysMsg
import com.glittering.youxi.data.bean.UserInfo
import com.glittering.youxi.data.request.ReportUserRequest
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.database.MsgDatabase
import com.glittering.youxi.databinding.ActivityChatBinding
import com.glittering.youxi.entity.MsgRecord
import com.glittering.youxi.ui.adapter.MsgAdapter
import com.glittering.youxi.ui.adapter.SysMsgAdapter
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.DrawableUtil
import com.glittering.youxi.utils.RequestUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.getToken
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.fitsTitleBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import kotlin.concurrent.thread


class ChatActivity : BaseActivity<ActivityChatBinding>() {
    override val fitSystemWindows: Boolean
        get() = false
    override val keyboardEnable: Boolean
        get() = true

    var sysMsgAdapter: SysMsgAdapter? = null
    var msgAdapter: MsgAdapter? = null
    lateinit var client: MyWebSocketClient
    var otherAvatar: MutableLiveData<Drawable> = MutableLiveData()
    var selfAvatar: MutableLiveData<Drawable> = MutableLiveData()

    private val broadcastReceiver = MyBroadcastReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatId = intent.getLongExtra("chat_id", -1L)
        if (chatId == -1L) {
            finish()
            return
        }

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.inflateMenu(R.menu.chat_menu)
            it.setOnMenuItemClickListener { it2 ->
                when (it2.itemId) {
                    R.id.action_clear -> {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("清空聊天记录")
                            .setMessage("确定要清空聊天记录吗？")
                            .setPositiveButton("确定") { _, _ ->
                                thread {
                                    MsgDatabase.getDatabase().msgRecordDao()
                                        .deleteMsgRecordByChatId(chatId)
                                    runOnUiThread {
                                        msgAdapter?.setAdapterList(emptyList())
                                        ToastSuccess("聊天记录已清空")
                                    }
                                }
                            }
                            .setNegativeButton("取消", null)
                            .show()
                            .let {
                                DialogUtil.stylize(it)
                            }
                        true
                    }

                    R.id.report_user -> {
                        val view = layoutInflater.inflate(R.layout.dialog_report_user, null)
                        MaterialAlertDialogBuilder(this)
                            .setTitle("举报该用户")
                            .setView(view)
                            .setPositiveButton("确定") { _, _ ->
                                val userService = ServiceCreator.create<UserService>()
                                val json = RequestUtil.generateJson(
                                    ReportUserRequest(
                                        chatId,
                                        view.findViewById<EditText>(R.id.et_info).text.toString()
                                    )
                                )
                                userService.reportUser(json)
                                    .enqueue(object : Callback<BaseResponse> {
                                        override fun onResponse(
                                            call: Call<BaseResponse>,
                                            response: Response<BaseResponse>
                                        ) {
                                            if (response.body() != null) {
                                                if (response.body()!!.code == 200) {
                                                    ToastSuccess(response.body()!!.message)
                                                } else {
                                                    ToastFail(response.body()!!.message)
                                                }
                                            } else {
                                                ToastFail(getString(R.string.toast_response_error))
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<BaseResponse>,
                                            t: Throwable
                                        ) {
                                            ToastFail(getString(R.string.toast_response_error))
                                        }
                                    })
                            }
                            .setNegativeButton("取消", null)
                            .show()
                            .let {
                                DialogUtil.stylize(it)
                            }
                        true
                    }

                    else -> false
                }
            }
            fitsTitleBar(it)
        }
        binding.bottomView.setPadding(0, 0, 0, ImmersionBar.getNavigationBarHeight(this))

        //connect websocket
        val uri: URI? = URI.create("ws://SERVER_ADDRESS:SERVER_PORT/chat/send")
        client = object : MyWebSocketClient(uri) {}
        client.connectBlocking()

        val filter = IntentFilter("com.glittering.youxi.NEW_MSG")
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
        val chatInteraction: MyBroadcastReceiver.ChatInteraction =
            object : MyBroadcastReceiver.ChatInteraction {
                override fun onNewMsg(chat_id: Long) {
                    if (chat_id == chatId) {
                        thread {
                            val msgRecordDao = MsgDatabase.getDatabase().msgRecordDao()
                            val msg = msgRecordDao.loadLastMsgRecord(chatId)!!
                            runOnUiThread {
                                msgAdapter?.plusAdapterList(listOf(msg))
                                binding.recyclerview.smoothScrollToPosition(msgAdapter!!.itemCount)
                            }
                        }
                    }
                }
            }
        broadcastReceiver.setChatInteractionListener(chatInteraction)

        if (chatId == 10000L) {  //系统通知
            binding.titleUsername.text = "游兮小助手"
            binding.bottomView.visibility = View.GONE
            binding.toolbar.menu.clear()
            val userService = ServiceCreator.create<UserService>()
            userService.getSysMsg(1).enqueue(object : Callback<BaseDataResponse<List<SysMsg>>> {
                override fun onResponse(
                    call: Call<BaseDataResponse<List<SysMsg>>>,
                    response: Response<BaseDataResponse<List<SysMsg>>>
                ) {
                    val code = response.body()?.code
                    if (code == 200) {
                        val data = response.body()?.data!!
                        if (sysMsgAdapter == null) {
                            sysMsgAdapter = SysMsgAdapter(data)
                            val layoutManager = LinearLayoutManager(applicationContext)
                            layoutManager.orientation = LinearLayoutManager.VERTICAL
                            binding.recyclerview.layoutManager = layoutManager
                            binding.recyclerview.adapter = sysMsgAdapter
                        } else {
                            sysMsgAdapter!!.plusAdapterList(data)
                        }
                    } else {
                        ToastFail(getString(R.string.toast_response_error))
                    }
                }

                override fun onFailure(call: Call<BaseDataResponse<List<SysMsg>>>, t: Throwable) {
                    t.printStackTrace()
                    ToastFail(getString(R.string.toast_response_error))
                }
            })
        } else {

            val msgRecordDao = MsgDatabase.getDatabase().msgRecordDao()
            thread {
                val msgRecordList = msgRecordDao.loadMsgRecord(chatId)
                runOnUiThread {
                    if (msgAdapter == null) {
                        msgAdapter = MsgAdapter(msgRecordList, otherAvatar, selfAvatar)
                        val layoutManager = LinearLayoutManager(applicationContext)
                        layoutManager.orientation = LinearLayoutManager.VERTICAL
                        binding.recyclerview.layoutManager = layoutManager
                        binding.recyclerview.adapter = msgAdapter
                    } else {
                        msgAdapter!!.plusAdapterList(msgRecordList)
                    }
                    binding.recyclerview.scrollToPosition(msgAdapter!!.itemCount - 1)
                }
            }

            binding.bottomSendButton.setOnClickListener {
                if (binding.editText.text.isEmpty()) return@setOnClickListener
                thread {
                    val record = MsgRecord(
                        chatId,
                        1,
                        0,
                        binding.editText.text.toString(),
                        System.currentTimeMillis()
                    )
                    try {
//                        client.send(Gson().toJson(record))
                        msgRecordDao.insertMsgRecord(record)
                        val msg = msgRecordDao.loadLastMsgRecord(chatId)!!
                        runOnUiThread {
                            binding.editText.setText("")
                            msgAdapter?.plusAdapterList(listOf(msg))
                            binding.recyclerview.smoothScrollToPosition(msgAdapter!!.itemCount)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            ToastFail("发送失败，请稍后重试")
                        }
                    }
                }
            }

            val pickMedia =
                registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        val dialog = MaterialAlertDialogBuilder(this)
                            .setMessage("正在发送，请稍候")
                            .setCancelable(false)
                            .show()
                        thread {
                            val photoStr: String
                            try {
                                val photoBmp =
                                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                                photoStr = DrawableUtil.bitmapShrinkToBase64(photoBmp)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ToastFail("读取图片失败，请稍后重试")
                                return@thread
                            }
                            try {
                                val record = MsgRecord(
                                    chatId,
                                    1,
                                    1,
                                    photoStr,
                                    System.currentTimeMillis()
                                )
//                                client.send(Gson().toJson(record))
                                msgRecordDao.insertMsgRecord(record)
                                runOnUiThread {
                                    binding.editText.setText("")
                                    msgAdapter?.plusAdapterList(listOf(record))
                                    binding.recyclerview.smoothScrollToPosition(msgAdapter!!.itemCount)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                runOnUiThread {
                                    ToastFail("发送失败，请稍后重试")
                                }
                            }
                            dialog.dismiss()
                        }

                    }
                }
            binding.bottomImage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            binding.bottomSendButton.setOnLongClickListener {
                thread {
                    val record = MsgRecord(
                        chatId,
                        0,
                        0,
                        "测试消息",
                        System.currentTimeMillis()
                    )
                    MsgDatabase.getDatabase().msgRecordDao().insertMsgRecord(record)

                    val intentBr = Intent("com.glittering.youxi.NEW_MSG")
                        .putExtra("chat_id", chatId)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentBr)

                }
                true
            }
//
//            binding.bottomSendButton.setOnLongClickListener {
//                thread {
//                    val record = MsgRecord(
//                        chatId,
//                        0,
//                        0,
//                        binding.editText.text.toString(),
//                        System.currentTimeMillis()
//                    )
//                    msgRecordDao.insertMsgRecord(record)
//                    runOnUiThread {
//                        binding.editText.setText("")
//                        msgAdapter?.plusAdapterList(listOf(record))
//                        binding.recyclerview.smoothScrollToPosition(msgAdapter!!.itemCount)
//                    }
//                }
//                true
//            }

            val userService = ServiceCreator.create<UserService>()
            userService.getUserInfo(chatId)
                .enqueue(object : Callback<BaseDataResponse<List<UserInfo>>> {
                    override fun onResponse(
                        call: Call<BaseDataResponse<List<UserInfo>>>,
                        response: Response<BaseDataResponse<List<UserInfo>>>
                    ) {
                        val code = response.body()?.code
                        if (code == 200) {
                            val data = response.body()?.data!![0]
                            binding.titleUsername.text = data.name
                            val options = RequestOptions()
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                            Glide.with(applicationContext)
                                .load(data.photo)
                                .apply(options)
                                .placeholder(R.drawable.ic_default_avatar)
                                .into(object : DrawableImageViewTarget(binding.ivAvatar) {
                                    override fun onResourceReady(
                                        resource: Drawable,
                                        transition: Transition<in Drawable>?
                                    ) {
                                        super.onResourceReady(resource, transition)
                                        binding.ivAvatar.setImageDrawable(resource)
                                        otherAvatar.value = resource
                                    }
                                })
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseDataResponse<List<UserInfo>>>,
                        t: Throwable
                    ) {
                        t.printStackTrace()
                    }
                })

            if (getToken() != "") {
                userService.getPersonalInfo()
                    .enqueue(object : Callback<BaseDataResponse<List<PersonalInfo>>> {
                        override fun onResponse(
                            call: Call<BaseDataResponse<List<PersonalInfo>>>,
                            response: Response<BaseDataResponse<List<PersonalInfo>>>
                        ) {
                            val res = response.body()
                            if (res?.code == 200) {
                                val data = res.data[0]
                                val options = RequestOptions()
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .error(R.drawable.ic_default_avatar)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                Glide.with(applicationContext)
                                    .load(data.photo)
                                    .apply(options)
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .into(object : SimpleTarget<Drawable>() {
                                        override fun onResourceReady(
                                            resource: Drawable,
                                            transition: Transition<in Drawable>?
                                        ) {
                                            selfAvatar.value = resource
                                        }
                                    })
                            }
                        }

                        override fun onFailure(
                            call: Call<BaseDataResponse<List<PersonalInfo>>>,
                            t: Throwable
                        ) {
                            t.printStackTrace()
                        }
                    })
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}