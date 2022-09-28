package com.ssafy.indive.utils

import com.ssafy.indive.model.dto.PlayListMusic
import com.ssafy.indive.model.entity.PlayListEntity

fun PlayListEntity.mapper(): PlayListMusic = PlayListMusic(
    id = seq.toString(),
    musicSeq = musicSeq,
    track = title,
    streamUrl = streamUrl,
    coverUrl = coverUrl,
    artist = artist
)

