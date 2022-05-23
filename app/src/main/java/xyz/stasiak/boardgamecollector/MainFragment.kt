package xyz.stasiak.boardgamecollector

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainBtnErase.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_ConfigFragment)
        }

        binding.mainHello.text = getString(R.string.main_hello, "World")
        binding.mainNumOfGames.text = getString(R.string.main_num_of_games, 3)
        binding.mainNumOfExtensions.text = getString(R.string.main_num_of_extensions, 4)
        binding.mainDateOfLastSync.text = getString(
            R.string.main_date_of_last_sync, Date.from(
                Instant.now()
            )
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}