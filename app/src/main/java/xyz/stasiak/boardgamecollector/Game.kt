package xyz.stasiak.boardgamecollector

class Game(
    val id: Long?,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val bggId: Long,
    val rank: Int,
    val image: ByteArray?
) {
}