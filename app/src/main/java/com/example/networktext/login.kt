package com.example.networktext
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import kotlin.concurrent.thread
class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        val loginbtn :Button = findViewById(R.id.loginbtn)
        val signinbtn :Button = findViewById(R.id.signinbtn)
        val ipaddress : EditText = findViewById(R.id.ipaddress)
        val ip_intent :String = ipaddress.text.toString()
        loginbtn.setOnClickListener {
            val account : EditText = findViewById(R.id.account)
            val keywords : EditText = findViewById(R.id.keywords)
            val now_account = account.text.toString()
            val now_keywords = keywords.text.toString()
            thread {
                val now_account : String = account.text.toString()
                try {
                    val client_login = OkHttpClient()
                    //text_id_selected_text.setText("http://[2001:da8:270:2021::f9]:8080/dat/car_data_1.json")
                    val request_login = Request.Builder()
                            .url("http://[2001:da8:270:2021::f9]:80/userlist.json")
                            .build()
                    val response_login = client_login.newCall(request_login).execute()
                    val responseData_login = response_login.body?.string()
                    if (responseData_login != null){
                        //showResponse(responseData)
                        //parseXMLWithPull(responseData)
                        //parseJSONwithJSONObject(responseData)
                        //parseJSONwithGSO_login(responseData_login)
                        parseJSONWithJSONObject_login(responseData_login,now_account,now_keywords,ip_intent)
                        Log.d("MainActivity",responseData_login)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

        }
        signinbtn.setOnClickListener {
            val signin_intent = Intent(this,Signin::class.java)
            startActivityForResult(signin_intent,1)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> if (resultCode == Activity.RESULT_OK){
                val returnData = data?.getStringExtra("success")
                Toast.makeText(this,returnData,Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun parseJSONwithGSO_login(jsonData: String){
//        val text_dest : TextView = findViewById(R.id.text_dest)
//        val text_ele : TextView = findViewById(R.id.text_ele)
//        val text_id : TextView = findViewById(R.id.text_id)
//        val text_location_num : TextView = findViewById(R.id.text_location_num)
//        val text_location_x : TextView = findViewById(R.id.text_location_x)
//        val text_location_y : TextView = findViewById(R.id.text_location_y)
//        val text_start : TextView = findViewById(R.id.text_start)
//        val text_state : TextView = findViewById(R.id.text_state)
//        val text_veolcity : TextView = findViewById(R.id.text_veolcity)
//
//        val gson = Gson()
//        val cars = gson.fromJson(jsonData,car::class.java)
//        Log.d("MainActivity","id is ${cars.car_dest}")
//        text_dest.text = cars.car_dest
//        Log.d("MainActivity","name is ${cars.car_ele}")
//        text_ele.text = cars.car_ele
//        Log.d("MainActivity","version is ${cars.car_id}")
//        text_id.text = cars.car_id
//        Log.d("MainActivity","name is ${cars.car_location_num}")
//        text_location_num.text = cars.car_location_num
//        Log.d("MainActivity","version is ${cars.car_location_x}")
//        text_location_x.text = cars.car_location_x
//        Log.d("MainActivity","name is ${cars.car_location_y}")
//        text_location_y.text = cars.car_location_y
//        Log.d("MainActivity","version is ${cars.car_start}")
//        text_start.text = cars.car_start
//        //text_start.setText(cars.car_start)
//        Log.d("MainActivity","name is ${cars.car_state}")
//        text_state.text = cars.car_state
//        Log.d("MainActivity","version is ${cars.car_veolcity}")
//        text_veolcity.text = cars.car_veolcity
    }
    private fun parseJSONWithJSONObject_login(jsondata : String, account_name : String,now_keywords:String,ip_intent : String){
        try {
            val keys = ArrayList<String>()
            var name_is_in_list :Int = 0
//            val response_username : TextView = findViewById(R.id.checkkey)
            val jsonobject_login = JSONObject(jsondata)
            var select_name = jsonobject_login.keys().forEach {
                keys.add(it as String)
            }
            Log.d("MainActivity","user_name is "+keys)

            if(account_name in keys){
                var getname = jsonobject_login.getString(account_name)
                Log.d("MainActivity","user_name is "+getname)
//                        response_username.text = getname
                if (now_keywords == getname){
                    val intent_login = Intent(this,MainActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("url",ip_intent)
                    intent_login.putExtras(bundle)
                    startActivity(intent_login)
                    finish()
                }
                else{
                    Looper.prepare()
                    Toast.makeText(this@login,"请输入正确的“管理者”权限等级账号密码",Toast.LENGTH_LONG).show()
                    Looper.loop()
                }
            }
            else{
                val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
                val prefs_password = prefs.getString(account_name,"")

                if (now_keywords == prefs_password){
                    val intent_login = Intent(this,MainActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("url",ip_intent)
                    intent_login.putExtras(bundle)
                    startActivity(intent_login)
                    finish()
                }
                else{
                    Looper.prepare()
                    Toast.makeText(this@login,"请输入正确的“使用者”权限等级账号密码",Toast.LENGTH_LONG).show()
                    Looper.loop()
                }
            }
//            for(name_key in keys){
//                if (account_name == name_key) {
//                    var getname = jsonobject_login.getString(account_name)
//                    if (getname != null){
//                        Log.d("MainActivity","user_name is "+getname)
////                        response_username.text = getname
//                        if (now_keywords == getname){
//                            val intent_login = Intent(this,MainActivity::class.java)
//                            var bundle = Bundle()
//                            bundle.putString("url",ip_intent)
//                            intent_login.putExtras(bundle)
//                            startActivity(intent_login)
//                            finish()
//                        }
//                        else{
//                            Toast.makeText(this,"account or password is invalid",Toast.LENGTH_LONG).show()
//                        }
//                    }
//                    break
//                }
//
//                Log.d("MainActivity","请输入正确的账号密码")
//            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}