package wang.xiunian.pexelsdemo.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import wang.xiunian.pexelsdemo.R
import wang.xiunian.pexelsdemo.ui.main.entity.PhotosResponse

class QuickViewer(val root: ViewGroup) {
    private val imageView: ImageView
    private val alterTextView: TextView
    private val photographerTv: TextView
    private val toggleButton: ToggleButton
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(ColorDrawable(Color.parseColor("#eeeeee")))//temp img
    private var imgViewWidth = -1
    private var imgViewHeight = -1

    init {
        imageView = root.findViewById(R.id.iv_full)
        alterTextView = root.findViewById(R.id.tv_alter)
        photographerTv = root.findViewById(R.id.tv_photographer)
        toggleButton = root.findViewById<ToggleButton>(R.id.toggle_like)
        root.setOnClickListener {
            it.visibility = View.GONE
        }

        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            setLike(isChecked)
        }
        val vto = imageView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                imgViewWidth = imageView.width
                imgViewHeight = imageView.height

            }
        })

    }

    fun show(itemData: PhotosResponse) {
        root.visibility = View.VISIBLE
        val req = Glide.with(imageView.context)
            .setDefaultRequestOptions(requestOptions)
            .load(itemData.src.original)
            .thumbnail(Glide.with(root.context).load(itemData.src.landscape))
            .error(ColorDrawable(Color.parseColor("#22ff00ff")))//temp img

        if (imgViewWidth > 0 && imgViewHeight > 0) {
            req.override(imgViewWidth, imgViewHeight).into(imageView)
        } else {
            req.into(imageView)
        }
        alterTextView.text = itemData.alt
        photographerTv.setText(itemData.photographer)
        toggleButton.isChecked = itemData.liked
        setLike(itemData.liked)

    }

    fun dismiss(): Boolean {
        if (root.visibility != View.VISIBLE) {
            return false
        }
        return true
    }

    private fun setLike(liked: Boolean) {
        if (liked) {
            toggleButton.setBackgroundResource(R.drawable.thumb_liked)
        } else {
            toggleButton.setBackgroundResource(R.drawable.thumb_un_like)
        }
    }
}