package com.radiomatalelaki.util;

/**
 * Created by rianpradana on 5/30/17.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StreamingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String param = intent.getAction();
        if (param.equals("exit")) {
            Intent service1 = new Intent(context, StreamingService.class);
            context.stopService(service1);
        } else if (param.equals("stop")) {
            context.sendBroadcast(new Intent("stop"));
        } else if (param.equals("start")) {
            context.sendBroadcast(new Intent("start"));
        }
    }
}