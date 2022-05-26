package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentConfigBinding
import java.time.Instant
import java.util.*

class ConfigFragment : Fragment() {

    private lateinit var binding: FragmentConfigBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentConfigBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.configName.doOnTextChanged { text, _, _, _ ->
            binding.configBtnStart.isEnabled = text?.isNotEmpty() ?: true
        }

        binding.configBtnStart.setOnClickListener {
            boardGameCollectorDbHandler.createConfig(
                UserName(binding.configName.text.toString()), Date.from(
                    Instant.now()
                )
            )
            val mainActivity = activity as MainActivity
            mainActivity.downloadData()
            findNavController().navigate(R.id.action_ConfigFragment_to_MainFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(context, null)
    }
}