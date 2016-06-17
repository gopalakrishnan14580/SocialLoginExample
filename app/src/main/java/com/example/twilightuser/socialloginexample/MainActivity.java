package com.example.twilightuser.socialloginexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    /*Facebook*/

    LoginButton login_facebook_button;
    CallbackManager callbackManager;
    Button fb,google_Login;

    /*Google plus*/

    //Signin button
    private SignInButton signInButton;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

     String TWITTER_KEY="QIhrlYhWgrSFqcsfuAFa7KQmK",TWITTER_SECRET="lPjGpKNRtIr9w8Lp8PLd8JzH8O63PKSrduKijNHlbi0mmyV8nc";

    private TwitterLoginButton loginButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //facebook Login start
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        //facebook Login end

        //Twitter

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        //Twitter start

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                System.out.println("Twitter ID -------------------------> " +session.getUserId());

                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        //Twitter end

        google_Login = (Button)findViewById(R.id.google_pluse);

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing signinbutton
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


       /* //Setting onclick listener to signing button
        signInButton.setOnClickListener(this);*/

        //facebook login Method start

        callbackManager = CallbackManager.Factory.create();
        fb = (Button) findViewById(R.id.fb);
        // Social Buttons
        login_facebook_button = (LoginButton) findViewById(R.id.login_facebook_button);

        login_facebook_button.setReadPermissions(Arrays.asList("public_profile","user_friends","email","user_birthday"));
        login_facebook_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.e("onSuccess", "--------" + loginResult.getAccessToken());
                Log.e("Token", "--------" + loginResult.getAccessToken().getToken());
                Log.e("Permision", "--------" + loginResult.getRecentlyGrantedPermissions());

                Log.e("OnGraph", "------------------------");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                Log.e("GraphResponse", "-------------" + object.toString());

                                //Log.e("GraphResponse123", "-------------" + response.toString());

                                try {

                                        String id = object.getString("id");

                                        System.out.println("FaceBook ID ---------------------------------->"+ id);



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                //=== above method is used to get the facebook values ==//

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email,first_name,last_name,location,locale,timezone");
                request.setParameters(parameters);
                request.executeAsync();



                Log.e("Total Friend in List", "----------------------");
                new GraphRequest(loginResult.getAccessToken(),"/me/friends", null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                Log.e("Friend in List", "-------------" + response.toString());
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("FacebookException", "-------------" + exception.toString());
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        //Facebook

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }

        //Google plus

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }

        //Twitter
        loginButton.onActivityResult(requestCode, responseCode, data);

    }


    public void onClick(View v) {
        if (v == fb) {
            LoginManager.getInstance().logOut();
            login_facebook_button.performClick();
        }
        if (v == google_Login) {
            //Calling signin
            signInButton.setOnClickListener(this);
            signIn();
        }
        if (v == loginButton)
        {
            loginButton.setOnClickListener(this);
        }
    }

    /*Google plus*/

    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email
            System.out.println("Google plus ID ---------------------------> " + acct.getId());
            /*System.out.println("Token -------------> "+ acct.getIdToken());
            System.out.println("ServerAuthCode -------------> "+ acct.getServerAuthCode());
            System.out.println("DisplayName -------------> " + acct.getDisplayName());
            System.out.println("Email -------------> " + acct.getEmail());
*/
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
