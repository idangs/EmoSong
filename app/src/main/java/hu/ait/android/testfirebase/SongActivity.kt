package hu.ait.android.testfirebase

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_song.*
import java.io.IOException
import hu.ait.android.testfirebase.R.id.caption


class SongActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener{

    private lateinit var mp: MediaPlayer
    private var url =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        val anim = AnimationUtils.loadAnimation(this, R.anim.lotus_anim)
        w.startAnimation(anim)

        // Initializing variables
        mp = MediaPlayer()

        val extras = intent.extras
        if (extras != null) {
            if (intent.hasExtra(MainActivity.CAPTION)) {
                val quote = intent.getStringExtra(MainActivity.CAPTION)
                caption.setText(quote)
            }
            url = extras.getString(getString(R.string.smile_value))
            fetchAudioUrlFromFirebase(url)
        }

        btnPause.setOnClickListener {
            mp.pause()
        }

        btnPlay.setOnClickListener {
            mp.start()
        }
    }

    private fun fetchAudioUrlFromFirebase(uri: String) {

//        val storage = FirebaseStorage.getInstance()
//        // Create a storage reference from our app
//        val storageRef = storage.getReferenceFromUrl(uri)
//        storageRef.downloadUrl.addOnSuccessListener(OnSuccessListener<Any> { uri ->
//            try {
//                // Download url of file
//                val url = uri.toString()
////                println("Here's your url: ${url}")
//                mp.setDataSource(url)
//                // wait for media player to get prepare
//                mp.setOnPreparedListener(this@SongActivity)
//                mp.prepareAsync()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        })
        mp.setDataSource(uri)
        // wait for media player to get prepare
        mp.setOnPreparedListener(this@SongActivity)
        mp.prepareAsync()
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
    }

    //need to make it stop when exit from app
    override fun onStop() {
        super.onStop()
        mp.stop()
    }

    override fun onPause() {
        super.onPause()
        mp.stop()
    }
}


