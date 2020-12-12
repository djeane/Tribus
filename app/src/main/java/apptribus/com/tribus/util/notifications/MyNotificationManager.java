package apptribus.com.tribus.util.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import apptribus.com.tribus.R;

/**
 * Created by User on 5/28/2017.
 */

public class MyNotificationManager {
    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mContext;

    public MyNotificationManager(Context context){
        this.mContext = context;
    }

    //O método mostrará uma pequena notificação
    // parâmetros são título para o título da mensagem, mensagem para o texto da mensagem e uma intenção que será aberta
    // quando o usário tocar na notificação.
    public void showSmallNotification(String title, String message, Intent intent){
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(mContext, ID_SMALL_NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        Notification notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }
}
