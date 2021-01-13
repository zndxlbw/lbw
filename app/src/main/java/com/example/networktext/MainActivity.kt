package com.example.networktext
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
class MainActivity : AppCompatActivity() {
    lateinit var timer : Timer
    public val searchCarSpinnerData = arrayOf("未选择","载运车#1","载运车#2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()  //隐藏标题栏
        var bundle = this.intent.extras
        val ipadress :String = bundle?.get("url").toString()
        var selected_car : String = "null"
        val sendresponsebtn : Button = findViewById(R.id.sendresponse)
        val button_stop :Button = findViewById(R.id.stopBtn)
        val searchCarSpinnerData = arrayOf("未选择","载运车#1","载运车#2")
        //val carSpinner :Spinner = findViewById(R.id.select_view)
        val id_text_intent : TextView = findViewById(R.id.text_id)
        //var selected_car : String = "null"
        id_text_intent.setText(ipadress)
        val selectcarEdit : EditText = findViewById(R.id.selectcar_edit)
        var select_number :String = selectcarEdit.text.toString()
        //创建载运车选择窗口
//        val adapter = ArrayAdapter(this, R.layout.color_spinner_layout, searchCarSpinnerData)
//        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
//        carSpinner.adapter = adapter
//        carSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (searchCarSpinnerData[position] == "未选择")
//                {
//                    Toast.makeText(this@MainActivity,"请选择待查看载运车", Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    val searchCarSpinnerData = arrayOf("载运车#1","载运车#2")
//                    Toast.makeText(this@MainActivity,"你点击的是"+searchCarSpinnerData[position-1], Toast.LENGTH_SHORT).show()
//                    selected_car = searchCarSpinnerData[position-1]
//                    id_text_intent.setText(selected_car)
//                }
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Toast.makeText(this@MainActivity,"nothingSelected", Toast.LENGTH_SHORT).show()
//            }
//        }
        //发送数据请求
        sendresponsebtn.setOnClickListener {
            //sendRequestWithHttpURLConnection()
            var select_number :String = selectcarEdit.text.toString()
            Log.d("MainActivity", select_number)
            if (select_number == "1" || select_number == "2" ){
                startTimer(ipadress,select_number)
                selectcarEdit.isEnabled=false
            }
            else if (selectcarEdit.text.isEmpty()){
                Toast.makeText(this, "请先选择要监控的载运车序号！", Toast.LENGTH_SHORT).show()
            }
            else if (select_number != "1" && select_number != "2"){
                Toast.makeText(this, "请输入正确的载运车序号！", Toast.LENGTH_SHORT).show()
            }


            //carSpinner.isEnabled = false
            //sendRequestWithOkHttp(ipadress,selected_car)
        }
        //停止读取数据
        button_stop.setOnClickListener {
            stopTimer()
            selectcarEdit.isEnabled=true
            //carSpinner.isEnabled = true
           //selected_car = "null"
        }
    }
    //定时器相关函数
    private fun startTimer(ipadress : String,selected_car : String) {
        timer = fixedRateTimer("", false, 0, 1000) {

            sendRequestWithOkHttp(ipadress,selected_car)
        }
    }
    private fun stopTimer() {
        timer.cancel()
        toast("数据读取已停止")
        Log.d("MainActivity", "数据读取已停止")
    }
    private fun toast(text: String) {
        runOnUiThread {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
    //网络请求解析相关函数
    private fun sendRequestWithHttpURLConnection(){
        thread {
            var connection : HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL("https://www.baidu.com")
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val input = connection.inputStream

                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                //showResponse(response.toString())
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                connection?.disconnect()
            }
        }
    }
    private fun sendRequestWithOkHttp(ipaddress : String,selectedCar : String){
       // thread {
            val text_id_selected_text : TextView = findViewById(R.id.text_id)
            try {
                val client = OkHttpClient()
                when(selectedCar){
                    "1" -> {
                        //text_id_selected_text.setText("http://"+ipaddress+"/car_data_1.json")
                        val request = Request.Builder()
                                //.url("http://[2001:da8:270:2021::f9]:8080/dat/car_data_1.json")
                                .url("http://[2001:da8:270:2021::f9]:80/car_data_1.json")
                                .build()
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        if (responseData != null){
                            //showResponse(responseData)
                            //parseXMLWithPull(responseData)
                            //parseJSONwithJSONObject(responseData)
                            parseJSONwithGSON(responseData)
                        }
                    }
                    "2" -> {
                        //text_id_selected_text.setText("http://"+ipaddress+"/car_data_2.json")
                            val request = Request.Builder()
                                    .url("http://"+ipaddress+"/car_data_2.json")
                                    .build()
                            val response = client.newCall(request).execute()
                            val responseData = response.body?.string()
                            if (responseData != null){
                                //showResponse(responseData)
                                //parseXMLWithPull(responseData)
                                //parseJSONwithJSONObject(responseData)
                                parseJSONwithGSON(responseData)
                            }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        //}
    }
    private fun parseXMLWithPull(xmlData :String){
        try {
            val factory =XmlPullParserFactory.newInstance()
            val xmlPullParser = factory.newPullParser()
            xmlPullParser.setInput(StringReader(xmlData))
            var eventType = xmlPullParser.eventType
            var id = ""
            var name = ""
            var version = ""
            val textvvv : TextView = findViewById(R.id.text_id)
            while (eventType != XmlPullParser.END_DOCUMENT){
                val nodeName = xmlPullParser.name
                when(eventType){
                    XmlPullParser.START_TAG ->{
                        when(nodeName){
                            "id" -> {
                                id = xmlPullParser.nextText()
                                Log.d("MainActivity","id is $id")
                                textvvv.text = id
                            }

                            "name" -> {
                                name = xmlPullParser.nextText()
                                Log.d("MainActivity","name is $name")
                            }

                            "version" -> {
                                version = xmlPullParser.nextText()
                                Log.d("MainActivity","version is $version")
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if ("app" == nodeName){
                            Log.d("MainActivity","id is $id")
                            Log.d("MainActivity","name is $name")
                            Log.d("MainActivity","version is $version")
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun parseJSONwithJSONObject(jsonData:String){
        try {
            val jsonArray = JSONArray(jsonData)
            for (i in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val name = jsonObject.getString("name")
                val version = jsonObject.getString("version")
                Log.d("MainActivity","id is $id")
                Log.d("MainActivity","name is $name")
                Log.d("MainActivity","version is $version")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun parseJSONwithGSON(jsonData: String){
        val text_dest : TextView = findViewById(R.id.text_dest)
        val text_ele : TextView = findViewById(R.id.text_ele)
        val text_id : TextView = findViewById(R.id.text_id)
        val text_location_num : TextView = findViewById(R.id.text_location_num)
        val text_location_x : TextView = findViewById(R.id.text_location_x)
        val text_location_y : TextView = findViewById(R.id.text_location_y)
        val text_start : TextView = findViewById(R.id.text_start)
        val text_state : TextView = findViewById(R.id.text_state)
        val text_veolcity : TextView = findViewById(R.id.text_veolcity)
        val gson = Gson()
        val cars = gson.fromJson(jsonData,car::class.java)
//        Log.d("MainActivity","id is ${cars.car_dest}")
//        text_dest.setText(cars.car_dest)
//        Log.d("MainActivity","name is ${cars.car_ele}")
//        text_ele.setText(cars.car_ele)
//        Log.d("MainActivity","version is ${cars.car_id}")
//        text_id.setText(cars.car_id)
//        Log.d("MainActivity","name is ${cars.car_location_num}")
//        text_location_num.setText(cars.car_location_num.toString())
//        Log.d("MainActivity","version is ${cars.car_location_x}")
//        text_location_x.setText(cars.car_location_x)
//        Log.d("MainActivity","name is ${cars.car_location_y}")
//        text_location_y.setText(cars.car_location_y)
//        Log.d("MainActivity","version is ${cars.car_start}")
//        text_start.text = cars.car_start
//        //text_start.setText(cars.car_start)
//        Log.d("MainActivity","name is ${cars.car_state}")
//        text_state.setText(cars.car_state)
//        Log.d("MainActivity","version is ${cars.car_veolcity}")
//        text_veolcity.setText(cars.car_veolcity)
        Log.d("MainActivity","id is ${cars.car_dest}")
        text_dest.text = cars.car_dest
        Log.d("MainActivity","name is ${cars.car_ele}")
        text_ele.text = cars.car_ele
        Log.d("MainActivity","version is ${cars.car_id}")
        text_id.text = cars.car_id
        Log.d("MainActivity","name is ${cars.car_location_num}")
        text_location_num.text = cars.car_location_num
        Log.d("MainActivity","version is ${cars.car_location_x}")
        text_location_x.text = cars.car_location_x
        Log.d("MainActivity","name is ${cars.car_location_y}")
        text_location_y.text = cars.car_location_y
        Log.d("MainActivity","version is ${cars.car_start}")
        text_start.text = cars.car_start
        //text_start.setText(cars.car_start)
        Log.d("MainActivity","name is ${cars.car_state}")
        text_state.text = cars.car_state
        Log.d("MainActivity","version is ${cars.car_veolcity}")
        text_veolcity.text = cars.car_veolcity
        val iv : ImageView = findViewById(R.id.car_line_view)
        val bitmap : Bitmap = Bitmap.createBitmap(2600,1600, Bitmap.Config.ARGB_8888)
        val canvas_new_circle: Canvas = Canvas(bitmap)
        val canvas_new_line: Canvas = Canvas(bitmap)
        val canvas_new_background: Canvas = Canvas(bitmap)
        val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.RED)
        val paint_background : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint_background.setColor(Color.BLACK)
        val paint_line : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint_line.style = Paint.Style.FILL
        paint_line.strokeWidth = 5F
        paint_line.setColor(Color.GREEN)
        canvas_new_background.drawRect(0F,0F,2600F,1600F,paint_background)
        var location_x : Float = text_location_x.text.toString().toFloat()
        var location_y : Float = text_location_y.text.toString().toFloat()
        Log.d("Mainactivity",location_x.toString())
        Log.d("Mainactivity",location_y.toString())
        for (i in 1 until 50){
            canvas_new_line.drawLine(i.toFloat()*52,0F,i.toFloat()*52,1600F,paint_line)
        }
        for (i in 1 until 20){
            canvas_new_line.drawLine(0F,i.toFloat()*80,2600F,i.toFloat()*80,paint_line)
        }
        canvas_new_circle.drawCircle(location_x, location_y, 30F, paint);
        iv.setImageBitmap(bitmap)
        }
}
