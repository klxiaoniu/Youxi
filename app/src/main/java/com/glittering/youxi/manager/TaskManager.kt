package com.glittering.youxi.manager

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class TaskManager {
    open class TaskListener {
        open fun onTaskSuccess() {}
        open fun onTaskFail() {}
    }

    private var taskCount: MutableLiveData<Int> = MutableLiveData(0)
    private var status = -1   //-1: not started, 0: normal, 1: fail

    fun add() {
        if (status == 1) return
        status = 0
        taskCount.value = taskCount.value?.plus(1)
        Log.d("TaskMan", "addTaskCount: ${taskCount.value}")
    }

    fun remove() {
        if (status == 1) return
        status = 0
        taskCount.value = taskCount.value?.minus(1)
        Log.d("TaskMan", "minusTaskCount: ${taskCount.value}")
    }

    fun fail() {
        status = 1
        taskCount.value = 0
        Log.d("TaskMan", "fail: ${taskCount.value}")
    }

    fun setTaskListener(owner: LifecycleOwner, listener: TaskListener) {
        taskCount.observe(owner) {
            if (it == 0) {
                when (status) {
                    0 -> listener.onTaskSuccess()
                    1 -> listener.onTaskFail()
                }
            } else if (it < 0) {
                throw Exception("taskCount < 0, please check your code")
            }
        }
    }
}