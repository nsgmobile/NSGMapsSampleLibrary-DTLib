package com.nsg.mapssample;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.nsg.nsgmapslibrary.Classes.HomeFragment;
public class SampleActivity extends Activity {
    AnimatorSet set;
    ImageView imgView;
    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_vessel_sample);
        imgView =(ImageView)findViewById(R.id.iv_wave);
       // setAnim1();
        AnimateBell();
        //rippleBackground.startRippleAnimation();
    }
    private void setAnim1() {
        AnimationSet as = new AnimationSet(true);
        // Zoom in the animation to 1.4 times the center from the original
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        // Gradual painting
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        as.setDuration(800);
        as.addAnimation(scaleAnimation);
        as.addAnimation(alphaAnimation);
        imgView.startAnimation(as);
    }

    public void AnimateBell() {
        Animation shake = AnimationUtils.loadAnimation(SampleActivity.this, R.anim.shake);
        shake.setDuration(800);
        shake.setRepeatCount(Animation.INFINITE);
        imgView.setAnimation(shake);
    }
    public void Animate(){
       // ObjectAnimator objAnimator = ObjectAnimator.ofFloat(mCircle, "alpha",0f,1f);
      //  objAnimator.setDuration(1000);
      //  objAnimator.setRepeatMode(Animation.REVERSE);
      //  objAnimator.setRepeatCount(Animation.INFINITE);
      //  objAnimator.start();
    }
}
