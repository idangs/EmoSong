package hu.ait.android.testfirebase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim)

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) { }

            override fun onAnimationEnd(animation: Animation?) {
                Handler().postDelayed({
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }, SPLASH_DISPLAY_LENGTH.toLong())

            }

            override fun onAnimationStart(animation: Animation?) { }
        })

        splashscreen.startAnimation(anim)

    }


}

