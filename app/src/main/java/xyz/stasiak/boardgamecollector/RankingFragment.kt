package xyz.stasiak.boardgamecollector

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.stasiak.boardgamecollector.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {

    companion object {
        const val TITLE_PARAM = "title"
        const val IMAGE_PARAM = "image"
    }

    private lateinit var binding: FragmentRankingBinding

    private var title: String? = null
    private var image: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE_PARAM)
            image = it.getByteArray(IMAGE_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingBinding.inflate(inflater, container, false)

        binding.rankingTitle.text = title
        if (image != null) {
            if (image != null) {
                binding.rankingImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        image,
                        0,
                        image!!.size
                    )
                )
            }
        }
        return binding.root
    }
}