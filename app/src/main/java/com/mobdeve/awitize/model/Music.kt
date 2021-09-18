package com.mobdeve.awitize.model

data class Music (val key: String,
                  val title: String,
                  val artist: String,
                  val audioFileURL: String,
                  val audioURI: String,
                  val albumCoverURL: String,
                  val albumURI: String,
                  val banned : ArrayList<String>)
