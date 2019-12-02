package Prototype.Design

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.nfc.Tag
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


class apiConnection {


    // GLOBAL VARIABLES
    //

    protected var jsonA: JSONArray? = null
    protected var mealInfo2: JSONObject? = null
    protected var parsedArray: ArrayList<ArrayList<String>>? = null
    protected var mealInfo: ArrayList<String> = ArrayList(5)
    private val TAG = "APICONNECTION"


    //fetches meal information using an ID Number
    //Requires the Meal ID # as an Argument
    //Returns an ArrayList<String> Containing the single Meal Information.
    fun getMealById(mealId: String): ArrayList<String> {
        return doAsyncResult {
            var jsonO =
                JSONObject(URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + mealId).readText()).getJSONArray(
                    "meals"
                ).getJSONObject(0)
            var mealInfoLocal = parseIndMeal(jsonO)
            //  Log.i(TAG, "GetMealById has finished")
            return@doAsyncResult mealInfoLocal
        }.get()
    }


    //fetches meal information using the meal's Name
    //Requires the Meal Name as an Argument
    //Returns an ArrayList<String> containing the single Meal Information
    fun getMealByName(mealName: String): ArrayList<String> {
        return doAsyncResult {
            while (mealName.contains(' ')) {
                mealName.replace(' ', '_')
            }
            var jsonO =
                JSONObject(URL("https://www.themealdb.com/api/json/v1/1/search.php?s=" + mealName).readText()).getJSONArray(
                    "meals"
                ).getJSONObject(0)
            var mealInfoLocal = parseIndMeal(jsonO)
            //  Log.i(TAG, "GetMealById has finished")
            return@doAsyncResult mealInfoLocal
        }.get()
    }


    //fetches an entire Category from
    // TESTING AND CHECKING WILL BE REQUIRED

    fun getCat(catName: String): ArrayList<ArrayList<String>> {
        return doAsyncResult {
            var jsonA =
                JSONObject(URL("https://www.themealdb.com/api/json/v2/9973533/filter.php?c=" + catName).readText()).getJSONArray(
                    "meals"
                )
            var fullMealInfoSingleCat = ArrayList<ArrayList<String>>(jsonA.length())
            for (i in 0..jsonA.length() - 1) {
                fullMealInfoSingleCat.add(parseCatIndMeal(jsonA.getJSONObject(i)))
            }
            return@doAsyncResult fullMealInfoSingleCat
        }.get()
    }


    fun getImgDrawable(imgUrl: String): Drawable {
        return doAsyncResult {
            var inputStream = URL(imgUrl).openStream()
            var draw = Drawable.createFromStream(inputStream, null)
            inputStream.close()
            return@doAsyncResult draw
        }.get()
    }


    fun getImgBitmap(imgUrl: String, cont: Context): Bitmap {
        return doAsyncResult {
            return@doAsyncResult Picasso.with(cont).load(imgUrl).get()
        }.get()
    }


    private fun parseCatIndMeal(jsonO: JSONObject): ArrayList<String> {
        var mealInfo: ArrayList<String> = ArrayList(5)

        if (jsonO.has("strMeal")) {
            mealInfo.add(jsonO.getString("strMeal"))
        } else {
            println("JsonO is incomplete: Missing strMeal")
        }
        if (jsonO.has("strMealThumb")) {
            mealInfo.add(jsonO.getString("strMealThumb"))
        } else {
            println("JsonO is incomplete: Missing strMealThumb")
        }
        if (jsonO.has("idMeal")) {
            mealInfo.add(jsonO.getString("idMeal"))
        } else {
            println("JsonO is incomplete: Missing idMeal")
        }


        return mealInfo
    }


    //This parses individual meals data into an ArrayList<String>
    //requires a Json Object as an argument. Only used by getMealById and getMealByName internally.
    //returns an ArrayList<String> full of all Individual Meal Information

    //[0]mealName, [1]mealImgURL, [2]Meal Id #, [3]mealArea, [4]Instructions, [5]Ingredients1, [6]Ingredients2, [7]...
    private fun parseIndMeal(jsonO: JSONObject): ArrayList<String> {
        var mealInfo: ArrayList<String> = ArrayList(25)
        //println("JsonO in parseIndMeal() - ")


        if (jsonO.has("strMeal")) {
            mealInfo.add(jsonO.getString("strMeal"))
        } else {
            println("JsonO is incomplete: Missing strMeal")
        }
        if (jsonO.has("strMealThumb")) {
            mealInfo.add(jsonO.getString("strMealThumb"))
        } else {
            println("JsonO is incomplete: Missing strMealThumb")
        }
        if (jsonO.has("idMeal")) {
            mealInfo.add(jsonO.getString("idMeal"))
        } else {
            println("JsonO is incomplete: Missing idMeal")
        }
        if (jsonO.has("strArea")) {
            mealInfo.add(jsonO.getString("strArea"))
        } else {
            println("JsonO is incomplete: Missing strArea")
        }
        if (jsonO.has("strInstructions")) {
            mealInfo.add(jsonO.getString("strInstructions"))
        } else {
            println("JsonO is incomplete: Missing strInstructions")
        }
        if (jsonO.has("strMeasure1") && !jsonO.getString("strMeasure1").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure1") + " " + jsonO.getString("strIngredient1"))
        }
        if (jsonO.has("strMeasure2") && !jsonO.getString("strMeasure2").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure2") + " " + jsonO.getString("strIngredient2"))
        }
        if (jsonO.has("strMeasure3") && !jsonO.getString("strMeasure3").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure3") + " " + jsonO.getString("strIngredient3"))
        }
        if (jsonO.has("strMeasure4") && !jsonO.getString("strMeasure4").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure4") + " " + jsonO.getString("strIngredient4"))
        }
        if (jsonO.has("strMeasure5") && !jsonO.getString("strMeasure5").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure5") + " " + jsonO.getString("strIngredient5"))
        }
        if (jsonO.has("strMeasure6") && !jsonO.getString("strMeasure6").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure6") + " " + jsonO.getString("strIngredient6"))
        }
        if (jsonO.has("strMeasure7") && !jsonO.getString("strMeasure7").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure7") + " " + jsonO.getString("strIngredient7"))
        }
        if (jsonO.has("strMeasure8") && !jsonO.getString("strMeasure8").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure8") + " " + jsonO.getString("strIngredient8"))
        }
        if (jsonO.has("strMeasure9") && !jsonO.getString("strMeasure9").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure9") + " " + jsonO.getString("strIngredient9"))
        }
        if (jsonO.has("strMeasure10") && !jsonO.getString("strMeasure10").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure10") + " " + jsonO.getString("strIngredient10"))
        }
        if (jsonO.has("strMeasure11") && !jsonO.getString("strMeasure11").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure11") + " " + jsonO.getString("strIngredient11"))
        }
        if (jsonO.has("strMeasure12") && !jsonO.getString("strMeasure12").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure12") + " " + jsonO.getString("strIngredient12"))
        }
        if (jsonO.has("strMeasure13") && !jsonO.getString("strMeasure13").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure13") + " " + jsonO.getString("strIngredient13"))
        }
        if (jsonO.has("strMeasure14") && !jsonO.getString("strMeasure14").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure14") + " " + jsonO.getString("strIngredient14"))
        }
        if (jsonO.has("strMeasure15") && !jsonO.getString("strMeasure15").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure15") + " " + jsonO.getString("strIngredient15"))
        }
        if (jsonO.has("strMeasure16") && !jsonO.getString("strMeasure16").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure16") + " " + jsonO.getString("strIngredient16"))
        }
        if (jsonO.has("strMeasure17") && !jsonO.getString("strMeasure17").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure17") + " " + jsonO.getString("strIngredient17"))
        }
        if (jsonO.has("strMeasure18") && !jsonO.getString("strMeasure18").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure18") + " " + jsonO.getString("strIngredient18"))
        }
        if (jsonO.has("strMeasure19") && !jsonO.getString("strMeasure19").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure19") + " " + jsonO.getString("strIngredient19"))
        }
        if (jsonO.has("strMeasure20") && !jsonO.getString("strMeasure20").isNullOrEmpty()) {
            mealInfo.add(jsonO.getString("strMeasure20") + " " + jsonO.getString("strIngredient20"))
        }
        return mealInfo
    }


    fun SearchByName(mealName: String): ArrayList<ArrayList<String>> {

        return doAsyncResult {
            var jsonA =
                JSONObject(URL("https://www.themealdb.com/api/json/v1/1/search.php?s=" + mealName).readText()).getJSONArray(
                    "meals"
                )
            var mealInfoLocal = ArrayList<ArrayList<String>>(9)

            for (i in 0 until jsonA.length() - 1) {

                mealInfoLocal.add(parseIndMeal(jsonA.getJSONObject(i)))

            }


            return@doAsyncResult mealInfoLocal
        }.get()
    }


}