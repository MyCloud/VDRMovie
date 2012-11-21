package com.androidbook.btdt.hour6;

import android.os.Bundle;
import android.util.Log;

public class QuizSplashActivity extends QuizActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_splash);
		Log.i("test", "create");
	}

}
