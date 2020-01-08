package Prototype.Design


import android.content.Intent
import android.os.Bundle
import android.util.Log.*

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import helpers.databaseManager
import org.jetbrains.anko.doAsync


private val TAG: String = "LOGIN ACTIVITY"
private var user: FirebaseUser? = null
private lateinit var data: databaseManager

class login_view : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_view)

        //createSignUpIntent()
        d(TAG, "Login Activity Started")
        findViewById<TextView>(R.id.titleBarLogin).text = getString(R.string.appName)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        user = auth.currentUser
        data = databaseManager()

        val loginBut = findViewById<Button>(R.id.loginBut)
        val signUpBut = findViewById<Button>(R.id.signUpBut)

        loginBut.setOnClickListener {

            var email = findViewById<EditText>(R.id.signInEmail).text.toString()
            var password = findViewById<EditText>(R.id.signInPassword).text.toString()

            if (email == "" || password == "") {
                makeText(this, "Please Input an Email & Password", LENGTH_SHORT).show()

            } else {
                i(TAG, "Sign In Initiated. Hang on!")


                //Ensures that a different user is not logged in
                //if another user is logged in, they are logged out before proceeding
                if (auth.currentUser?.email.toString() != email && auth.currentUser?.email.toString() != "null") {
                    i(TAG, "Signing " + user?.email + " out.")
                    auth.signOut()
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            user = auth.currentUser
                            makeText(this, "Sign in Successful. Welcome!", LENGTH_SHORT).show()
                            createMainIntent()
                        } else {
                            val e = task.exception as FirebaseAuthException?
                            makeText(
                                this,
                                "Failed Sign In: " + e!!.message,
                                LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        signUpBut.setOnClickListener {
            createSignUpIntent()

        }

    }

    private fun sendVerificationEmail() {
        doAsync {
            user?.sendEmailVerification()!!.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    d(TAG, "Verification Email Sent - " + user?.email)
                } else {
                    e(TAG, (task.exception as FirebaseException).message.toString())
                }
            }
        }
        makeText(this, "Please Verify Your Email!", LENGTH_SHORT).show()


    }

    private fun createMainIntent() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun createSignUpIntent() {
        startActivity(Intent(this, SignUp::class.java))
    }

}

