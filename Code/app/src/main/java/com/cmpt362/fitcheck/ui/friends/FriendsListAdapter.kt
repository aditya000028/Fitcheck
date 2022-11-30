package com.cmpt362.fitcheck.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.User

class FriendsListAdapter(private val friendsStatus: FriendshipStatus?):
    RecyclerView.Adapter<FriendsListAdapter.MyViewHolder>() {

    private val userList = ArrayList<User>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rootView: RelativeLayout = itemView.findViewById(R.id.userListView)
        val email: TextView = itemView.findViewById(R.id.userEmail)
        val userId: TextView = itemView.findViewById(R.id.userId)
        val firstName: TextView = itemView.findViewById(R.id.userFirstName)
        val lastName: TextView = itemView.findViewById(R.id.userLastName)
        val friendButton: Button = itemView.findViewById<Button>(R.id.add_friend_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_list_display,
            parent,false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val targetUser: User = userList[position]
        holder.email.text = "user email - ${targetUser.email}"
        holder.userId.text = "user id - ${targetUser.uid}"
        holder.firstName.text = "user first name - ${targetUser.firstName}"
        holder.lastName.text = "user last name - ${targetUser.lastName}"

        when (friendsStatus) {
            FriendshipStatus.FRIENDS -> {
                holder.rootView.removeView(holder.friendButton)
            }
            FriendshipStatus.FRIEND_REQUEST_RECEIVED -> {
                holder.friendButton.text = "Accept Request"
                holder.friendButton.setOnClickListener {
                    Firebase.acceptFriendRequest(targetUser.uid!!)
                }
            }
            FriendshipStatus.FRIEND_REQUEST_SENT -> {
                holder.friendButton.text = "Request Sent"
                holder.friendButton.isEnabled = false
                holder.friendButton.isClickable = false
            }
            else -> {
                holder.friendButton.setOnClickListener{
                    Firebase.sendFriendRequest(targetUser.uid!!)
                    holder.friendButton.text = "Sent"
                    holder.friendButton.isEnabled = false
                    holder.friendButton.isClickable = false
                }
            }
        }
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