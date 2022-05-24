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
    private var _userNameDbHandler: UserNameDbHandler? = null
    private val userNameDbHandler get() = _userNameDbHandler!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        binding.configName.setText(userNameDbHandler.getName()?.name)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.configBtnStart.setOnClickListener {
            userNameDbHandler.saveName(UserName(binding.configName.text.toString()))
            findNavController().navigate(R.id.action_ConfigFragment_to_MainFragment)
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