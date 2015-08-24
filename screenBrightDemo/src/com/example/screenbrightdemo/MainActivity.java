package com.example.screenbrightdemo;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {
	int a=255;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		a=getScreenBrightness();
		Toast.makeText(MainActivity.this,"当前亮度"+getScreenBrightness()+":当前模式为"+(getScreenMode()==1?"自动调节屏幕亮度 ":"手动调节屏幕亮度 "), 0).show();
	}

	public void liang(View v) {
		a+=50;
		System.out.println(a);
		if(a>255){
			a=255;
			saveScreenBrightness(a);
			Toast.makeText(MainActivity.this,"已到最大", 0).show();
			return;
		}
		saveScreenBrightness(a);
		setScreenBrightness(a);
		Toast.makeText(MainActivity.this,"当前亮度"+getScreenBrightness()+":当前模式为"+(getScreenMode()==1?"自动调节屏幕亮度 ":"手动调节屏幕亮度 "), 0).show();
		
	}

	public void an(View v) {
		a-=50;
		System.out.println(a);
		if(a<0){
			a=0;
			saveScreenBrightness(a);
			Toast.makeText(MainActivity.this,"已到最小", 0).show();
			return;
		}
		saveScreenBrightness(a);
		setScreenBrightness(a);
		
		Toast.makeText(MainActivity.this,"当前亮度"+getScreenBrightness()+":当前模式为"+(getScreenMode()==1?"自动调节屏幕亮度 ":"手动调节屏幕亮度 "), 0).show();
	}

	/** 获得当前屏幕亮度的模式     
	 * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度 
	 */
	private int getScreenMode() {
		int screenMode = 0;
		try {
			screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (Exception localException) {

		}
		return screenMode;
	}

	/** 
	 * 获得当前屏幕亮度值  0--255 
	 */
	private int getScreenBrightness() {
		int screenBrightness = 255;
		try {
			screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {

		}
		return screenBrightness;
	}
	
	/** 
     * 设置当前屏幕亮度的模式     
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度 
     */  
      private void setScreenMode(int paramInt){  
        try{  
          Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);  
        }catch (Exception localException){  
          localException.printStackTrace();  
        }  
      }  
      
      /** 
       * 设置当前屏幕亮度值  0--255 
       */  
      private void saveScreenBrightness(int paramInt){  
        try{  
          Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);  
        }  
        catch (Exception localException){  
          localException.printStackTrace();  
        }  
      }  
      
      /** 
       * 保存当前的屏幕亮度值，并使之生效 
       */  
      private void setScreenBrightness(int paramInt){  
        Window localWindow = getWindow();  
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();  
        float f = paramInt / 255.0F;  
        localLayoutParams.screenBrightness = f;  
        localWindow.setAttributes(localLayoutParams);  
      }  
      
      
}
