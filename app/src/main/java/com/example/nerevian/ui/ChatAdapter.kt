package com.example.nerevian.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R

class ChatAdapter(private val chatMessages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardBot: CardView = view.findViewById(R.id.cardBot)
        val tvBot: TextView = view.findViewById(R.id.tvBotMessage)
        val cardUser: CardView = view.findViewById(R.id.cardUser)
        val tvUser: TextView = view.findViewById(R.id.tvUserMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_bot, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatMessages[position]
        if (message.isUser) {
            holder.cardUser.visibility = View.VISIBLE
            holder.cardBot.visibility = View.GONE
            holder.tvUser.text = message.text
        } else {
            holder.cardUser.visibility = View.GONE
            holder.cardBot.visibility = View.VISIBLE
            holder.tvBot.text = message.text
        }
    }

    override fun getItemCount() = chatMessages.size
}