 package com.example.weatherapp.Activity

 import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
 import android.util.Log
 import android.widget.SearchView
 import com.example.weatherapp.ApiInterface
 import com.example.weatherapp.Model.WeatherApp.weather

 import com.example.weatherapp.R
 import com.example.weatherapp.databinding.ActivityMainBinding
 import retrofit2.Call
 import retrofit2.Callback
 import retrofit2.Response
 import retrofit2.Retrofit
 import retrofit2.converter.gson.GsonConverterFactory
 import java.sql.Timestamp
 import java.text.SimpleDateFormat
 import java.util.Date
 import java.util.Locale

 class MainActivity : AppCompatActivity() {


     private val binding:ActivityMainBinding by lazy {
         ActivityMainBinding.inflate(layoutInflater)
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        fetchWeatherdata("kolkata")
        SearchCity()


    }
     private fun SearchCity(){
         val searchView = binding.search
         searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
             override fun onQueryTextSubmit(query: String?): Boolean {
                 if (query != null){
                     fetchWeatherdata(query)
                 }
                 return true
             }

             override fun onQueryTextChange(p0: String?): Boolean {
                 return true
             }

         })
     }



     private fun fetchWeatherdata(location:String) {
         val retrofit = Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl("https://api.openweathermap.org/data/2.5/")
             .build().create(ApiInterface::class.java)

         val response = retrofit.getWeatherData(location,"b841cc8e84be89f169cfde13338bc4be","metric")
         response.enqueue(object :Callback<weather>{
             override fun onResponse(call: Call<weather>, response: Response<weather>) {
                 val responseBody = response.body()
                 if (response.isSuccessful && responseBody != null){
                     val temperature = responseBody.main.temp.toString()
                     val humidity = responseBody.main.humidity
                     val windspeed = responseBody.wind.speed
                     val sunrise = responseBody.sys.sunrise.toLong()
                     val sunset = responseBody.sys.sunset.toLong()
                     val sealevel = responseBody.main.pressure
                     val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                     val maxTemp = responseBody.main.temp_max
                     val minTemp = responseBody.main.temp_min


                     binding.temp.text = "$temperature °C"
                     binding.weather.text = condition
                     binding.maxtemp.text = "MaxTemp: $maxTemp °C"
                     binding.mintemp.text = "MinTemp: $minTemp °C"
                     binding.humidity.text = "$humidity %"
                     binding.windspeed.text = "$windspeed m/s"
                     binding.condition.text = condition
                     binding.sunrise.text = "${time(sunrise)}"
                     binding.sunset.text = "${time(sunset)}"
                     binding.sealevel.text = "$sealevel hPa"

                     binding.location.text = "$location"
                     binding.day.text = dayName(System.currentTimeMillis())
                     binding.date.text = date()



//                     Log.d("TAG","OnResponse: $temperature")

                     changeImageAccrodingToWeatherCondition(condition)
                 }
             }

             override fun onFailure(call: Call<weather>, t: Throwable) {
                 TODO("Not yet implemented")
             }

         })
     }

     private fun changeImageAccrodingToWeatherCondition(conditions: String) {
         when(conditions){

             "Clear Sky","Sunny","Clear"->{
                 binding.root.setBackgroundResource(R.drawable.sunny_background)
                 binding.animationImage.setAnimation(R.raw.sun)
             }
             "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                 binding.root.setBackgroundResource(R.drawable.colud_background)
                 binding.animationImage.setAnimation(R.raw.cloud)
             }

             "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                 binding.root.setBackgroundResource(R.drawable.rain_background)
                 binding.animationImage.setAnimation(R.raw.rain)
             }
             "Light Snow","Moderate Snow","Heavy snow","Blizzard"->{
                 binding.root.setBackgroundResource(R.drawable.snow_background)
                 binding.animationImage.setAnimation(R.raw.snow)
             }
             else ->{
                 binding.root.setBackgroundResource(R.drawable.sunny_background)
                 binding.animationImage.setAnimation(R.raw.sun)

             }

         }
         binding.animationImage.playAnimation()

     }

     private fun dayName(timestamp:Long):String{
          val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
          return sdf.format((Date()))
      }

     private fun date():String{
         val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
         return sdf.format((Date()))

     }

     private fun time(timestamp: Long):String{
         val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
         return sdf.format((timestamp*1000))
     }




 }
