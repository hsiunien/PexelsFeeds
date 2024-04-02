package wang.xiunian.pexelsdemo.ui.main

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import wang.xiunian.pexelsdemo.R
import wang.xiunian.pexelsdemo.ui.main.entity.PhotosResponse


class FeedsAdapter(val eventMessage: (EventMessage) -> Unit) : RecyclerView.Adapter<ViewHolder>() {
    companion object {
        private const val TAG = "FeedsAdapter"

        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private val items = mutableListOf<PhotosResponse?>()

    var haveCachedData = false
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
            FeedviewHolder(itemView, eventMessage)
        } else {
            val progressBar = ProgressBar(parent.context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                progressBar.indeterminateDrawable.colorFilter =
                    BlendModeColorFilter(Color.GRAY, BlendMode.SRC_ATOP)
            } else {
                progressBar.indeterminateDrawable.setColorFilter(
                    Color.GRAY,
                    PorterDuff.Mode.MULTIPLY
                )
            }
            val layoutParams =
                MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
            progressBar.layoutParams = layoutParams
            LoadingViewHolder(progressBar)
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FeedviewHolder) {
            items.get(position)?.let { holder.setData(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return if (items[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }


    fun addList(photosResponse: List<PhotosResponse>, isCached: Boolean) {
        if (isCached) {
            items.addAll(0, photosResponse)
        } else {
            items.addAll(photosResponse)
        }
        this.haveCachedData = isCached
    }

    fun replaceList(photosResponse: List<PhotosResponse>) {
        items.clear()
        items.addAll(photosResponse)
        haveCachedData = false
    }

    fun addLoading() {
        items.add(null)
        notifyItemInserted(items.size - 1)
    }

    fun removeLoadingView() {
        //remove loading item
        if (items.size != 0) {
            items.removeAt(items.size - 1)
            notifyItemRemoved(items.size)
        }
    }
}

class FeedviewHolder(rootView: View, val eventMessage: (EventMessage) -> Unit) :
    RecyclerView.ViewHolder(rootView) {
    companion object {
        private const val TAG = "FeedsAdapter"
    }

    val imageView: ImageView
    val authNameTv: TextView
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(ColorDrawable(Color.parseColor("#eeeeee")))//temp img

    init {
        imageView = rootView.findViewById(R.id.photoImageView)
        authNameTv = rootView.findViewById(R.id.authorNameTextView)
        val vto = imageView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val imageViewWidth = imageView.width
                val imageViewHeight = 627 / 1200f * imageViewWidth
                imageView.layoutParams.height = imageViewHeight.toInt()
                imageView.requestLayout()
            }
        })
    }

    fun setData(data: PhotosResponse) {
        itemView.setOnClickListener {
            eventMessage.invoke(EventMessage.ItemClickEvent(data))
        }
        authNameTv.text = data.photographer
        Log.d(TAG, "setData: ${data.src}")
        Glide.with(imageView.context)
            .setDefaultRequestOptions(requestOptions)
            .load(data.src.landscape)
            .error(ColorDrawable(Color.parseColor("#22ff00ff")))//temp img
            .into(imageView)
    }
}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

