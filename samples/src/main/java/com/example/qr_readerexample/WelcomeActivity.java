/*
 * Copyright (C) Shashank Kulkarni - Shashank.physics AT gmail DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.qr_readerexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

/**
 * Activity for welcoming user when first run.
 *
 * @author Shashank
 */
public class WelcomeActivity extends Activity {

    /** Text fade IN/OUT delay */
    private static final int ANIMATION_DELAY = 3000;

    /**
     * Fade out the welcome message and show the instruction.
     * then move the user to settings screen.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Intent intent = getIntent();
        final String uEmail = intent.getStringExtra("EXTRA_USER_EMAIL");

        final TextView welcome = (TextView) findViewById(R.id.animatedWelcome);

        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(ANIMATION_DELAY);

        welcome.startAnimation(out);

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(ANIMATION_DELAY);

        // Once the Welcome text disappears,
        // show the custom message
        out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                welcome.setText("Este usuario tiene el correo " + uEmail);
                welcome.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }
}
