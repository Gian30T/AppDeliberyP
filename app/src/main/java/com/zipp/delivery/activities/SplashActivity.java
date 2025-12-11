package com.zipp.delivery.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zipp.delivery.R;
import com.zipp.delivery.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoIcon;
    private TextView logoText;
    private TextView tagline;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        initViews();
        startAnimations();
    }

    private void initViews() {
        logoIcon = findViewById(R.id.logo_icon);
        logoText = findViewById(R.id.logo_text);
        tagline = findViewById(R.id.tagline);

        // Set initial states
        logoIcon.setAlpha(0f);
        logoIcon.setScaleX(0.3f);
        logoIcon.setScaleY(0.3f);

        logoText.setAlpha(0f);
        logoText.setTranslationY(50f);

        tagline.setAlpha(0f);
        tagline.setTranslationY(30f);
    }

    private void startAnimations() {
        // Logo icon animation
        ObjectAnimator iconAlpha = ObjectAnimator.ofFloat(logoIcon, "alpha", 0f, 1f);
        ObjectAnimator iconScaleX = ObjectAnimator.ofFloat(logoIcon, "scaleX", 0.3f, 1f);
        ObjectAnimator iconScaleY = ObjectAnimator.ofFloat(logoIcon, "scaleY", 0.3f, 1f);

        AnimatorSet iconSet = new AnimatorSet();
        iconSet.playTogether(iconAlpha, iconScaleX, iconScaleY);
        iconSet.setDuration(600);
        iconSet.setInterpolator(new OvershootInterpolator(1.5f));

        // Logo text animation
        ObjectAnimator textAlpha = ObjectAnimator.ofFloat(logoText, "alpha", 0f, 1f);
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(logoText, "translationY", 50f, 0f);

        AnimatorSet textSet = new AnimatorSet();
        textSet.playTogether(textAlpha, textTranslation);
        textSet.setDuration(500);
        textSet.setInterpolator(new AccelerateDecelerateInterpolator());
        textSet.setStartDelay(300);

        // Tagline animation
        ObjectAnimator taglineAlpha = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f);
        ObjectAnimator taglineTranslation = ObjectAnimator.ofFloat(tagline, "translationY", 30f, 0f);

        AnimatorSet taglineSet = new AnimatorSet();
        taglineSet.playTogether(taglineAlpha, taglineTranslation);
        taglineSet.setDuration(400);
        taglineSet.setInterpolator(new AccelerateDecelerateInterpolator());
        taglineSet.setStartDelay(600);

        // Start all animations
        iconSet.start();
        textSet.start();
        taglineSet.start();

        // Navigate after animations
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, 2500);
    }

    private void navigateToNextScreen() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}




