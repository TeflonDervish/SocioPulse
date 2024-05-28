package com.avinash.sociopulse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avinash.sociopulse.R
import com.avinash.sociopulse.models.Comment
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils

class CommentsAdapter(options: FirestoreRecyclerOptions<Comment>, val context: Context) :
    FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentsViewHolder>(options) {

    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage: ImageView = itemView.findViewById(R.id.profile_image)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val commentAuthor: TextView = itemView.findViewById(R.id.comment_author)
        val commentTime: TextView = itemView.findViewById(R.id.comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, model: Comment) {
        val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.commentText.text = model.text
        holder.commentAuthor.text = model.author.name
        holder.commentTime.text = dateFormatted

        Glide.with(context)
            .load(model.author.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.person_icon_black)
            .into(holder.userImage)
    }

}