package com.glittering.youxi.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.glittering.youxi.R
import com.glittering.youxi.entity.MsgRecord
import com.glittering.youxi.utils.DrawableUtil.Companion.base64ToDrawable
import com.glittering.youxi.utils.ToastInfo

class MsgAdapter(var list: List<MsgRecord>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_OTHER: Int = 0
    val TYPE_ME: Int = 1
    val TYPE_SYSTEM: Int = 2
    var typeItem = TYPE_OTHER

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cv = view.findViewById<CardView>(R.id.cardView_msg)
        val item = view
    }

    inner class FootViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_msg = itemView.findViewById<TextView>(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (typeItem) {
            TYPE_OTHER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_other, parent, false)
                ItemViewHolder(view)
            }

            TYPE_ME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_me, parent, false)
                ItemViewHolder(view)
            }

            else -> {
                //TYPE_SYSTEM
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_system, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            when (list[position].msgType) {
                0 -> {
                    LayoutInflater.from(holder.item.context).inflate(R.layout.item_chat_msg_text,holder.item as ViewGroup,false).let {
                        it.findViewById<TextView>(R.id.msg_tv).text=list[position].content
                        holder.cv.addView(it)
                    }
                }
                1 -> {
                    LayoutInflater.from(holder.item.context).inflate(R.layout.item_chat_msg_img,holder.item as ViewGroup,false).let {
                        it.findViewById<ImageView>(R.id.msg_iv).setImageDrawable(base64ToDrawable(list[position].content))
                        holder.cv.addView(it)
                    }
                }
            }
        } else if (holder is FootViewHolder) {
            holder.tv_msg.visibility = if (itemCount == 1) View.GONE else View.VISIBLE
            //无项目时（往往正加载）不显示footview

            //当点击footview时，将该事件回调出去
            holder.tv_msg.setOnClickListener {
                footViewClickListener.invoke()
            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterList(list2: List<MsgRecord>) {
        list = list2
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: List<MsgRecord>) {
        list = list.plus(list2)
        notifyItemRangeInserted(list.size - list2.size, list2.size)
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        typeItem = list[position].senderType
        return typeItem
    }

    var footviewPosition = 0
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (footviewPosition == holder.adapterPosition) {
            return
        }
        if (holder is FootViewHolder) {
            footviewPosition = holder.adapterPosition
            //回调查询事件
            footViewAttachedToWindowListener.invoke()
        }
    }


    //定义footview附加到Window上时的回调
    private var footViewAttachedToWindowListener: () -> Unit = { }
    fun setOnFootViewAttachedToWindowListener(pListener: () -> Unit) {
        this.footViewAttachedToWindowListener = pListener
    }

    //定义footview点击时的回调
    private var footViewClickListener: () -> Unit = { ToastInfo("已无更多") }  //如果是默认，说明不是分页查询
    fun setOnFootViewClickListener(pListener: () -> Unit) {
        this.footViewClickListener = pListener
    }
}