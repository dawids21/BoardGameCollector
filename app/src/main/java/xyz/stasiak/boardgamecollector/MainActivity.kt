package xyz.stasiak.boardgamecollector

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import xyz.stasiak.boardgamecollector.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        val boardGameCollectorDbHandler = BoardGameCollectorDbHandler(this, null)
        if (boardGameCollectorDbHandler.isNameSet()) {
            navController.navigate(R.id.action_ConfigFragment_to_MainFragment)
        }
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun downloadData() {
        GamesDownloader().execute()
    }

    @Suppress("DEPRECATION")
    private inner class GamesDownloader : AsyncTask<String, Int, String>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            println(result)
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                val url =
                    URL("https://www.boardgamegeek.com/xmlapi2/collection?username=Dalakos&stats=1")
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val stream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/games.xml")
                val data = ByteArray(1024)
                var total: Long = 0
                var progress = 0
                var count = stream.read(data)
                while (count != -1) {
                    total += count.toLong()
                    val progress_temp = total.toInt() * 100 / lengthOfFile
                    if (progress_temp % 10 == 0 && progress != progress_temp) {
                        progress = progress_temp
                    }
                    fos.write(data, 0, count)
                    count = stream.read(data)
                }
                stream.close()
                fos.close()
            } catch (e: MalformedURLException) {
                return "Zły URL"
            } catch (e: FileNotFoundException) {
                return "Brak pliku"
            } catch (e: IOException) {
                return "Wyjątek IO"
            }
            return "success"
        }
    }
}