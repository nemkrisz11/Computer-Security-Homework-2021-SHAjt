package com.shajt.caffshop.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shajt.caffshop.data.models.UserData
import com.shajt.caffshop.databinding.FragmentUsersUserListItemBinding
import com.shajt.caffshop.ui.commons.GenericRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.shajt.caffshop.ui.user.DetailedUserActivity


class UsersRecyclerViewAdapter
    : GenericRecyclerViewAdapter<UserData, UsersRecyclerViewAdapter.UserListItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRecyclerViewAdapter.UserListItemViewHolder {
        return UserListItemViewHolder(
            FragmentUsersUserListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersRecyclerViewAdapter.UserListItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.username.text = item.username
        holder.regDate.text = SimpleDateFormat.getDateInstance().format(Date(item.regDate))
        holder.isAdmin.text = item.isAdmin.toString()
        holder.root.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailedUserActivity::class.java)
                .putExtra("username", item.username)
            startActivity(
                holder.itemView.context,
                intent,
                null
            )
        }
    }

    inner class UserListItemViewHolder(binding: FragmentUsersUserListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val root = binding.root
        val username = binding.username
        val regDate = binding.regDate
        val isAdmin = binding.isAdmin


    }
}