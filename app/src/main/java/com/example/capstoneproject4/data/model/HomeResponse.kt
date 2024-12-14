package com.example.capstoneproject4.data.model

data class HomeResponse(
    val message: String,
    val recommended_products: List<RecommendedProductX>,
    val routines: List<RoutineRequest>,
    val status: String
)