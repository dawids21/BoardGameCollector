package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentMainBinding
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        update(
            boardGameCollectorDbHandler.getName(),
            boardGameCollectorDbHandler.countGames(),
            boardGameCollectorDbHandler.countExtensions(),
            boardGameCollectorDbHandler.getLastSync()
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainBtnListOfGames.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_ListOfGamesFragment)
        }

        binding.mainBtnStartSyncing.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_SyncFragment)
        }

        binding.mainBtnErase.setOnClickListener {
            boardGameCollectorDbHandler.deleteConfig()
            boardGameCollectorDbHandler.deleteGames()
            findNavController().navigate(R.id.action_MainFragment_to_ConfigFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
    }

    fun update(userName: UserName?, games: Int, extensions: Int, lastSync: Date?) {
        if (userName != null) {
            binding.mainHello.text = getString(R.string.main_hello, userName.name)
        } else {
            binding.mainHello.text = getString(R.string.main_hello, "World")
        }
        binding.mainNumOfGames.text =
            getString(R.string.main_num_of_games, games)
        binding.mainNumOfExtensions.text =
            getString(R.string.main_num_of_extensions, extensions)
        binding.mainDateOfLastSync.text = getString(
            R.string.main_date_of_last_sync, lastSync
        )
    }
}