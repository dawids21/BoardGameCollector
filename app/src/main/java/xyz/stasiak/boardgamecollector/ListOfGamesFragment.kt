package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentListOfGamesBinding
import java.net.URL

class ListOfGamesFragment : Fragment() {

    private lateinit var binding: FragmentListOfGamesBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler
    private lateinit var adapter: SimpleCursorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListOfGamesBinding.inflate(inflater, container, false)
        binding.listOfGames.setOnItemClickListener { _, _, _, gameId ->
            val game = boardGameCollectorDbHandler.findGame(gameId) ?: return@setOnItemClickListener
            val bundle = Bundle()
            bundle.putLong(RankingFragment.ID_PARAM, game.id!!)
            bundle.putString(RankingFragment.TITLE_PARAM, game.title)
            bundle.putString(RankingFragment.IMAGE_PARAM, game.image)
            findNavController().navigate(R.id.action_ListOfGamesFragment_to_RankingFragment, bundle)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listOfGames.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
        val columns = arrayOf("_id", "title", "year", "rank", "image")
        val id =
            intArrayOf(R.id.gameId, R.id.gameTitle, R.id.gameYear, R.id.gameRank, R.id.gameImage)
        val cursor = boardGameCollectorDbHandler.findGamesCursor()
        adapter =
            SimpleCursorAdapter(context, R.layout.list_games_template, cursor, columns, id, 0)
        adapter.setViewBinder { view, dbCursor, column ->
            when (view.id) {
                R.id.gameId, R.id.gameTitle, R.id.gameYear, R.id.gameRank -> {
                    val textView = view as TextView
                    textView.text = dbCursor.getString(column)
                }
                R.id.gameImage -> {
                    val imageView = view as SquareImageView
                    val imageUrl = dbCursor.getString(column)
                    if (imageUrl != null) {
                        ImagesDownloaderAsync(
                            imageView,
                            URL(imageUrl)
                        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    }
                }
            }
            return@setViewBinder true
        }
    }
}