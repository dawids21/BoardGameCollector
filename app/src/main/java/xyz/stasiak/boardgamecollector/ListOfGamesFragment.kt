package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.stasiak.boardgamecollector.databinding.FragmentListOfGamesBinding

class ListOfGamesFragment : Fragment() {

    private var _binding: FragmentListOfGamesBinding? = null
    private val binding get() = _binding!!
    private var _boardGameCollectorDbHandler: BoardGameCollectorDbHandler? = null
    private val boardGameCollectorDbHandler get() = _boardGameCollectorDbHandler!!
    private lateinit var adapter: SimpleCursorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfGamesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listOfGames.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
        val columns = arrayOf("_id", "title", "year", "rank")
        val id =
            intArrayOf(R.id.gameId, R.id.gameTitle, R.id.gameYear, R.id.gameRank)
        val cursor = boardGameCollectorDbHandler.findGamesCursor()
        adapter =
            SimpleCursorAdapter(context, R.layout.list_games_template, cursor, columns, id, 0)
        adapter.setViewBinder { view, dbCursor, column ->
            when (view.id) {
                R.id.gameId, R.id.gameTitle, R.id.gameYear, R.id.gameRank -> {
                    val textView = view as TextView
                    textView.text = dbCursor.getString(column)
                }
            }
            return@setViewBinder true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}