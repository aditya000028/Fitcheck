package com.cmpt362.fitcheck.ui.friends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.Util
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.calendar.DetailActivity
import com.cmpt362.fitcheck.ui.friends.viewModels.ItemsViewModel


class RecyclerViewAdapter(private val mList: MutableList<ItemsViewModel>, private  val context: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        Firebase.getFriendsPhoto(ItemsViewModel.uid, ItemsViewModel.year, ItemsViewModel.month, ItemsViewModel.day, holder.imageView, context)

        // Set date text
        val dateStr = Util.convertMonthIntToString(ItemsViewModel.month) + " ${ItemsViewModel.day}, ${ItemsViewModel.year}"
        holder.dateView.text = dateStr

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.USER_ID_KEY, ItemsViewModel.uid)
            intent.putExtra(DetailActivity.YEAR_KEY, ItemsViewModel.year)
            intent.putExtra(DetailActivity.MONTH_KEY, ItemsViewModel.month -1)
            intent.putExtra(DetailActivity.DAY_KEY, ItemsViewModel.day)
            intent.putExtra(DetailActivity.USER_FULL_NAME_KEY, ItemsViewModel.userName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for image and date text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.outfitImage)
        val dateView: TextView = itemView.findViewById(R.id.textDate)
    }

}