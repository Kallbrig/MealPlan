package Prototype.Design

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap

class favs_view : AppCompatActivity() {

    //Holds all cardviews on favs View page
    var favsCards = ArrayList<CardView>(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favs_view)

        //Back Button
        setBackBut()

        //Adding all Cards from Favs_View to the arraylist that will store them. This makes them iteratable
        favsCards.addAll(
            arrayListOf(
                findViewById<CardView>(R.id.Card1),
                findViewById<CardView>(R.id.Card2),
                findViewById<CardView>(R.id.Card3),
                findViewById<CardView>(R.id.Card4),
                findViewById<CardView>(R.id.Card5),
                findViewById<CardView>(R.id.Card6)
            )
        )




        for (i in 0 until 6)

            favsCards[i].setOnClickListener {
                var Img = findViewById<ImageView>(R.id.img1Favs).drawable.toBitmap(300, 400)
                var Carddesc = findViewById<TextView>(R.id.mealName1).text as String
                createDetailIntent(Carddesc, Img)
            }


    }


    //Creates and starts an intent that takes you to detailedView.
    //Requires Meal ID # and the Meal Image as a bitmap as arguments
    //
    fun createDetailIntent(id: String, img: Bitmap) {
        println("Intent Created")
        val intent = Intent(this, DetailedView::class.java)
        intent.putExtra("img", img)
        intent.putExtra("id", id)
        // log message
        Log.i(TAG, "Put Extras - about to start DetailedView with meal ID#" + id)
        startActivity(intent)
    }

    //
    fun setBackBut() {
        var backBut = findViewById<ImageButton>(R.id.backButton)
        backBut.setOnClickListener {
            finish()
        }
    }

}





