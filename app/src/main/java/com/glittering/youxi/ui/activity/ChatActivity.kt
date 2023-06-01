package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.glittering.youxi.MyWebSocketClient
import com.glittering.youxi.R
import com.glittering.youxi.data.MsgAdapter
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.SysMsgAdapter
import com.glittering.youxi.data.SysMsgResponse
import com.glittering.youxi.data.UserService
import com.glittering.youxi.database.MsgDatabase
import com.glittering.youxi.databinding.ActivityChatBinding
import com.glittering.youxi.entity.MsgRecord
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.DrawableUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatId = intent.getLongExtra("chat_id", -1L)
        if (chatId == -1L) {
            finish()
            return
        }

        binding.toolbar.let { it ->
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.inflateMenu(R.menu.chat_menu)
            it.setOnMenuItemClickListener { it2 ->
                when (it2.itemId) {
                    R.id.action_clear -> {
                        val dialog = MaterialAlertDialogBuilder(this)
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
                        DialogUtil.stylize(dialog)
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

        if (chatId == 10000L) {  //系统通知
            binding.bottomView.visibility = View.GONE
            val userService = ServiceCreator.create<UserService>()
            userService.getSysMsg(1).enqueue(object : Callback<SysMsgResponse> {
                override fun onResponse(
                    call: Call<SysMsgResponse>,
                    response: Response<SysMsgResponse>
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

                override fun onFailure(call: Call<SysMsgResponse>, t: Throwable) {
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
                        msgAdapter = MsgAdapter(msgRecordList)
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
                        client.send(Gson().toJson(record))
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
                                client.send(Gson().toJson(record))
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

        }
    }
}