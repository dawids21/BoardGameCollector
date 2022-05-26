package xyz.stasiak.boardgamecollector

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.stasiak.boardgamecollector.databinding.FragmentRankingBinding
import java.net.URL
import java.text.SimpleDateFormat

class RankingFragment : Fragment() {

    companion object {
        const val ID_PARAM = "gameId"
        const val TITLE_PARAM = "title"
        const val IMAGE_PARAM = "image"
    }

    private lateinit var binding: FragmentRankingBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler
    private lateinit var adapter: SimpleCursorAdapter

    private var gameId: Long? = null
    private var title: String? = null
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getLong(ID_PARAM)
            title = it.getString(TITLE_PARAM)
            image = it.getString(IMAGE_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingBinding.inflate(inflater, container, false)

        binding.rankingTitle.text = title
        if (image != null) {
            ImagesDownloaderAsync(
                binding.rankingImage,
                URL(image)
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(requireContext(), null)
        val columns = arrayOf("date", "rank")
        val id =
            intArrayOf(R.id.rankDate, R.id.rankValue)
        val cursor = boardGameCollectorDbHandler.findRanksByGameCursor(gameId!!)
        adapter =
            SimpleCursorAdapter(context, R.layout.list_ranks_template, cursor, columns, id, 0)
        adapter.setViewBinder { view, dbCursor, column ->
            when (view.id) {
                R.id.rankValue -> {
                    val textView = view as TextView
                    textView.text = dbCursor.getString(column)
                }
                R.id.rankDate -> {
                    val textView = view as TextView
                    val string = dbCursor.getString(column)
                    textView.text =
                        getString(
                            R.string.ranking_list_rank_date,
                            SimpleDateFormat.getDateTimeInstance().parse(string)
                        )
                }
            }
            return@setViewBinder true
        }
        binding.listOfRanks.adapter = adapter
        return binding.root
    }
}