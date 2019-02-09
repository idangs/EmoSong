package hu.ait.android.testfirebase

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import android.content.pm.PackageManager
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.support.v4.content.ContextCompat
import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.provider.MediaStore
import android.content.Intent
import android.widget.Toast
import com.google.firebase.firestore.*
import hu.ait.android.testfirebase.data.Songs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var smileProb = 0.toFloat()
    private lateinit var postsListener: ListenerRegistration
    private var songUrls = mutableListOf<String>()
    private var songIds = mutableListOf<String>()
    private var hypeUrls = mutableListOf<String>()
    private var mildUrls = mutableListOf<String>()
    private var sadUrls = mutableListOf<String>()
    private var testUrls = mutableListOf<String>()

    private val RESULT_LOAD_IMAGE = 100

    private val REQUEST_PERMISSION_CODE = 200

    companion object {
        private const val CAMERA_REQUEST_CODE = 102
    }

    private lateinit var uploadBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPosts()

        btnAttach.setOnClickListener{
            startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE), MainActivity.CAMERA_REQUEST_CODE)
        }

        btnAttach.isEnabled = false
        requestNeededPermission()


        btnAnalyze.setOnClickListener{
            if(!::uploadBitmap.isInitialized){
                Toast.makeText(
                        this@MainActivity, getString(R.string.upload), Toast.LENGTH_LONG
                ).show()
            } else {
                var prop = analyze()
                var str =""
                if(prop!= (-1).toFloat()){
                    str = getString(R.string.prob) + prop

                }else{
                    str = getString(R.string.cannot)
                }
                tvResult.text = str
            }
        }

        btnP.setOnClickListener{
            if(::uploadBitmap.isInitialized){
                var smile_prop = getSongs(analyze().toDouble())
                val i = Intent(this@MainActivity, SongActivity::class.java)
                i.putExtra(getString(R.string.smile_value), smile_prop)
                startActivity(i)
            } else {
                Toast.makeText(this@MainActivity, getString(R.string.upload), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun analyze(): Float {
        val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build()
        val detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts)
        // import a bitmap
        val image = FirebaseVisionImage.fromBitmap(this!!.uploadBitmap!!)

        val result = detector.detectInImage(image)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        // If classification was enabled:
                        if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                            smileProb = face.smilingProbability
                            var str = getString(R.string.sm_prob)+ smileProb
                            tvResult.text = str
                        } else {
                            var str = getString(R.string.unable)
                            tvResult.text = str
                            smileProb = (-1).toFloat()
                        }
                    }
                }
        return smileProb
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.CAMERA)) {
                Toast.makeText(this,
                        getString(R.string.need_camera), Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    1001)
        } else {
            // WE ALREADY HAVE IT
            btnAttach.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.camera_perm_granted), Toast.LENGTH_SHORT).show()
                    btnAttach.isEnabled = true
                } else {
                    Toast.makeText(this, getString(R.string.camera_not), Toast.LENGTH_SHORT).show()
                    btnAttach.isEnabled = false
                }
            }
        }
    }


    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // get the photo URI from the gallery, find the file path from URI and send the file path to ConfirmPhoto
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {

            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])

            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            uploadBitmap = BitmapFactory.decodeFile(picturePath)
            ivPicture.setImageBitmap(uploadBitmap)
            ivPicture.background = null

        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                uploadBitmap = it.extras.get(getString(R.string.data)) as Bitmap
                ivPicture.setImageBitmap(uploadBitmap)
                ivPicture.background = null
            }
        }
    }

    fun initPosts() {
        val db = FirebaseFirestore.getInstance()
        val postsCollection = db.collection(getString(R.string.songurls))

        postsListener = postsCollection.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    Toast.makeText(this@MainActivity, getString(R.string.error)+ p1.message,
                            Toast.LENGTH_LONG).show()
                    return
                }
                for (docChange in querySnapshot!!.documentChanges) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            val post = docChange.document.toObject(Songs::class.java)
                            val id = docChange.document.id
                            songIds.add(id)
                            songUrls.add(post.url)
                        }

                    }
                }

                for (i in 0 until songUrls.size) {
                    when {
                        songIds[i].first() == 'h' -> hypeUrls.add(songUrls[i])
                        songIds[i].first() == 'm' -> mildUrls.add(songUrls[i])
                        songIds[i].first() == 's' -> sadUrls.add(songUrls[i])
                        //This is for testing purpose
                        songIds[i].first() == 't' -> testUrls.add(songUrls[i])
                    }
                }
            }
        })
    }

    private fun getSongs(smile_percent: Double): String{

        return when {
            (smile_percent <= 0.333) -> {
                val randomIndex = (0 until sadUrls.size).shuffled().first()
                sadUrls[randomIndex]
            }
            ( smile_percent>0.333 && smile_percent <= 0.666)-> {
                val randomIndex = (0 until mildUrls.size).shuffled().first()
                mildUrls[randomIndex]
            }
            else -> {
                val randomIndex = (0 until hypeUrls.size).shuffled().first()
                hypeUrls[randomIndex]
            }
        }
    }
}


