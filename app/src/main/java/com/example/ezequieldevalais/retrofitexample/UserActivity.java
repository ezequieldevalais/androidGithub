package com.example.ezequieldevalais.retrofitexample;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezequieldevalais.retrofitexample.model.User;
import com.example.ezequieldevalais.retrofitexample.model.githubAPI;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UserActivity extends ActionBarActivity {
    public static final String EXTRA_MESSAGE = "com.example.ezequieldevalais.retrofitexample.MESSAGE";
    private RestAdapter restAdapter;
    private UserActivity activity = this;
    public static String githubUser = null;
    private String API = "https://api.github.com";
    private Intent intent;


    private String TAG = "Eze";
    @InjectView(R.id.txtUserName) TextView txtName;
    @InjectView(R.id.txtUser) TextView txtUser;
    @InjectView(R.id.txtGithubId) TextView txtGithubId;
    @InjectView(R.id.imageGithub) ImageView imageGithub;
    @InjectView(R.id.progress_wheel) ProgressWheel progressWheel;
    @InjectView(R.id.buttonRepos) Button buttonRepos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        intent = getIntent();
        if(githubUser == null){
            githubUser = intent.getStringExtra(SelectUserActivity.EXTRA_MESSAGE);
        }
        Log.i(TAG, "github user : "+ githubUser);

        ProgressWheel wheel = new ProgressWheel(this);
        wheel.setBarColor(Color.BLUE);

        ButterKnife.inject(this);
        restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();
        getGithubUser();
    }


    @OnClick(R.id.buttonRepos)
    public void loadRepositoriesView(View v){
                Intent intent = new Intent(activity, RepositoriesActivity.class);
                intent.putExtra(EXTRA_MESSAGE, githubUser);
                startActivity(intent);
    }


    private void getGithubUser() {
        githubAPI git = restAdapter.create(githubAPI.class);

        git.getUser(githubUser, new Callback<User>() {
            public void success(User Gitmodel, Response response) {
                progressWheel.stopSpinning();
                gitHubCalbackSuccess(Gitmodel);
            }

            @Override
            public void failure(RetrofitError error) {

                Log.e(TAG, error.getMessage());
                Toast toast = Toast.makeText(activity, "User \"" + githubUser + "\" does not exist" ,Toast.LENGTH_SHORT);
                githubUser = null;
                progressWheel.stopSpinning();
                toast.show();
                finish();
            }
        });
    }

    public void gitHubCalbackSuccess(User gitmodel){
        txtName.setText(gitmodel.getName());
        txtUser.setText(gitmodel.getLogin());
        txtGithubId.setText(gitmodel.getId().toString());
        buttonRepos.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(gitmodel.getAvatarUrl())
                .into(imageGithub);
    }

}