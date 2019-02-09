package hu.ait.android.testfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_color.*

class ColorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        r.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            startActivity(i)
        }

        b.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            startActivity(i)
        }

        p.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            startActivity(i)
        }

        w.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            startActivity(i)
        }
    }

        }
