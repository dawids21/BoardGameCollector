package xyz.stasiak.boardgamecollector

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.stasiak.boardgamecollector.databinding.FragmentListOfExtensionsBinding
import java.net.URL

class ListOfExtensionsFragment : Fragment() {

    private lateinit var binding: FragmentListOfExtensionsBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler
    private lateinit var adapter: SimpleCursorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListOfExtensionsBinding.inflate(inflater, container, false)

        binding.extensionsListId.setOnClickListener {
            setAdapter(requireContext(), boardGameCollectorDbHandler.findExtensionsCursor())
            binding.listOfExtensions.adapter = adapter
        }

        binding.extensionsListTitle.setOnClickListener {
            setAdapter(
                requireContext(),
                boardGameCollectorDbHandler.findExtensionsCursorSortByTitle()
            )
            binding.listOfExtensions.adapter = adapter
        }

        binding.extensionsListYear.setOnClickListener {
            setAdapter(
                requireContext(),
                boardGameCollectorDbHandler.findExtensionsCursorSortByYear()
            )
            binding.listOfExtensions.adapter = adapter
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listOfExtensions.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
        setAdapter(context, boardGameCollectorDbHandler.findExtensionsCursor())
    }

    private fun setAdapter(context: Context, cursor: Cursor) {
        val columns = arrayOf("_id", "title", "year", "image")
        val id =
            intArrayOf(
                R.id.extensionId,
                R.id.extensionTitle,
                R.id.extensionYear,
                R.id.extensionImage
            )
        adapter =
            SimpleCursorAdapter(context, R.layout.list_extensions_template, cursor, columns, id, 0)
        adapter.setViewBinder { view, dbCursor, column ->
            when (view.id) {
                R.id.extensionId, R.id.extensionTitle, R.id.extensionYear -> {
                    val textView = view as TextView
                    textView.text = dbCursor.getString(column)
                }
                R.id.extensionImage -> {
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