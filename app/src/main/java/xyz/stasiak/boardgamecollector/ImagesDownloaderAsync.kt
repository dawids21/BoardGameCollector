@file:Suppress("DEPRECATION")

package xyz.stasiak.boardgamecollector

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.net.URL

@SuppressLint("StaticFieldLeak")
class ImagesDownloaderAsync(private val imageView: ImageView, private val imageUrl: URL) :
    AsyncTask<String, Void, Bitmap>() {

    override fun onPreExecute() {
        super.onPreExecute()
        imageView.setImageResource(R.drawable.board_game)
    }

    override fun doInBackground(vararg p0: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(
                imageUrl.openConnection().getInputStream()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        if (result != null) {
            imageView.setImageBitmap(result)
        }
    }
}