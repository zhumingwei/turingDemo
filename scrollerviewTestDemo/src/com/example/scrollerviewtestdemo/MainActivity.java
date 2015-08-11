package com.example.scrollerviewtestdemo;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {
	private MyScrollview ms;
	private MyScrollview2 ms2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ms=(MyScrollview) findViewById(R.id.myScrollview);
		ms2=(MyScrollview2) findViewById(R.id.myScrollview2);
		initEvent();
	}
	private void initEvent() {
		ms.setOnBorderListener(new com.example.scrollerviewtestdemo.MyScrollview.OnBorderListener() {
			@Override
			public void onTop() {
				System.out.println("方法一到顶。。。");
			}
			
			@Override
			public void onBottom() {
				System.out.println("方法一到底。。。");
			}
		});
		
		ms2.setOnBorderListener(new com.example.scrollerviewtestdemo.MyScrollview2.OnBorderListener() {

			@Override
			public void onBottom() {
				System.out.println("方法二到顶。。。");
			}

			@Override
			public void onTop() {
				System.out.println("方法二到底。。。");
				
			}
			
		});
	}
}
