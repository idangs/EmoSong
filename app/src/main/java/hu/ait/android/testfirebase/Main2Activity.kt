package hu.ait.android.testfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        camera.setOnClickListener{
            val i = Intent(this@Main2Activity, MainActivity::class.java)
            startActivity(i)
        }

        color.setOnClickListener{
            val i = Intent(this@Main2Activity, ColorActivity::class.java)
            startActivity(i)
        }
    }
}
