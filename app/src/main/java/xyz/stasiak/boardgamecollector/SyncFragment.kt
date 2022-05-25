package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentSyncBinding
import java.time.Instant
import java.util.*

class SyncFragment : Fragment() {

    private lateinit var binding: FragmentSyncBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncBinding.inflate(inflater, container, false)
        update(boardGameCollectorDbHandler.getLastSync())
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.syncBtnStart.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.downloadData()
            boardGameCollectorDbHandler.setLastSync(Date.from(Instant.now()))
            findNavController().navigateUp()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
    }

    fun update(lastSync: Date?) {
        binding.syncDateOfSync.text = getString(
            R.string.main_date_of_last_sync, lastSync
        )
    }
}