package xyz.stasiak.boardgamecollector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.stasiak.boardgamecollector.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {

    companion object {
        const val TITLE_PARAM = "title"
    }

    private lateinit var binding: FragmentRankingBinding

    private var gameName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameName = it.getString(TITLE_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingBinding.inflate(inflater, container, false)

        binding.rankingName.text = gameName
        return binding.root
    }
}