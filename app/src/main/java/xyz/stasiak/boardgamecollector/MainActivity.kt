@file:Suppress("DEPRECATION")

package xyz.stasiak.boardgamecollector

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import xyz.stasiak.boardgamecollector.databinding.ActivityMainBinding
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var boardGameCollectorDbHandler: BoardGameCollectorDbHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardGameCollectorDbHandler = BoardGameCollectorDbHandler(this, null)

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
        val userName = boardGameCollectorDbHandler.getName()
        if (userName != null) {
            val gamesDownloader = GamesDownloader()
            gamesDownloader.execute(userName.name)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GamesDownloader :
        AsyncTask<String, Int, String>() {

        private lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Synchronization in progress...")
            progressDialog.show()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
        }

        override fun doInBackground(vararg args: String?): String {
            try {
                val url =
                    URL("https://www.boardgamegeek.com/xmlapi2/collection?username=${args[0]}&stats=1")
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
            val games = ArrayList<Game>()
            val fileName = "games.xml"
            val inDir = File(filesDir, "XML")
            if (inDir.exists()) {
                val file = File(inDir, fileName)
                if (file.exists()) {
                    val xmlDoc: Document =
                        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                    xmlDoc.documentElement.normalize()
                    val items: NodeList = xmlDoc.getElementsByTagName("item")
                    for (i in 0 until items.length) {
                        val itemNode: Node = items.item(i)
                        if (itemNode.nodeType == Node.ELEMENT_NODE) {
                            val children = itemNode.childNodes
                            var currentName: String? = null
                            var currentYear: String? = null
                            val currentBggId =
                                itemNode.attributes.getNamedItem("objectid").textContent
                            var currentRank: String? = null
                            var currentImageUrl: String? = null
                            for (j in 0 until children.length) {
                                val node = children.item(j)
                                if (node is Element) {
                                    when (node.nodeName) {
                                        "name" -> currentName = node.textContent
                                        "yearpublished" -> currentYear = node.textContent
                                        "image" -> currentImageUrl = node.textContent
                                        "stats" -> {
                                            val statsChildren = node.childNodes
                                            for (k in 0 until statsChildren.length) {
                                                val statsChild = statsChildren.item(k)
                                                if (statsChild.nodeName == "rating") {
                                                    val ratingChildren = statsChild.childNodes
                                                    for (l in 0 until ratingChildren.length) {
                                                        val ratingChild = ratingChildren.item(l)
                                                        if (ratingChild.nodeName == "ranks") {
                                                            val ranksChildren =
                                                                ratingChild.childNodes
                                                            for (m in 0 until ranksChildren.length) {
                                                                val rankChild =
                                                                    ranksChildren.item(m)
                                                                if (rankChild is Element) {
                                                                    val type =
                                                                        rankChild.attributes.getNamedItem(
                                                                            "type"
                                                                        )
                                                                    val name =
                                                                        rankChild.attributes.getNamedItem(
                                                                            "name"
                                                                        )

                                                                    if (type != null && type.textContent == "subtype" && name != null && name.textContent == "boardgame") {
                                                                        currentRank =
                                                                            rankChild.attributes.getNamedItem(
                                                                                "value"
                                                                            ).textContent
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (currentYear != null && currentName != null && currentBggId != null && currentRank != null) {
                                var bytes: ByteArray? = null
                                try {
                                    val url = URL(currentImageUrl)
                                    val connection = url.openConnection()
                                    connection.doInput = true
                                    connection.connect()
                                    val input: InputStream = connection.getInputStream()
                                    val bitmap = BitmapFactory.decodeStream(input)
                                    val byteArrayOutputStream = ByteArrayOutputStream()
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        100,
                                        byteArrayOutputStream
                                    )
                                    bytes = byteArrayOutputStream.toByteArray()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                if (currentRank != "Not Ranked") {
                                    games.add(
                                        Game(
                                            null,
                                            currentName,
                                            "org",
                                            currentYear.toInt(),
                                            currentBggId.toLong(),
                                            currentRank.toInt(),
                                            bytes
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            games.forEach { boardGameCollectorDbHandler.addGame(it) }
            updateFragments()
            return "success"
        }
    }

    private fun updateFragments() {
        val userName = boardGameCollectorDbHandler.getName()
        val numOfGames = boardGameCollectorDbHandler.countGames()
        val lastSync = boardGameCollectorDbHandler.getLastSync()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is MainFragment) {
                fragment.update(userName, numOfGames, 4, lastSync)
            } else if (fragment is SyncFragment) {
                fragment.update(lastSync)
            }
        }
    }
}