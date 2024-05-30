package com.avinash.sociopulse

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.avinash.sociopulse.models.Post
import com.bumptech.glide.Glide
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class StatisticActivity : AppCompatActivity() {

    private var postId: String? = null
    private lateinit var user_image: ImageView
    private lateinit var post_author: TextView
    private lateinit var post_text: TextView
    private lateinit var post_time: TextView
    private lateinit var post_image: ImageView
    private val TAG = "StatisticActivity"
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        db = FirebaseFirestore.getInstance();

        postId = intent.getStringExtra("postId")

        user_image = findViewById<ImageView>(R.id.user_image)
        post_author = findViewById<TextView>(R.id.post_author)
        post_text = findViewById<TextView>(R.id.post_text)
        post_time = findViewById<TextView>(R.id.post_time)
        post_image = findViewById<ImageView>(R.id.feed_post_image)

        val postDoc = db.collection("Posts")
            .document(postId!!).get().addOnCompleteListener { it ->
                if (it.isSuccessful){
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


    }
}