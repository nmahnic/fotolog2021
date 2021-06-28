package com.nicomahnic.dadm.fotolog2021.ui.login.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.databinding.FragmentLoginScreenBinding
import com.nicomahnic.dadm.fotolog2021.ui.home.HomeActivity

class LoginScreenFragment : Fragment(R.layout.fragment_login_screen) {

    private lateinit var b: FragmentLoginScreenBinding
    private lateinit var edtPasswd: String
    private lateinit var edtEmail: String
    private val firebaseAuth by lazy { FirebaseAuth.getInstance()}

    companion object{
        const val GOOGLE_SIGN_IN = 100
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b = FragmentLoginScreenBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.show()

    }


    override fun onStart() {
        super.onStart()

        b.progressBar.visibility = View.GONE
        b.authLayout.visibility = View.VISIBLE

        firebaseAuth.currentUser?.let { showHome() }
        setup()
    }

    private fun setup(){
        (activity as AppCompatActivity).supportActionBar?.title = "Autenticación"

        b.btnSign.setOnClickListener {
            if(b.edtEmail.text.isNotEmpty() && b.edtPasswd.text.isNotEmpty()){
                edtEmail = b.edtEmail.text.toString()
                edtPasswd = b.edtPasswd.text.toString()
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    edtEmail,
                    edtPasswd
                ).addOnCompleteListener {
                    if(it.isSuccessful)
                        showHome()
                    else
                        it.exception?.message?.let { message -> showAlert(message) }
                }
            }else{
                showAlert("Email or Password is empty")
            }
        }

        b.btnLogin.setOnClickListener {
            if(b.edtEmail.text.isNotEmpty() && b.edtPasswd.text.isNotEmpty()){
                edtEmail = b.edtEmail.text.toString()
                edtPasswd = b.edtPasswd.text.toString()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    edtEmail,
                    edtPasswd
                ).addOnCompleteListener {
                    if(it.isSuccessful)
                        showHome()
                    else
                        it.exception?.message?.let { message -> showAlert(message) }
                }
            }else{
                showAlert("Email or Password is empty")
            }
        }

        b.btnGoogleSign.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(requireContext(),googleConf)
            googleClient.signOut() //si no se pone esto se necesita AuthUI para el logout
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                showHome()
                            }else{
                                it.exception?.message?.let { message -> showAlert(message) }
                            }
                        }
                }
            }catch (e: ApiException){
                Log.e("NM",e.toString())
                showAlert(e.toString())
            }

        }
    }

    private fun showAlert(exceptionMessage: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(exceptionMessage)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val sendIntent = Intent(context, HomeActivity::class.java)
        startActivity(sendIntent)
    }
}