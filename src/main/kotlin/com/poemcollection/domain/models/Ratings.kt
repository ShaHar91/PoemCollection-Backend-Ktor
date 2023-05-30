package com.poemcollection.domain.models

@kotlinx.serialization.Serializable
data class Ratings(
    val total: Int = 0,
    val five_star: Int = 0,
    val four_star: Int = 0,
    val three_star: Int = 0,
    val two_star: Int = 0,
    val one_star: Int = 0,
    val average: Double = 0.0
)