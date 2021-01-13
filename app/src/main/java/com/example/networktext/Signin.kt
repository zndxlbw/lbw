    package com.example.networktext
    import android.app.Activity
    import android.content.Context
    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import android.widget.TextView
    import android.widget.Toast
    class Signin : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_signin)
            supportActionBar?.hide()
            val signinbtn : Button = findViewById(R.id.signinbtn)
            val username_Edit : EditText = findViewById(R.id.username)
            val password_Edit : EditText = findViewById(R.id.password)
            val repassword_Edit : EditText = findViewById(R.id.repassword)
            val name_Edit : EditText = findViewById(R.id.name)
            val number_Edit : EditText = findViewById(R.id.number)
            val righr_edit : EditText = findViewById(R.id.right_number)
            signinbtn.setOnClickListener {
                val username = username_Edit.text.toString()
                val password = password_Edit.text.toString()
                val repassword = repassword_Edit.text.toString()
                val name = name_Edit.text.toString()
                val number = number_Edit.text.toString()
                val right_number = righr_edit.text.toString()
                if (password != repassword){
                    Toast.makeText(this,"请确认输入密码是否一致",Toast.LENGTH_LONG).show()
                }
                else if (right_number != "870653"){
                    Toast.makeText(this,"请确认注册授权码是否正确",Toast.LENGTH_LONG).show()
                }
                else if (password == repassword && right_number == "870653"){
                    val editor = getSharedPreferences("User",Context.MODE_PRIVATE).edit()
                    editor.putString(username,password)
                    editor.putString("name",name)
                    editor.putString("number",number)
                    editor.apply()
                    val intent = Intent()
                    intent.putExtra("success","您已经成功注册账号，欢迎登陆！")
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            }
        }
    }