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
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
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
    Button fb,google_Login,linkedin,customTwitter;

    /*Google plus*/

    //Signin button
    private SignInButton signInButton;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;


    private static final String TWITTER_KEY = "QIhrlYhWgrSFqcsfuAFa7KQmK";
    private static final String TWITTER_SECRET = "lPjGpKNRtIr9w8Lp8PLd8JzH8O63PKSrduKijNHlbi0mmyV8nc";

    private TwitterLoginButton loginButton;
    //TwitterSession session;

    //Linkedin
    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host+ "/v1/people/~:(id,email-address,formatted-name,phone-numbers,picture-urls::(original))";


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
        customTwitter=(Button) findViewById(R.id.customTwitter);
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


//------------------------------------------------------------------------
                /*session = Twitter.getSessionManager().getActiveSession();

                Twitter.getApiClient(session).getAccountService()
                        .verifyCredentials(true, false, new Callback<User>() {

                            @Override
                            public void success(Result<User> userResult) {

                                User user = userResult.data;

                                System.out.println("screenName : "+user.screenName);
                                System.out.println("name : "+user.name);
                                System.out.println("location : "+user.location);
                                System.out.println("timeZone : "+user.timeZone);
                                System.out.println("description : "+user.description);
                                System.out.println("id : "+user.id);
                                System.out.println("Email : "+user.email);



                                twitterImage = user.profileImageUrl;
                                screenname = user.screenName;
                                username = user.name;
                                location = user.location;
                                timeZone = user.timeZone;
                                description = user.description;


                                *//*user_picture = (ImageView) findViewById(R.id.profile_pic);
                                Picasso.with(getApplicationContext()).load(twitterImage.toString())
                                        .into(user_picture);
                                *//*

                            }

                            @Override
                            public void failure(TwitterException e) {
                            }

                        });
*/


            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        //Twitter end

        google_Login = (Button)findViewById(R.id.google_pluse);
        linkedin=(Button) findViewById(R.id.linkedin);

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

        //Linkedin
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, responseCode, data);

        linkededinApiHelper();

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
        if (v == customTwitter)
        {
            loginButton.performClick();
        }
        if (v == linkedin)
        {
            linkden();
        }

    }


    private void linkden()
    {

        LISessionManager.getInstance(getApplicationContext())
                .init(this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        Toast.makeText(getApplicationContext(), "success" +
                                        LISessionManager
                                                .getInstance(getApplicationContext())
                                                .getSession().getAccessToken().toString(),
                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                        Toast.makeText(getApplicationContext(), "failed "
                                        + error.toString(),
                                Toast.LENGTH_LONG).show();
                        System.out.println("Exception : " + error.toString());
                    }
                }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void linkededinApiHelper() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {

                    JSONObject response=result.getResponseDataAsJson();

                    System.out.println("Linkedin Response :" +response);

                    //response.getString("id");
                    System.out.println("Linkedin ID ------------------->"+response.getString("id"));



                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Home page Exception :" + e.getMessage());
                }
            }

            @Override
            public void onApiError(LIApiError error) {

            }
        });
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
