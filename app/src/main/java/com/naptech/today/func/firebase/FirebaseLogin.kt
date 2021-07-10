package com.naptech.today.func.firebase

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.naptech.today.R
import com.naptech.today.func.NaptechActivity

// TODO("클래스 구조 개선 -> 싱글톤으로?")
class FirebaseLogin(private val activity: NaptechActivity, private val onLogin: ((Boolean) -> Unit)? = null) {

    private lateinit var firebaseAuth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null

    val user: FirebaseUser?; get() = firebaseAuth.currentUser

    fun getInstance(): FirebaseLogin {
        firebaseAuth = FirebaseAuth.getInstance()

        if (googleSignInClient == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(activity, gso)
        }

        return this
    }

// ------------

    enum class LoginOption { ANONYMOUSLY, GOOGLE, APPLE }

    fun login(option: LoginOption) {
        when(option) {
            LoginOption.ANONYMOUSLY -> {
                firebaseAuth.signInAnonymously().addOnCompleteListener(activity) {
                    onLogin?.invoke(it.isSuccessful)
                }.addOnFailureListener {
                    TODO("ERROR!")
                }
            }
            LoginOption.GOOGLE -> {
                googleLoginResult.launch(googleSignInClient!!.signInIntent)
            }
            LoginOption.APPLE -> {
                TODO()
            }
        }
    }

    fun logout() {
        googleSignInClient?.signOut()
        firebaseAuth.signOut()
    }

// ------------

    private val googleLoginResult = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val googleAccount = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
            // 익명 계정에 Google 연결
            user!!.linkWithCredential(credential).addOnCompleteListener(activity) { linkTask ->
                if (linkTask.isSuccessful) {
                    user!!.updateProfile(UserProfileChangeRequest.Builder().apply {
                        displayName = task.result!!.displayName
                        photoUri = task.result!!.photoUrl
                    }.build()).addOnCompleteListener { updateTask ->
                        onLogin?.invoke(updateTask.isSuccessful)
                    }
                } else {
                    user!!.delete().addOnCompleteListener {
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { googleTask ->
                            onLogin?.invoke(googleTask.isSuccessful)
                        }
                    }
                }
            }
        } catch (e: ApiException) {
            onLogin?.invoke(false)
        }
    }

}