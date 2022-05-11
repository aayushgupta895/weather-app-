package com.example.weatherapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(){
//    var blurLayout: BlurLayout? = null
//     lateinit var blurView:BlurView
       lateinit  var chances_of_rain : String
    private lateinit var progressBar : ProgressBar
    lateinit var mAdaper : RvAdapter
//    lateinit var locationButton : Button
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

    // TODO: Step 1.1, Review variables (no changes).
// FusedLocationProviderClient - Main class for receiving location updates.
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e., how often you
// should receive updates, the priority, etc.
    lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    lateinit var locationCallback: LocationCallback

    // Used only for local storage of the last known location. Usually, this would be saved to your
// database, but because this is a simplified sample without a full database, we only need the
// last location to create a Notification if the user navigates away from the app.
    var currentLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        idBinding()
        progressBar = findViewById(R.id.progress)
        progressBar.visibility = ProgressBar.VISIBLE
        cityName = findViewById(R.id.city_info)
//        locationButton = findViewById(R.id.location_button)
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
        fetchData("mawsynram");
        doSomething(editText)
//        Toast.makeText(this,chances_of_rain,Toast.LENGTH_LONG).show()

//        locationButton.setOnClickListener {
//            Log.d("locationButton","inside onclicklistener")
//            fetchLocation();
//        }

    }
    private fun doSomething(search: EditText){
        search.setOnEditorActionListener(TextView.OnEditorActionListener{ _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                fetchData(search.text.toString());
                return@OnEditorActionListener true
            }
            false
        })
    }

     fun button(v: View){

         Log.d("editText",editText.text.toString())
        fetchData(editText.text.toString())
 }

   fun fetchData(s : String){
       progressBar.visibility = ProgressBar.VISIBLE
       val url =
           "https://api.weatherapi.com/v1/forecast.json?key=c1f2c15e9dc9483d9ad141048220802&q=$s&days=1&aqi=yes&alerts=yes"
       val jsonObjectRequest = JsonObjectRequest(
           Request.Method.GET, url, null,
           {

               val locationJsonObject = it.getJSONObject("location")
               val currentJsonObject = it.getJSONObject("current")
               val conditionJsonObject = currentJsonObject.getJSONObject("condition")

               val dayJsonObject = it.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day")
               Toast.makeText(this,s, Toast.LENGTH_SHORT).show()
//               Toast.makeText(this,dayJsonObject.getString("daily_will_it_rain").toString(), Toast.LENGTH_LONG).show()
               chances_of_rain = dayJsonObject.getString("daily_chance_of_rain").toString()
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
                   dayJsonObject.getString("maxtemp_c").toFloat().roundToInt().toString(),
                   //minTemp
                   dayJsonObject.getString("mintemp_c").toFloat().roundToInt().toString()+" /",
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
               makeNotification()
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
    fun makeNotification(){
        Toast.makeText(this,chances_of_rain,Toast.LENGTH_SHORT).show()
      var textContent : String = "rain chances today is " + chances_of_rain + "%"
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

          val importance = NotificationManager.IMPORTANCE_DEFAULT
          val channel = NotificationChannel("My notification", "MY Notifications", importance).apply {
              description = textContent
          }
          // Register the channel with the system
          val notificationManager: NotificationManager =
              getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
          notificationManager.createNotificationChannel(channel)
      }
      var builder = NotificationCompat.Builder(this, "My notification")
          .setSmallIcon(R.drawable.ic_notification)
          .setContentTitle("weather report today")
          .setContentText(textContent)
          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          .setAutoCancel(true)
      val  managerCompat = NotificationManagerCompat.from(this)
      managerCompat.notify(1,builder.build())
  }
//
//    fun fetchLocation(){
//
//        // TODO: Step 1.2, Review the FusedLocationProviderClient.
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        // check permission
//        if(ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED){
//
//            getLocation();
//        }else{
//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),PackageManager.PERMISSION_GRANTED
//            )
//            Log.d("locationButton","inside fetchLoaction")
//        }
//    }

//    private fun getLocation(){
//        val locationTask: Task<Location> = fusedLocationProviderClient.lastLocation
//        if(ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//
//                Log.d("locationButton","inside here in getLocation")
//                //initialize geoCoder
//                val geocoder =  Geocoder(this, Locale.getDefault())
//                var address : List<Address>? = null
//
////                    Log.d("location",it.latitude.toString())
//
//                    if(locationTask != null ) {
//                        address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
//
//                        Log.d("locationButton", "${address.get(0).locality}")
//                        Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show()
//
//                    }
//
//            }
//        }
//        else{
//            AskForPermission()
//        }
//            }
//    fun AskForPermission(){
//        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if(requestCode == 100)
//        {
//            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                getLocation()
//            }
//            else{
//                Toast.makeText(this,"permission required",Toast.LENGTH_SHORT).show()
//            }
//        }
//                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
}


