package org.aossie.agoraandroid.ui.fragments.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.fragment_welcome.view.fb_login_btn
import kotlinx.android.synthetic.main.fragment_welcome.view.progress_bar
import kotlinx.android.synthetic.main.fragment_welcome.view.signin_btn
import kotlinx.android.synthetic.main.fragment_welcome.view.signup_btn
import org.aossie.agoraandroid.R
import org.aossie.agoraandroid.data.db.PreferenceProvider
import org.aossie.agoraandroid.ui.fragments.auth.AuthListener
import org.aossie.agoraandroid.ui.fragments.auth.login.LoginViewModel
import org.aossie.agoraandroid.utilities.hide
import org.aossie.agoraandroid.utilities.hideActionBar
import org.aossie.agoraandroid.utilities.show
import org.aossie.agoraandroid.utilities.snackbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class WelcomeFragment
@Inject
constructor(
  private val viewModelFactory: ViewModelProvider.Factory,
    private val prefs: PreferenceProvider
) : Fragment(), AuthListener {

  private var callbackManager: CallbackManager? = null
  private val loginViewModel: LoginViewModel by viewModels {
    viewModelFactory
  }
  private lateinit var rootView: View
//  var accessTokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
//    override fun onCurrentAccessTokenChanged(
//      oldAccessToken: AccessToken,
//      currentAccessToken: AccessToken?
//    ) {
//      if (currentAccessToken == null) {
//        Toast.makeText(context, "User Logged Out", Toast.LENGTH_SHORT)
//            .show()
//      } else {
//        val facebookAccessToken = currentAccessToken.token
//        Log.d("friday", facebookAccessToken)
//        loginViewModel.facebookLogInRequest(facebookAccessToken)
//      }
//    }
//  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.fragment_welcome, container, false)
    hideActionBar()

    loginViewModel.authListener = this

    callbackManager = Factory.create()

    LoginManager.getInstance()
        .registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
              override fun onSuccess(loginResult: LoginResult?) {
                Log.d("friday", "Success")
                prefs.setFacebookAccessToken(loginResult!!.accessToken.token)
                loginViewModel.facebookLogInRequest(loginResult.accessToken.token)
              }

              override fun onCancel() {
                Toast.makeText(context, "Login Cancel", Toast.LENGTH_LONG)
                    .show()
              }

              override fun onError(exception: FacebookException) {
                Toast.makeText(context, exception.message, Toast.LENGTH_LONG)
                    .show()
              }
            })
    rootView.fb_login_btn.setOnClickListener {
      LoginManager.getInstance()
          .logInWithReadPermissions(
              activity,
              listOf("email" , "public_profile")
          )
    }
    rootView.signin_btn.setOnClickListener {
      Navigation.findNavController(rootView)
          .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
    }
    rootView.signup_btn.setOnClickListener {
      Navigation.findNavController(rootView)
          .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment())
    }

    return rootView
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    Log.d("friday", "Activity result")
    callbackManager!!.onActivityResult(requestCode, resultCode, data)
  }

  override fun onSuccess(message: String?) {
    rootView.progress_bar.hide()
    Navigation.findNavController(rootView)
        .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToHomeFragment())
  }

  override fun onStarted() {
    rootView.progress_bar.show()
  }

  override fun onFailure(message: String) {
    rootView.progress_bar.hide()
    rootView.snackbar(message)
  }

}
