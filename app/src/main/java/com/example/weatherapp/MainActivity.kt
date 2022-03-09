package com.example.weatherapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request

import com.android.volley.toolbox.JsonObjectRequest
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(){
//    var blurLayout: BlurLayout? = null
//     lateinit var blurView:BlurView
    private lateinit var progressBar : ProgressBar
    lateinit var mAdaper : RvAdapter
    lateinit var editText: EditText
    private lateinit var cityName : TextView
    lateinit var temperature: TextView
    lateinit var condition: TextView
    lateinit var maxtemp : TextView
    lateinit var mintemp : TextView
    lateinit var feelLike : TextView
    lateinit var precipitation : TextView
    lateinit var humidity : TextView
    lateinit var wind : TextView
    lateinit var pressure : TextView
    lateinit var current_weather : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        idBinding()
        progressBar = findViewById(R.id.progress)
        progressBar.visibility = ProgressBar.VISIBLE
        cityName = findViewById(R.id.city_info)
        editText = findViewById(R.id.editText)
        cityName.ellipsize = TextUtils.TruncateAt.MARQUEE
        cityName.isSelected = true;
        temperature = findViewById(R.id.temperature)
        condition = findViewById(R.id.condition)
        condition.ellipsize = TextUtils.TruncateAt.MARQUEE
        condition.isSelected = true
        maxtemp = findViewById(R.id.max_temp)
        mintemp = findViewById(R.id.min_temp)
        feelLike = findViewById(R.id.feelLike)
        precipitation = findViewById(R.id.precipitation)
        humidity = findViewById(R.id.humidity)
        wind = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        current_weather = findViewById(R.id.current_weather)
        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        mAdaper = RvAdapter(this)
        recyclerView.adapter = mAdaper
        fetchData("delhi");

    }


     fun button(v: View){
         progressBar.visibility = ProgressBar.VISIBLE
         Log.d("editText",editText.text.toString())
        fetchData(editText.text.toString())
 }

   fun fetchData(s : String){
       val url =
           "https://api.weatherapi.com/v1/forecast.json?key=c1f2c15e9dc9483d9ad141048220802&q=$s&days=1&aqi=yes&alerts=yes"
       val jsonObjectRequest = JsonObjectRequest(
           Request.Method.GET, url, null,
           {

               val locationJsonObject = it.getJSONObject("location")
               val currentJsonObject = it.getJSONObject("current")
               val conditionJsonObject = currentJsonObject.getJSONObject("condition")

               val maxminJsonObject = it.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day")
               Toast.makeText(this,s, Toast.LENGTH_LONG).show()
               //name of the city
               val weatherData = WeatherData(
                   locationJsonObject.getString("name"),
//                Toast.makeText(this,cityName.text.toString(),Toast.LENGTH_LONG).show()

                   //current temperature
                   currentJsonObject.getString("temp_c").toFloat().roundToInt().toString(),


                   //condition of the weather
                   conditionJsonObject.getString("text"),
                   //feel of the temperature
                   "real feel "+currentJsonObject.getString("feelslike_c").toFloat().roundToInt().toString(),
                   // maxTemp
                   maxminJsonObject.getString("maxtemp_c").toFloat().roundToInt().toString(),
                   //minTemp
                   maxminJsonObject.getString("mintemp_c").toFloat().roundToInt().toString()+" /",
                   // precipitation
                   currentJsonObject.getString("precip_mm"),
                   //wind
                   currentJsonObject.getString("wind_kph")+" km/h",
                   //pressure
                   currentJsonObject.getString("pressure_mb")+"mBar",
                   //humidity
                   currentJsonObject.getString("humidity")+"%",
                //isDay
                   currentJsonObject.getString("is_day")
                   //text_condition

                   )
               val hour = it.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour")
               val hourList = ArrayList<hour>()
               for ( i in 0 until hour.length()){

                   val currentHour =  hour.getJSONObject(i)

                   val conditionJson = currentHour.getJSONObject("condition")

                   val hour =  hour(currentHour.getString("is_day"),conditionJson.getString("text"),currentHour.getString("temp_c"))
//                    Log.d("hourList","here")
                   hourList.add(hour)
               }
               //
               for( i in 0 until hourList.size){
                   Log.d("hourlist","${hourList.get(i).isDay} "+" ${hourList.get(i).hour_temp}")
               }


               mAdaper.hourData(hourList)
               dataAddition(weatherData)
           },
           {
               progressBar.visibility = ProgressBar.GONE
               Toast.makeText(this," please choose the valid location", Toast.LENGTH_LONG).show()
//                Log.d("volleyError",it.localizedMessage)

           }
       )

// Access the RequestQueue through your singleton class.
       MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

   }
//    fun idBinding(){
//
//    }


    fun dataAddition(WD : WeatherData){
        progressBar.visibility = ProgressBar.GONE
        cityName.text = WD.cityName
        temperature.text = WD.temperature
        condition.text =  WD.condition
        feelLike.text =  WD.feelLike
        maxtemp.text = WD.maxTemp
        mintemp.text = WD.minTemp
        precipitation.text =  WD.precipitation
        humidity.text =  WD.humidity
        wind.text = WD.wind
        pressure.text =  WD.pressure

         current_weather.setImageResource(drawable(WD.condition,WD.isDay))
    }



//    private fun blurLayout() {
//        val radius = 2f
//
//        val decorView: View = window.decorView
//        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
//        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
//        //Set drawable to draw in the beginning of each blurred frame (Optional).
//        //Can be used in case your layout has a lot of transparent space and your content
//        //gets kinda lost after after blur is applied.
//        //Set drawable to draw in the beginning of each blurred frame (Optional).
//        //Can be used in case your layout has a lot of transparent space and your content
//        //gets kinda lost after after blur is applied.
//        val windowBackground: Drawable = decorView.getBackground()
//
//        blurView.setupWith(decorView.findViewById(android.R.id.content))
//            .setFrameClearDrawable(windowBackground)
//            .setBlurAlgorithm(RenderScriptBlur(this))
//            .setBlurRadius(radius)
//            .setBlurAutoUpdate(true)
//            .setHasFixedTransformationMatrix(true)
//    }


}