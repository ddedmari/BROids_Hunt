package com.hackerhunt.newshunt;

import android.app.Activity;
import android.os.Bundle;

public class AnimatedActivity extends Activity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    //opening transition animations
    overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    //closing transition animations
    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
  }
}