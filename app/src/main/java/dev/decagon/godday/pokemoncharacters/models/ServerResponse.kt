package dev.decagon.godday.pokemoncharacters.models


// This is a model of servers response after a successful image upload

data class ServerResponse(
        val status: Long,
        val message: String,
        val payload: PayLoad
)

data class PayLoad(
        val fileId: String,
        val fileType: String,
        val fileName: String,
        val downloadUri: String,
        val uploadStatus: Boolean
)