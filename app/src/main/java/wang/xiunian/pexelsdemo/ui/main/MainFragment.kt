package wang.xiunian.pexelsdemo.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import wang.xiunian.pexelsdemo.R
import java.util.concurrent.atomic.AtomicInteger

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val PER_PAGE = 10
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: FeedsAdapter
    private lateinit var scrollListener: RecyclerViewLoadMoreScroll
    private lateinit var quickViewer: QuickViewer
    private val currentPage = AtomicInteger(0)
    private val loadMoreListener = object : LoadMoreListener {
        override fun onLoadMore() {
            Handler().post {
                if (isAdded) {
                    loadMore()
                }
            }

        }
    }
    private val eventMessage = { msg: EventMessage ->
        when (msg) {
            is EventMessage.ItemClickEvent -> {
                quickViewer.show(msg.itemData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val recycleView = root.findViewById<RecyclerView>(R.id.feed_list_recycleview)
        val fullviewLayout = root.findViewById<ViewGroup>(R.id.full_layout)
        quickViewer = QuickViewer(fullviewLayout)
        adapter = FeedsAdapter(eventMessage)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(requireContext());
        scrollListener =
            RecyclerViewLoadMoreScroll(recycleView.layoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(loadMoreListener)
        recycleView.addOnScrollListener(scrollListener)

        viewModel.loadingResult.observe(this.viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(
                    requireContext(), "loading content failed, check network please",
                    Toast.LENGTH_LONG
                ).show()
                adapter.removeLoadingView()
                scrollListener.setLoaded()
            }
        }
        viewModel.imageItems.observe(this.viewLifecycleOwner) {
            adapter.removeLoadingView()
            if (!it.isCached && adapter.itemCount > 0) {
                adapter.addList(it.photoListResponse)
            } else {
                adapter.replaceList(it.photoListResponse)
            }
            scrollListener.setLoaded()
            adapter.notifyItemChanged(adapter.itemCount)
        }
        viewModel.loadCacheIfExist(1, PER_PAGE)
        loadMore()
        return root
    }


    private fun loadMore() {
        adapter.addLoading()
        viewModel.fetchImages(currentPage.incrementAndGet(), PER_PAGE)
    }

}