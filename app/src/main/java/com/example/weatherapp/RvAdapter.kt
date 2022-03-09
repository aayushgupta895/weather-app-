package com.example.weatherapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class RvAdapter(private val context:Context) : RecyclerView.Adapter<weatherViewHolder>() {
    private  val items : ArrayList<hour> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): weatherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
        return weatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: weatherViewHolder, position: Int) {
         val currentItem = items[position]
        holder.temp.text = currentItem.hour_temp
        if(position==0)
            holder.time.text = "12 AM"
        else if(position<=12)
             holder.time.text = position.toString()+" AM"
        else holder.time.text = (position-12).toString()+" PM"
//        holder.image.setImageDrawable(R.drawable.moon)
        val drawable : Int = drawable(currentItem.text_condition,currentItem.isDay)

            holder.image.setImageResource(drawable)

    }



    override fun getItemCount(): Int {
        return items.size;
    }
    fun hourData(updatedList: ArrayList<hour>){
        items.clear();
        items.addAll(updatedList);
        notifyDataSetChanged()
    }

}
   class weatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image : ImageView = itemView.findViewById(R.id.sun)
       val temp : TextView = itemView.findViewById(R.id.temp)
       val time : TextView = itemView.findViewById(R.id.time)
//       val current_weather : ImageView = itemView.findViewById(R.id.current_weather)
   }
public fun drawable(textCondition: String, day: String): Int {
    if(day=="1"){
        when(textCondition){
            "Clear" -> return R.drawable.sunny
            "Partly cloudy" -> return R.drawable.sunny_partly_cloudy
            "Mist" -> return R.drawable.misty_sunny
        }
    }
    when(textCondition){
        "Mist" -> return R.drawable.misty_night
        "Partly cloudy" -> return R.drawable.moon_partly_cloudy
        "Rain" -> return R.drawable.rain
        "Sunny" -> return R.drawable.sunny
        "Cloudy" -> return R.drawable.cloudy
        "Clear" -> return R.drawable.moon
        "Overcast" -> return R.drawable.cloudy
        "Moderate snow" -> return R.drawable.snow
        "Heavy snow" -> return R.drawable.snow
        "Light snow" -> return R.drawable.snow
        "Fog" -> return R.drawable.fog
        "Light rain shower" -> return R.drawable.light_rain
        "Patchy rain shower" -> return R.drawable.light_rain
        "Moderate or heavy rain shower" -> return R.drawable.rain
        "Thunder" -> return R.drawable.rain
        "Torrential rain shower"-> return  R.drawable.rain

    }
    return R.drawable.cloudy
}