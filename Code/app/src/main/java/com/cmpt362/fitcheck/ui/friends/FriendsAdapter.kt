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

class FriendsAdapter(private val friendsStatus: FriendshipStatus?): RecyclerView.Adapter<FriendsAdapter.MyViewHolder>() {

    private val userList = ArrayList<User>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rootView: RelativeLayout = itemView.findViewById(R.id.userListView)
        val email: TextView = itemView.findViewById(R.id.userEmail)
        val user_id: TextView = itemView.findViewById(R.id.userId)
        val firstName: TextView = itemView.findViewById(R.id.userFirstName)
        val lastName: TextView = itemView.findViewById(R.id.userLastName)
        val add_friend_btn: Button = itemView.findViewById<Button>(R.id.add_friend_btn)
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
        // println("debug: onBindViewHolder - $currentItem")
        holder.email.text = "user email - ${targetUser.email}"
        holder.user_id.text = "user id - ${targetUser.uid}"
        holder.firstName.text = "user first name - ${targetUser.firstName}"
        holder.lastName.text = "user last name - ${targetUser.lastName}"

        when (friendsStatus) {
            FriendshipStatus.FRIENDS -> {
                holder.rootView.removeView(holder.add_friend_btn)
            }
            FriendshipStatus.FRIEND_REQUEST_RECEIVED -> {
                holder.add_friend_btn.text = "Accept Request"
                holder.add_friend_btn.setOnClickListener {
                    Firebase.acceptFriendRequest(targetUser.uid!!)
                    // Should automatically remove user from this list through valueEventListener?
                }
            }
            FriendshipStatus.FRIEND_REQUEST_SENT -> {
                holder.add_friend_btn.text = "Request Sent"
                holder.add_friend_btn.isEnabled = false
                holder.add_friend_btn.isClickable = false
            }
            else -> {
                holder.add_friend_btn.setOnClickListener{
                    // println("debug: selected user - $currentItem")
                    // code for sending pending friend request to user

                    Firebase.acceptFriendRequest(targetUser.uid!!)
                    holder.add_friend_btn.text = "Sent"
                    holder.add_friend_btn.isEnabled = false
                    holder.add_friend_btn.isClickable = false
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