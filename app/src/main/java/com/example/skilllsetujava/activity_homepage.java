package com.example.skilllsetujava;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class activity_homepage extends AppCompatActivity {

    private MotionLayout rootMotionLayout;
    private TextView btnStart;
    private TextView txtHelper;

    private View selectedInterviewCard = null;
    private TextView selectedRolePill = null;

    private final List<View> interviewCards = new ArrayList<>();
    private final List<TextView> rolePills = new ArrayList<>();

    // Typing dots
    private View dot1, dot2, dot3;

    // Progress ring
    private ProgressBar progressConfidence;

    // Coming soon shimmer
    private LinearLayout cardVoice, cardEmotion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        rootMotionLayout = findViewById(R.id.rootMotionLayout);
        btnStart = findViewById(R.id.btnStart);
        txtHelper = findViewById(R.id.txtHelper);

        // Header / Lottie
        LottieAnimationView lottieOrbit = findViewById(R.id.lottieOrbit);
        lottieOrbit.playAnimation(); // particles orbit

        setupInterviewCards();
        setupRolePills();
        setupTypingDots();
        setupConfidenceRing();
        setupComingSoonShimmer();

        // Trigger entry animation
        rootMotionLayout.post(() ->
                rootMotionLayout.transitionToEnd()
        );

        // Loop breathing logo via logo transition
        rootMotionLayout.postDelayed(() ->
                rootMotionLayout.transitionToState(R.id.logo_large), 600);

        setupCtaGlowPulse();
    }

    private void setupInterviewCards() {
        View cardHr = findViewById(R.id.cardHr);
        View cardTech = findViewById(R.id.cardTechnical);
        View cardApt = findViewById(R.id.cardAptitude);
        View cardMixed = findViewById(R.id.cardMixed);

        interviewCards.add(cardHr);
        interviewCards.add(cardTech);
        interviewCards.add(cardApt);
        interviewCards.add(cardMixed);

        OvershootInterpolator overshoot = new OvershootInterpolator();

        for (View card : interviewCards) {
            card.setScaleX(0.9f);
            card.setScaleY(0.9f);
            card.setAlpha(0f);

            // Staggered appearance
            card.postDelayed(() -> {
                card.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(350)
                        .setInterpolator(overshoot)
                        .start();
            }, 200 + interviewCards.indexOf(card) * 70L);

            card.setOnClickListener(v -> {
                handleInterviewCardSelected(v);
                maybeEnableCta();
            });
        }
    }

    private void handleInterviewCardSelected(View card) {
        // De-emphasize previous
        if (selectedInterviewCard != null && selectedInterviewCard != card) {
            selectedInterviewCard.animate()
                    .translationZ(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(new AnticipateInterpolator())
                    .start();

            selectedInterviewCard.setBackgroundResource(R.drawable.bg_card_glass);
        }

        // Emphasize new selection: elevate, glow, subtle scale
        card.setBackgroundResource(R.drawable.bg_card_glass); // could change to a glow variant
        card.animate()
                .translationZ(12f)
                .scaleX(1.03f)
                .scaleY(1.03f)
                .setDuration(260)
                .setInterpolator(new OvershootInterpolator())
                .start();

        selectedInterviewCard = card;
    }

    private void setupRolePills() {
        TextView pillRoleSde = findViewById(R.id.pillRoleSde);
        TextView pillRoleData = findViewById(R.id.pillRoleData);
        TextView pillRoleWeb = findViewById(R.id.pillRoleWeb);
        TextView pillRoleCore = findViewById(R.id.pillRoleCore);

        rolePills.add(pillRoleSde);
        rolePills.add(pillRoleData);
        rolePills.add(pillRoleWeb);
        rolePills.add(pillRoleCore);

        for (TextView pill : rolePills) {
            pill.setOnClickListener(v -> {
                handleRoleSelected(pill);
                maybeEnableCta();
            });
        }
    }

    private void handleRoleSelected(TextView pill) {
        // Reset previous
        if (selectedRolePill != null && selectedRolePill != pill) {
            selectedRolePill.setBackgroundResource(R.drawable.bg_pill_role_unselected);
            selectedRolePill.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start();
        }

        pill.setBackgroundResource(R.drawable.bg_pill_role_selected);

        // Micro bounce
        pill.setScaleX(0.9f);
        pill.setScaleY(0.9f);
        pill.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(220)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> pill.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start())
                .start();

        selectedRolePill = pill;
    }

    private void maybeEnableCta() {
        boolean ready = (selectedInterviewCard != null && selectedRolePill != null);

        if (ready && !btnStart.isEnabled()) {
            btnStart.setEnabled(true);
            // Morph background via ObjectAnimator color if you prefer, here we swap drawable and animate pulse
            btnStart.setBackgroundResource(R.drawable.bg_cta_button);
            txtHelper.setText("You’re all set. Start your AI mock interview!");

            startCtaGlowLoop();
        }
    }

    private void setupCtaGlowPulse() {
        // Initially subtle breathing even when disabled (smaller)
        btnStart.setScaleX(0.98f);
        btnStart.setScaleY(0.98f);
    }

    private void startCtaGlowLoop() {
        // Button glow pulse loop
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(btnStart, View.SCALE_X, 1f, 1.04f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(btnStart, View.SCALE_Y, 1f, 1.04f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(btnStart, View.SCALE_X, 1.04f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(btnStart, View.SCALE_Y, 1.04f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleUpX).with(scaleUpY);
        set.play(scaleDownX).with(scaleDownY).after(scaleUpX);
        set.setDuration(600);
        set.setInterpolator(new OvershootInterpolator());
        set.setStartDelay(200);
        set.addListener(new SimpleAnimatorListener() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                // Loop
                if (btnStart.isEnabled()) {
                    btnStart.postDelayed(set::start, 400);
                }
            }
        });
        set.start();
    }

    private void setupTypingDots() {
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        startDotBounce(dot1, 0);
        startDotBounce(dot2, 120);
        startDotBounce(dot3, 240);
    }

    private void startDotBounce(View dot, long delay) {
        ObjectAnimator up = ObjectAnimator.ofFloat(dot, View.TRANSLATION_Y, 0f, -4f);
        ObjectAnimator down = ObjectAnimator.ofFloat(dot, View.TRANSLATION_Y, -4f, 0f);
        up.setDuration(160);
        down.setDuration(160);
        up.setInterpolator(new AnticipateInterpolator());
        down.setInterpolator(new OvershootInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(up, down);
        set.setStartDelay(delay);
        set.addListener(new SimpleAnimatorListener() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                dot.postDelayed(set::start, 260);
            }
        });
        set.start();
    }

    private void setupConfidenceRing() {
        progressConfidence = findViewById(R.id.progressConfidence);
        // Animate to target confidence to make it feel alive
        int start = 20;
        int target = 72;
        progressConfidence.setProgress(start);
        ObjectAnimator animator = ObjectAnimator.ofInt(progressConfidence, "progress", start, target);
        animator.setDuration(900);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }

    private void setupComingSoonShimmer() {
        cardVoice = findViewById(R.id.cardVoice);
        cardEmotion = findViewById(R.id.cardEmotion);

        startShimmer(cardVoice, 0);
        startShimmer(cardEmotion, 200);
    }

    private void startShimmer(View view, long delay) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.5f, 0.9f);
        alpha.setRepeatMode(ObjectAnimator.REVERSE);
        alpha.setRepeatCount(ObjectAnimator.INFINITE);
        alpha.setDuration(1400);
        alpha.setStartDelay(delay);
        alpha.start();
    }

    // Utility listener with empty implementations
    private abstract static class SimpleAnimatorListener implements android.animation.Animator.AnimatorListener {
        @Override public void onAnimationStart(android.animation.Animator animation) {}
        @Override public void onAnimationCancel(android.animation.Animator animation) {}
        @Override public void onAnimationRepeat(android.animation.Animator animation) {}
    }
}