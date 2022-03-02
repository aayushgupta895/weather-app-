package com.example.weatherapp

 data class WeatherData (
    val cityName : String,
    val temperature : String,
    val condition : String,
    val feelLike : String,
    val maxTemp : String,
    val minTemp : String,
    val precipitation : String,
    val wind : String,
    val pressure : String,
    val humidity : String,
        )
  data class hour(
     val isDay :String,
     val text_condition :String,
     val hour_temp : String
  )