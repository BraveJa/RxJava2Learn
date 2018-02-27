package com.zzw.myapplication.rxjava2learn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zzw.myapplication.rxjava2learn.day.Eight;
import com.zzw.myapplication.rxjava2learn.day.Five;
import com.zzw.myapplication.rxjava2learn.day.Four;
import com.zzw.myapplication.rxjava2learn.day.Nine;
import com.zzw.myapplication.rxjava2learn.day.One;
import com.zzw.myapplication.rxjava2learn.day.Seven;
import com.zzw.myapplication.rxjava2learn.day.Six;
import com.zzw.myapplication.rxjava2learn.day.Ten;
import com.zzw.myapplication.rxjava2learn.day.Three;
import com.zzw.myapplication.rxjava2learn.day.Two;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn1).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
		findViewById(R.id.btn3).setOnClickListener(this);
		findViewById(R.id.btn4).setOnClickListener(this);
		findViewById(R.id.btn5).setOnClickListener(this);
		findViewById(R.id.btn6).setOnClickListener(this);
		findViewById(R.id.btn7).setOnClickListener(this);
		findViewById(R.id.btn8).setOnClickListener(this);
		findViewById(R.id.btn9).setOnClickListener(this);
		findViewById(R.id.btn10).setOnClickListener(this);


	}


	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.btn1:
				new One();
				break;
			case R.id.btn2:
				new Two();
				break;
			case R.id.btn3:
				new Three();
				break;
			case R.id.btn4:
				new Four();
				break;
			case R.id.btn5:
				new Five();
				break;
			case R.id.btn6:
				new Six();
				break;
			case R.id.btn7:
				new Seven();
				break;
			case R.id.btn8:
				new Eight();
				break;
			case R.id.btn9:
				new Nine();
				break;
			case R.id.btn10:
				new Ten();
				break;
			default:
				break;
		}
	}
}
