package hu.ait.android.testfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.*
import hu.ait.android.testfirebase.data.Quotes
import hu.ait.android.testfirebase.data.Songs
import kotlinx.android.synthetic.main.activity_color.*

class ColorActivity : AppCompatActivity() {

    private var smileProb = 0.toFloat()
    private lateinit var postsListener: ListenerRegistration
    private var songUrls = mutableListOf<String>()
    private var songIds = mutableListOf<String>()
    private var hypeUrls = mutableListOf<String>()
    private var mildUrls = mutableListOf<String>()
    private var sadUrls = mutableListOf<String>()
    private var testUrls = mutableListOf<String>()

    private lateinit var quotesListener: ListenerRegistration
    private var quotes_array = mutableListOf<String>()
    private var quoteIds = mutableListOf<String>()
    private var happyQuotes = mutableListOf<String>()
    private var sadQuotes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        initPosts()


        g.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            i.putExtra(getString(R.string.smile_value), getSongs(0.5))
            i.putExtra("CAPTION", getQuotes(0.5))

            startActivity(i)
        }

        w.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            i.putExtra(getString(R.string.smile_value), getSongs(0.2))
            i.putExtra("CAPTION", getQuotes(0.2))
            startActivity(i)
        }

        p.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            i.putExtra(getString(R.string.smile_value), getSongs(0.7))
            i.putExtra("CAPTION", getQuotes(0.7))
            startActivity(i)
        }

        b.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            i.putExtra(getString(R.string.smile_value), getSongs(0.1))
            i.putExtra("CAPTION", getQuotes(0.1))
            startActivity(i)
        }

        r.setOnClickListener{
            val i = Intent(this@ColorActivity, SongActivity::class.java)
            i.putExtra(getString(R.string.smile_value), getSongs(0.9))
            i.putExtra("CAPTION", getQuotes(0.9))
            startActivity(i)
        }
    }

    fun initPosts() {
        val db = FirebaseFirestore.getInstance()
        val postsCollection = db.collection(getString(R.string.songurls))
        val quotesCollection = db.collection("quotes")

        postsListener = postsCollection.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    Toast.makeText(this@ColorActivity, getString(R.string.error)+ p1.message,
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

        quotesListener = quotesCollection.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    Toast.makeText(this@ColorActivity, getString(R.string.error)+ p1.message,
                            Toast.LENGTH_LONG).show()
                    return
                }
                for (docChange in querySnapshot!!.documentChanges) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            val post = docChange.document.toObject(Quotes::class.java)
                            val id = docChange.document.id
                            quoteIds.add(id)
                            quotes_array.add(post.quotes)
                        }

                    }
                }

                for (i in 0 until quotes_array.size) {
                    when {
                        quoteIds[i].first() == 'h' -> happyQuotes.add(quotes_array[i])
                        quoteIds[i].first() == 's' -> sadQuotes.add(quotes_array[i])
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

    private fun getQuotes(smile_percent: Double): String {
        return when {
            (smile_percent <= 0.5) -> {
                val randomIndex = (0 until sadQuotes.size).shuffled().first()
                sadQuotes[randomIndex]
            }
            else -> {
                val randomIndex = (0 until happyQuotes.size).shuffled().first()
                happyQuotes[randomIndex]
            }
        }
    }

        }
