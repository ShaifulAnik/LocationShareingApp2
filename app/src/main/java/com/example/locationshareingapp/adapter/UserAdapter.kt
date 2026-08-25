package com.example.locationshareingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationshareingapp.databinding.ItemUserBinding
import com.example.locationshareingapp.data.AppUser

// FriendListActivity-তে সব ইউজারের তালিকা দেখানোর Adapter
class UserAdapter(
    private var userList: List<AppUser>,
    private val onItemClick: (AppUser) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        // displayName খালি/null থাকলে Default text বসাবে
        holder.binding.tvUserName.text = if (!user.displayName.isNullOrBlank()) user.displayName else "Default User Name"
        holder.binding.tvUserEmail.text = user.userEmail

        // সুনির্দিষ্ট কোন ইউজারে ক্লিক করলে তার লোকেশন ম্যাপে ওপেন হবে
        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newList: List<AppUser>) {
        userList = newList
        notifyDataSetChanged()
    }
}