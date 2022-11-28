package com.cmpt362.fitcheck.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.User

class FriendsAdapter: RecyclerView.Adapter<FriendsAdapter.MyViewHolder>() {

    private val userList = ArrayList<User>()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val email: TextView = itemView.findViewById<TextView>(R.id.user_email)
        val user_id: TextView = itemView.findViewById<TextView>(R.id.user_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_list_display,
            parent,false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        println("debug: onBindViewHolder - $currentItem")
        holder.email.text = "user email - ${currentItem.email}"
        holder.user_id.text = "user id - ${currentItem.uid}"
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}