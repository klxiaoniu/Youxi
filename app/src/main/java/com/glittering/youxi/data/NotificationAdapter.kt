package com.glittering.youxi.data

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glittering.youxi.R
import com.glittering.youxi.ui.activity.ChatActivity

class NotificationAdapter(var list: List<Notification>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FOOTVIEW: Int = 1 //item类型：footview
    val TYPE_ITEMVIEW: Int = 2 //item类型：itemview
    var typeItem = TYPE_ITEMVIEW

    lateinit var fullList: List<Notification>

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
        Log.d("onBindViewHolder", position.toString())
        if (holder is ItemViewHolder) {
            holder.title.text = list[position].title
            holder.message.text = list[position].message
            holder.time.text = list[position].time.toString()
            holder.avatar.setImageResource(list[position].avatar)   //TODO: 从网络获取头像
            holder.item.setOnClickListener {
                val intent = Intent(it.context, ChatActivity::class.java)
                intent.putExtra("chat_id", list[position].id)
                it.context.startActivity(intent)
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

    fun setAdapterList(list2: List<Notification>) {
        list = list2
        fullList = list
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: List<Notification>) {
        list = list.plus(list2)
        fullList = list
        notifyDataSetChanged()
    }

    fun filter(key: String) {
        list = fullList
        if (key != "")
            list = list.filter {
                it.title.contains(key) or it.message.contains(key)
            }
        notifyDataSetChanged()
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