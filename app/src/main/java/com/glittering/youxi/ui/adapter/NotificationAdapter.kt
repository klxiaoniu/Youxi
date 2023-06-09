package com.glittering.youxi.ui.adapter

import android.app.Activity
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.UserInfo
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.database.MsgDatabase
import com.glittering.youxi.database.UserInfoDatabase
import com.glittering.youxi.entity.UserInfoStored
import com.glittering.youxi.ui.activity.ChatActivity
import com.glittering.youxi.utils.applicationContext
import retrofit2.Response
import kotlin.concurrent.thread

class NotificationAdapter(var list: List<Long>, val activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FOOTVIEW: Int = 1 //item类型：footview
    val TYPE_ITEMVIEW: Int = 2 //item类型：itemview
    var typeItem = TYPE_ITEMVIEW

    var fullList: List<Long>

    init {
        fullList = list
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val message: TextView = view.findViewById(R.id.message)
        val time: TextView = view.findViewById(R.id.time)
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        val item = view
    }

    //TODO: footview 增加图片
    inner class FootViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_msg = itemView.findViewById<TextView>(R.id.tv_empty)
        val item = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (typeItem == TYPE_ITEMVIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification_empty, parent, false)
            FootViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.item.setOnClickListener {
                val intent = Intent(it.context, ChatActivity::class.java)
                intent.putExtra("chat_id", list[position])
                it.context.startActivity(intent)
            }
            if (list[position] == 10000L) {
                holder.title.text = "游兮小助手"
                holder.message.text = "点击查看系统通知"
                return
            }
            thread {
                val userId = list[position]
                val userService = ServiceCreator.create<UserService>()
                val response: Response<BaseDataResponse<List<UserInfo>>>
                try {
                    response = userService.getUserInfo(userId).execute()
                } catch (e: Exception) {
                    queryLocalUserInfo(userId, holder)
                    return@thread
                }

                if (response.code() == 200 && response.body()?.code == 200) {
                    val userInfo = response.body()!!.data[0]
                    activity.runOnUiThread {
                        holder.title.text = userInfo.name
                        val options = RequestOptions()
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.ic_default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                        Glide.with(applicationContext)
                            .load(userInfo.photo)
                            .apply(options)
                            .into(holder.avatar)
                    }
                    val userInfoStored =
                        UserInfoStored(userId, userInfo.name, userInfo.orders, userInfo.photo)
                    UserInfoDatabase.getDatabase().userInfoDao()
                        .insertUserInfo(userInfoStored)
                } else {
                    queryLocalUserInfo(userId, holder)
                }
            }
            thread {
                val msgRecordDao = MsgDatabase.getDatabase().msgRecordDao()
                val msg = msgRecordDao.loadLastMsgRecord(list[position])
                activity.runOnUiThread {
                    if (msg != null) {
                        holder.message.text =
                            if (msg.msgType == 0) msg.content.trim() else "[图片]"
                        holder.time.text = DateFormat.format("HH:mm", msg.time)
                    }
                }
            }
        } else if (holder is FootViewHolder) {
            holder.item.visibility = if (itemCount != 1) View.GONE else View.VISIBLE
            //无项目时（往往正加载）不显示footview
            if (fullList.isNotEmpty()) {
                holder.tv_msg.text = "搜索无结果"
            } else {
                holder.tv_msg.text = "还没有人找你聊天吖~"
            }
            //当点击footview时，将该事件回调出去
//            holder.tv_msg.setOnClickListener {
//                footViewClickListener.invoke()
//            }
        }
    }

    private fun queryLocalUserInfo(id: Long, holder: ItemViewHolder) {
        thread {
            val userInfoDao = UserInfoDatabase.getDatabase().userInfoDao()
            val userInfoStored = userInfoDao.getUserInfo(id)
            activity.runOnUiThread {
                if (userInfoStored != null) {
                    holder.title.text = userInfoStored.name
                    val options = RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                    Glide.with(applicationContext)
                        .load(userInfoStored.photo)
                        .apply(options)
                        .into(holder.avatar)
                } else {
                    holder.title.text = "游兮用户$id"
                    holder.avatar.setImageResource(R.drawable.ic_default_avatar)
                }
            }
        }
    }

    fun setAdapterList(list2: List<Long>) {
        list = list2
        fullList = list
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: List<Long>) {
        list = list.plus(list2)
        fullList = list
        notifyDataSetChanged()
    }

    fun filter(key: String) {
        if (key != "") {
            thread {
                val msgRecordDao = MsgDatabase.getDatabase().msgRecordDao()
                list = msgRecordDao.searchChatIdContain(key)
//                usernameList.filter { it.second.contains(key) }.forEach {
//                    list = list.plus(it.first)
//                }
//                list.distinct().sorted()
                activity.runOnUiThread {
                    notifyDataSetChanged()
                }
            }
        } else {
            list = fullList
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = list.size + 1

    override fun getItemViewType(position: Int): Int {
        typeItem = if (position == list.size) TYPE_FOOTVIEW else TYPE_ITEMVIEW
        return typeItem
    }

//    var footviewPosition = 0
//    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
//        super.onViewAttachedToWindow(holder)
//        if (footviewPosition == holder.adapterPosition) {
//            return
//        }
//        if (holder is FootViewHolder) {
//            footviewPosition = holder.adapterPosition
//            //回调查询事件
//            footViewAttachedToWindowListener.invoke()
//        }
//    }
//
//
//    //定义footview附加到Window上时的回调
//    private var footViewAttachedToWindowListener: () -> Unit = { }
//    fun setOnFootViewAttachedToWindowListener(pListener: () -> Unit) {
//        this.footViewAttachedToWindowListener = pListener
//    }
//
//    //定义footview点击时的回调
//    private var footViewClickListener: () -> Unit = { ToastShort("已无更多") }  //如果是默认，说明不是分页查询
//    fun setOnFootViewClickListener(pListener: () -> Unit) {
//        this.footViewClickListener = pListener
//    }
}