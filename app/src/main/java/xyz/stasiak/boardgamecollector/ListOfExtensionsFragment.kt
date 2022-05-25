package xyz.stasiak.boardgamecollector

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.stasiak.boardgamecollector.databinding.FragmentListOfExtensionsBinding

class ListOfExtensionsFragment : Fragment() {

    private lateinit var binding: FragmentListOfExtensionsBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler
    private lateinit var adapter: SimpleCursorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListOfExtensionsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listOfExtensions.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
        val columns = arrayOf("_id", "title", "year", "image")
        val id =
            intArrayOf(
                R.id.extensionId,
                R.id.extensionTitle,
                R.id.extensionYear,
                R.id.extensionImage
            )
        val cursor = boardGameCollectorDbHandler.findExtensionsCursor()
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
                    val image = dbCursor.getBlob(column)
                    if (image != null) {
                        imageView.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                image,
                                0,
                                image.size
                            )
                        )
                    }
                }
            }
            return@setViewBinder true
        }
    }
}