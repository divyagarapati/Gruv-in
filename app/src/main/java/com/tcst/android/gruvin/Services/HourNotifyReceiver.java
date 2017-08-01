package com.tcst.android.gruvin.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by TCST06 on 13-Jan-17.
 */

public class HourNotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent irecv=new Intent(context,HourNotifyService.class);
        context.startService(irecv);
    }
}
