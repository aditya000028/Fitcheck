package com.cmpt362.fitcheck.ui.friends

import android.content.Context
import android.content.Intent
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

class FriendsListAdapter(private val friendsStatus: FriendshipStatus?, private val context: Context):
    RecyclerView.Adapter<FriendsListAdapter.MyViewHolder>() {

    private val userList = ArrayList<User>()

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rootView: RelativeLayout = itemView.findViewById(R.id.userListView)
        val email: TextView = itemView.findViewById(R.id.userEmail)
        val firstName: TextView = itemView.findViewById(R.id.userFirstName)
        val lastName: TextView = itemView.findViewById(R.id.userLastName)
        val friendButton: Button = itemView.findViewById(R.id.friend_btn)
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
        holder.email.text = "Email: ${targetUser.email}"
        holder.firstName.text = "First name: ${targetUser.firstName}"
        holder.lastName.text = "Last name: ${targetUser.lastName}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(ProfileActivity.USER_ID_KEY, targetUser.uid)
            context.startActivity(intent)
        }

        when (friendsStatus) {
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
                holder.rootView.removeView(holder.friendButton)
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