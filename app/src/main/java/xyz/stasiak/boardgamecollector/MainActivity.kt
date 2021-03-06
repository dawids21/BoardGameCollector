@file:Suppress("DEPRECATION")

package xyz.stasiak.boardgamecollector

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
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
        private val deletedGames = ArrayList<Long>()
        private val deletedExtensions = ArrayList<Long>()

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Synchronization in progress...")
            progressDialog.show()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (deletedGames.isNotEmpty() || deletedExtensions.isNotEmpty()) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.download_dialog_title)
                    .setMessage(R.string.download_dialog_message)
                    .setPositiveButton(R.string.download_dialog_yes) { _, _ ->
                        deletedGames.forEach { boardGameCollectorDbHandler.deleteGame(it) }
                        deletedExtensions.forEach { boardGameCollectorDbHandler.deleteExtension(it) }
                        updateFragments()
                    }
                    .setNegativeButton(R.string.download_dialog_no, null)
                    .show()
            }
            updateFragments()
        }

        override fun doInBackground(vararg args: String?): String {
            try {
                val url =
                    URL("https://boardgamegeek.com/xmlapi2/collection?username=${args[0]}&stats=1")
                var lengthOfFile = 0
                for (i in 0..3) {
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    if (connection.responseCode != HttpURLConnection.HTTP_ACCEPTED) {
                        lengthOfFile = connection.contentLength
                        break
                    }
                    connection.disconnect()
                    Thread.sleep(3000)
                }
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
                return "Z??y URL"
            } catch (e: FileNotFoundException) {
                return "Brak pliku"
            } catch (e: IOException) {
                return "Wyj??tek IO"
            }
            val games = ArrayList<Game>()
            val extensions = ArrayList<Extension>()
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
                            var currentBayesAverage: String? = null
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
                                                                        currentBayesAverage =
                                                                            rankChild.attributes.getNamedItem(
                                                                                "bayesaverage"
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
                                if (currentRank == "Not Ranked" && currentBayesAverage != "Not Ranked") {
                                    extensions.add(
                                        Extension(
                                            null,
                                            currentName,
                                            currentYear.toInt(),
                                            currentBggId.toLong(),
                                            currentImageUrl
                                        )
                                    )
                                } else {
                                    games.add(
                                        Game(
                                            null,
                                            currentName,
                                            currentYear.toInt(),
                                            currentBggId.toLong(),
                                            if (currentRank != "Not Ranked") currentRank.toInt() else 0,
                                            currentImageUrl
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val previousGamesBggIds = boardGameCollectorDbHandler.findGameBggIds()
            val previousExtensionsBggIds = boardGameCollectorDbHandler.findExtensionBggIds()
            val currentGamesBggIds = games.map { it.bggId }
            val currentExtensionsBggIds = extensions.map { it.bggId }
            deletedGames.addAll(previousGamesBggIds)
            deletedGames.removeAll(currentGamesBggIds.toSet())
            deletedExtensions.addAll(previousExtensionsBggIds)
            deletedExtensions.removeAll(currentExtensionsBggIds.toSet())

            games.forEach { boardGameCollectorDbHandler.addGame(it) }
            extensions.forEach { boardGameCollectorDbHandler.addExtension(it) }
            return "success"
        }
    }

    private fun updateFragments() {
        val userName = boardGameCollectorDbHandler.getName()
        val numOfGames = boardGameCollectorDbHandler.countGames()
        val numOfExtensions = boardGameCollectorDbHandler.countExtensions()
        val lastSync = boardGameCollectorDbHandler.getLastSync()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is MainFragment) {
                fragment.update(userName, numOfGames, numOfExtensions, lastSync)
            } else if (fragment is SyncFragment) {
                fragment.update(lastSync)
            }
        }
    }
}