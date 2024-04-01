package wang.xiunian.pexelsdemo.ui.main

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var isCached = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
            FeedviewHolder(itemView, eventMessage)
        } else {
            val progressBar = ProgressBar(parent.context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                progressBar.indeterminateDrawable.colorFilter =
                    BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
            } else {
                progressBar.indeterminateDrawable.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.MULTIPLY
                )
            }
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


    fun addList(photosResponse: List<PhotosResponse>) {
        items.addAll(photosResponse)
    }

    fun replaceList(photosResponse: List<PhotosResponse>) {
        items.clear()
        items.addAll(photosResponse)
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
        // If we have some rules for adapting preview images,
        // we can dynamically calculate the width and height needed for the images here.
//        imageView.updateLayoutParams<FrameLayout.LayoutParams> {
//            height = getImageHeight(imageView.context)
//        }
        Log.d(TAG, "setData: ${data.src}")
        Glide.with(imageView.context)
            .setDefaultRequestOptions(requestOptions)
            .load(data.src.landscape)
            .error(ColorDrawable(Color.parseColor("#22ff00ff")))//temp img
            .into(imageView)
    }
}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

fun getImageHeight(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.getDisplayMetrics()
    val screenWidth = displayMetrics.widthPixels
    val scale = context.resources.displayMetrics.density
    val padding = (24f * scale + 0.5f).toInt()
    val ratio = 627 / 1200f
    return (ratio * (screenWidth - padding)).toInt()

}