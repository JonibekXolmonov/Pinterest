package com.example.pinterest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pinterest.R
import com.example.pinterest.model.topic.Topic

class TopicAdapter : RecyclerView.Adapter<TopicAdapter.VH>() {

    private val topics: ArrayList<Topic> = ArrayList()
    lateinit var topicClick: ((Topic, Int) -> Unit)

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvTopic: TextView = view.findViewById(R.id.tvTopic)

        fun bind(topic: Topic, position: Int) {
            if (position == 0) {
                tvTopic.text = "All"
            } else {
                tvTopic.text = topic.title
            }
            tvTopic.setOnClickListener {
                topicClick.invoke(topic, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_topic, parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(topics[position], position)
    }

    override fun getItemCount(): Int = topics.size

    fun submitData(list: List<Topic>) {
        topics.addAll(list)
        notifyDataSetChanged()
    }
}