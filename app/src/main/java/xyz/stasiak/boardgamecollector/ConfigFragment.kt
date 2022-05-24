package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {

    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    private var _boardGameCollectorDbHandler: BoardGameCollectorDbHandler? = null
    private val boardGameCollectorDbHandler get() = _boardGameCollectorDbHandler!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.configBtnStart.setOnClickListener {
            boardGameCollectorDbHandler.saveName(UserName(binding.configName.text.toString()))
            findNavController().navigate(R.id.action_ConfigFragment_to_MainFragment)
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