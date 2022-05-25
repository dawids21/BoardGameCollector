package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentMainBinding
import java.time.Instant
import java.util.*

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var _boardGameCollectorDbHandler: BoardGameCollectorDbHandler? = null
    private val boardGameCollectorDbHandler get() = _boardGameCollectorDbHandler!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val userName = boardGameCollectorDbHandler.getName()

        if (userName != null) {
            binding.mainHello.text = getString(R.string.main_hello, userName.name)
        } else {
            binding.mainHello.text = getString(R.string.main_hello, "World")
        }
        binding.mainNumOfGames.text = getString(R.string.main_num_of_games, 3)
        binding.mainNumOfExtensions.text = getString(R.string.main_num_of_extensions, 4)
        binding.mainDateOfLastSync.text = getString(
            R.string.main_date_of_last_sync, Date.from(
                Instant.now()
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainBtnListOfGames.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_ListOfGamesFragment)
        }

        binding.mainBtnStartSyncing.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.downloadData()
        }

        binding.mainBtnErase.setOnClickListener {
            boardGameCollectorDbHandler.deleteName()
            boardGameCollectorDbHandler.deleteGames()
            findNavController().navigate(R.id.action_MainFragment_to_ConfigFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}