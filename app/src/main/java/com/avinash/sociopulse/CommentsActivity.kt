package com.avinash.sociopulse

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avinash.sociopulse.adapters.CommentsAdapter
import com.avinash.sociopulse.databinding.ActivityCommentsBinding
import com.avinash.sociopulse.models.Comment
import com.avinash.sociopulse.models.Post
import com.avinash.sociopulse.util.UserUtil
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.firebase.firestore.FirebaseFirestore

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding

    private var postId: String? = null
    private var commentsAdapter: CommentsAdapter? = null
    private lateinit var recyclerView: RecyclerView

    private lateinit var user_image: ImageView
    private lateinit var post_author: TextView
    private lateinit var post_text: TextView
    private lateinit var post_time: TextView
    private lateinit var post_image: ImageView
    private val TAG = "StatisticActivity"
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getStringExtra("postId")
        recyclerView = binding.commentsRecyclerView

        db = FirebaseFirestore.getInstance();

        postId = intent.getStringExtra("postId")

        user_image = findViewById<ImageView>(R.id.user_image)
        post_author = findViewById<TextView>(R.id.post_author)
        post_text = findViewById<TextView>(R.id.post_text)
        post_time = findViewById<TextView>(R.id.post_time)
        post_image = findViewById<ImageView>(R.id.feed_post_image)

        val postDoc = db.collection("Posts")
            .document(postId!!).get().addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    val post = it.result?.toObject(Post::class.java)

                    val date = DateTimeUtils.formatDate(post!!.time)
                    val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

                    post_author.text = post!!.user.name
                    post_text.text = post!!.text
                    post_time.text = dateFormatted

                    Glide.with(this)
                        .load(post.imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(post_image)

                    Glide.with(this)
                        .load(post.user.imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.person_icon_black)
                        .into(user_image)

                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong! Please Try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        setUpRecyclerView()

        binding.commentSendIcon.setOnClickListener {
            val commentText = binding.commentEditText.editableText.toString()
            val firestore = FirebaseFirestore.getInstance()
            val comment = Comment(commentText, UserUtil.user!!, System.currentTimeMillis())
            firestore.collection("Posts").document(postId!!)
                .collection("Comments")
                .document().set(comment)

            binding.commentEditText.editableText.clear()
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = postId?.let {
            firestore.collection("Posts").document(it).collection("Comments")
        }
        val recyclerViewOptions = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it, Comment::class.java).build()
        }
        commentsAdapter = recyclerViewOptions?.let {
            CommentsAdapter(it, this)
        }
        recyclerView.adapter = commentsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        commentsAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentsAdapter?.stopListening()
    }
}