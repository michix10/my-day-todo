package com.github.naz013.tasker.settings.groups

import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.ItemTouchHelperAdapter
import com.github.naz013.tasker.arch.OnStartDragListener
import com.github.naz013.tasker.data.TaskGroup
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_group_edit.view.*
import java.util.*


/**
 * Copyright 2018 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class GroupsListAdapter : RecyclerView.Adapter<GroupsListAdapter.Holder>(), ItemTouchHelperAdapter {

    companion object {
        const val DELETE = 0
        const val EDIT = 1
        const val ADD = 2
    }

    val items: MutableList<TaskGroup> = mutableListOf()
    var callback: ((TaskGroup, Int) -> Unit)? = null
    var mDragStartListener: OnStartDragListener? = null

    fun setData(data: List<TaskGroup>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_group_edit, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: TaskGroup) {
            itemView.summaryView.text = task.name
        }

        init {
            itemView.onClick { edit(adapterPosition) }
            itemView.deleteView.onClick { delete(adapterPosition) }
            itemView.handleView.setOnTouchListener({ _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener?.onStartDrag(this)
                }
                false
            })
        }
    }

    private fun edit(position: Int) {
        callback?.invoke(items[position], EDIT)
    }

    private fun delete(position: Int) {
        callback?.invoke(items[position], DELETE)
    }
}