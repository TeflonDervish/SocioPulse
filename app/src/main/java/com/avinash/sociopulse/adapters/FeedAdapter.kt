package com.avinash.sociopulse.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avinash.sociopulse.CommentsActivity
import com.avinash.sociopulse.R
import com.avinash.sociopulse.StatisticActivity
import com.avinash.sociopulse.models.Post
import com.avinash.sociopulse.util.UserUtil
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/*
* Firestore Recycler Adapter for FeedFragment
* */
class FeedAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context) :
    FirestoreRecyclerAdapter<Post, FeedAdapter.FeedViewHolder>(options) {

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage: ImageView = itemView.findViewById(R.id.user_image)
        val postImage: ImageView = itemView.findViewById(R.id.feed_post_image)
        val likeIcon: ImageView = itemView.findViewById(R.id.post_like_btn)
        val commentIcon: ImageView = itemView.findViewById(R.id.post_comment_btn)
        val postLikeCount: TextView = itemView.findViewById(R.id.like_count)
        val postCommentCount: TextView = itemView.findViewById(R.id.comment_count)
        val authorText: TextView = itemView.findViewById(R.id.post_author)
        val timeText: TextView = itemView.findViewById(R.id.post_time)
        val postText: TextView = itemView.findViewById(R.id.post_text)
        val statictic: TextView = itemView.findViewById(R.id.statistic)
        val variants: ChipGroup = itemView.findViewById(R.id.variants)

        val chose_yes: Chip = itemView.findViewById(R.id.chose_yes)
        val chose_probably_yes: Chip = itemView.findViewById(R.id.chose_probably_yes)
        val chose_probably_not: Chip = itemView.findViewById(R.id.chose_probably_not)
        val chose_no: Chip = itemView.findViewById(R.id.chose_no)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, model: Post) {
        val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.postText.text = model.text
        holder.authorText.text = model.user.name
        holder.timeText.text = dateFormatted
        holder.postLikeCount.text = model.likeList.size.toString()

        Glide.with(context)
            .load(model.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder_image)
            .into(holder.postImage)

        Glide.with(context)
            .load(model.user.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.person_icon_black)
            .into(holder.userImage)

        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid


        val postDocument =
            firestore.collection("Posts")
                .document(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)

        postDocument.collection("Comments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                holder.postCommentCount.text = it.result?.size().toString()
            }
        }

        postDocument.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val post = it.result?.toObject(Post::class.java)
                post?.likeList?.let { list ->
                    if (list.contains(userId)) {
                        holder.likeIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.icon_like_fill
                            )
                        )
                    } else {
                        holder.likeIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.like_icon_outline
                            )
                        )
                    }
//                    Like Feature of Feed
                    holder.likeIcon.setOnClickListener {
                        if (post.likeList.contains(userId)) {
                            post.likeList.remove(userId)
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.like_icon_outline
                                )
                            )
                            postDocument.set(post)

                        } else {
                            userId?.let { userId ->
                                post.likeList.add(userId)
                            }
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.icon_like_fill
                                )
                            )
                            postDocument.set(post)
                        }
                    }
//                    Setting the post details of current user: Updated One
                    postDocument.set(post)
                }

                post?.listYes?.let { list ->
                    if(list.contains(userId)) {
                        holder.chose_yes.isChecked = true
                    }
                }

                post?.listProbablyYes?.let { list ->
                    if(list.contains(userId)){
                        holder.chose_probably_yes.isChecked = true
                    }
                }

                post?.listProbablyNo?.let { list ->
                    if(list.contains(userId)){
                        holder.chose_probably_not.isChecked = true
                    }
                }

                post?.listNot?.let { list ->
                    if(list.contains(userId)){
                        holder.chose_no.isChecked = true
                    }
                }

                holder.variants.setOnCheckedChangeListener{ group, checkedId ->

                    post?.listYes?.remove(userId)
                    post?.listProbablyYes?.remove(userId)
                    post?.listProbablyNo?.remove(userId)
                    post?.listNot?.remove(userId)

                    if (checkedId == R.id.chose_yes) {
                        post?.listYes?.add(userId.toString())
                    }
                    else if (checkedId == R.id.chose_probably_yes) {
                        post?.listProbablyYes?.add(userId!!)
                    }
                    else if (checkedId == R.id.chose_probably_not) {
                        post?.listProbablyNo?.add(userId!!)
                    }
                    else if (checkedId == R.id.chose_no) {
                        post?.listNot?.add(userId!!)
                    }
                    postDocument.set(post!!)
                }

            } else {
                Toast.makeText(
                    context,
                    "Something went wrong! Please Try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
//        Comment Feature
        holder.commentIcon.setOnClickListener {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra("postId", snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
            context.startActivity(intent)
        }

        holder.statictic.setOnClickListener{
            val intent = Intent(context, StatisticActivity::class.java)
            intent.putExtra("postId", snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
            context.startActivity(intent)

        }
    }

}