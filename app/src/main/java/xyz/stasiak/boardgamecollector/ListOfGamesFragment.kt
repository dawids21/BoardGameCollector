package xyz.stasiak.boardgamecollector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.stasiak.boardgamecollector.databinding.FragmentListOfGamesBinding

class ListOfGamesFragment : Fragment() {

    private var _binding: FragmentListOfGamesBinding? = null
    private val binding get() = _binding!!
    private var _userNameDbHandler: UserNameDbHandler? = null
    private val userNameDbHandler get() = _userNameDbHandler!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfGamesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listOfGamesBtnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _userNameDbHandler = UserNameDbHandler(context, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}