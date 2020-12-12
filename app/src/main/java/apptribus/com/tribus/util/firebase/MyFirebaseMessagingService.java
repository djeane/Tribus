package apptribus.com.tribus.util.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuPresenter;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserPresenter;
import apptribus.com.tribus.activities.detail_add_talker.DetailAddTalkerActivity;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.detail_tribu_add_followers.DetailTribuAddFollowersActivity;
import apptribus.com.tribus.activities.main_activity.mpv.MainPresenter;
import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static apptribus.com.tribus.util.Constantes.FROM_NOTIFICATION;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 5/28/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /*if(InvitationRequestUserPresenter.isOpen
                && remoteMessage.getData().size() > 0
                && remoteMessage.getData().get("id").equals("1")){

            Intent intent = new Intent(this, DetailAddTalkerActivity.class);
            startActivity(intent);

        }
        else*/
        /*
        * NOTIFICATION = ADD NEW TALKER
        * params: title, message, imageUrl
        */
        if (remoteMessage.getData().get("id").equals("1")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String idTalker = remoteMessage.getData().get(CONTACT_ID);

            Bitmap bitmap = null;
            try {
                bitmap = Picasso
                        .with(this)
                        .load(remoteMessage.getData().get("imageUrl"))
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, DetailAddTalkerActivity.class);
            intent.putExtra(FROM_NOTIFICATION, "yes");
            intent.putExtra(CONTACT_ID, idTalker);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //Spannable sb = new SpannableString(message);
            //sb.setSpan(new ForegroundColorSpan(getColor(R.color.primary_text)),
              //      0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            inboxStyle.addLine(message);

            //NOFICATION GROUP
            NotificationCompat.Builder groupBuilder =
                    new NotificationCompat.Builder(this)
                            .setColor(getResources().getColor(R.color.colorAccent))
                            .setContentTitle(title)
                            .setContentText(message)
                            .setGroupSummary(true)
                            .setSmallIcon(R.mipmap.ic_icon_notification)
                            .setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setGroup("GROUP_1")
                            .setStyle(inboxStyle)
                            .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.mipmap.ic_icon_notification)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)
                    .setGroup("GROUP_1")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(1, groupBuilder.build());
            notificationManager.notify(idNotification, builder.build());

        }


        /*
        * NOTIFICATION = NEW TALKER ACCEPTED
        * params: title, message, imageUrl
        */
        if (remoteMessage.getData().get("id").equals("2")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String idTalker = remoteMessage.getData().get(CONTACT_ID);

            Bitmap bitmap = null;
            try {
                bitmap = Picasso
                        .with(this)
                        .load(remoteMessage.getData().get("imageUrl"))
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, ChatUserActivity.class);
            intent.putExtra(FROM_NOTIFICATION, "yes");
            intent.putExtra(CONTACT_ID, idTalker);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //Spannable sb = new SpannableString(message);
            //sb.setSpan(new ForegroundColorSpan(getColor(R.color.primary_text)),
              //      0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            inboxStyle.addLine(message);

            NotificationCompat.Builder groupBuilder =
                    new NotificationCompat.Builder(this)
                            .setColor(getResources().getColor(R.color.colorAccent))
                            .setContentTitle(title)
                            .setContentText(message)
                            .setGroupSummary(true)
                            .setGroup("GROUP_2")
                            .setSmallIcon(R.mipmap.ic_icon_notification)
                            .setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setStyle(inboxStyle)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.mipmap.ic_icon_notification)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setGroup("GROUP_2")
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, groupBuilder.build());
            notificationManager.notify(idNotification, builder.build());

        }


        /*
        * NOTIFICATION = ADD NEW FOLLOWER
        * params: title, message, imageUrl
        */
        if (remoteMessage.getData().get("id").equals("3")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String tribuUniqueName = remoteMessage.getData().get(TRIBU_UNIQUE_NAME);


            Bitmap bitmap = null;
            try {
                bitmap = Picasso
                        .with(this)
                        .load(remoteMessage.getData().get("imageUrl"))
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, DetailTribuAddFollowersActivity.class);
            intent.putExtra(FROM_NOTIFICATION, "yes");
            intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //Spannable sb = new SpannableString(message);
            //sb.setSpan(new ForegroundColorSpan(getColor(R.color.primary_text)),
              //      0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            inboxStyle.addLine(message);

            NotificationCompat.Builder groupBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setColor(getResources().getColor(R.color.colorAccent))
                            .setGroupSummary(true)
                            .setSmallIcon(R.mipmap.ic_icon_notification)
                            .setGroup("GROUP_3")
                            .setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setStyle(inboxStyle)
                            .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.mipmap.ic_icon_notification)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)
                    .setGroup("GROUP_3")
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(3, groupBuilder.build());
            notificationManager.notify(idNotification, builder.build());

        }



        /*
        * NOTIFICATION = NEW FOLLOWER ACCEPTED
        * params: title, message, imageUrl
        */
        if (remoteMessage.getData().get("id").equals("4")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String tribuUniqueName = remoteMessage.getData().get(TRIBU_UNIQUE_NAME);


            Bitmap bitmap = null;
            try {
                bitmap = Picasso
                        .with(this)
                        .load(remoteMessage.getData().get("imageUrl"))
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, ChatTribuActivity.class);
            intent.putExtra(FROM_NOTIFICATION, "yes");
            intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //Spannable sb = new SpannableString(message);
            //sb.setSpan(new ForegroundColorSpan(getColor(R.color.primary_text)),
            //        0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            inboxStyle.addLine(message);

            NotificationCompat.Builder groupBuilder =
                    new NotificationCompat.Builder(this)
                            .setColor(getResources().getColor(R.color.colorAccent))
                            .setContentTitle(title)
                            .setContentText(message)
                            .setGroupSummary(true)
                            .setGroup("GROUP_4")
                            .setSmallIcon(R.mipmap.ic_icon_notification)
                            .setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setStyle(inboxStyle)
                            .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.mipmap.ic_icon_notification)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)
                    .setGroup("GROUP_4")
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(4, groupBuilder.build());
            notificationManager.notify(idNotification, builder.build());

        }


        /*
        * NOTIFICATION = NEW MESSAGE SENT - TALKER
        * params: title, message, imageUrl
        */
        if ((!ChatUserPresenter.isOpen)
                && remoteMessage.getData().get("id").equals("5")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String talkerId = remoteMessage.getData().get(CONTACT_ID);

            if(talkerId.equals(mAuth.getCurrentUser().getUid())){
                return;
            }
            else {

                Bitmap bitmap = null;
                try {
                    bitmap = Picasso
                            .with(this)
                            .load(remoteMessage.getData().get("imageUrl"))
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ChatUserActivity.class);
                intent.putExtra(FROM_NOTIFICATION, "yes");
                intent.putExtra(CONTACT_ID, talkerId);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(title);
                //Spannable sb = new SpannableString(message);
                //sb.setSpan(new ForegroundColorSpan(getColor(R.color.primary_text)),
                  //      0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                inboxStyle.addLine(message);

                NotificationCompat.Builder groupBuilder =
                        new NotificationCompat.Builder(this)
                                .setColor(getResources().getColor(R.color.colorAccent))
                                .setContentTitle(title)
                                .setContentText(message)
                                .setSmallIcon(R.mipmap.ic_icon_notification)
                                .setLargeIcon(bitmap)
                                .setAutoCancel(true)
                                .setGroupSummary(true)
                                .setGroup("GROUP_5")
                                .setStyle(inboxStyle)
                                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSmallIcon(R.mipmap.ic_icon_notification)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .setGroup("GROUP_5")
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(5, groupBuilder.build());
                notificationManager.notify(idNotification, builder.build());
            }
        }

        /*
        * NOTIFICATION = NEW MESSAGE SENT - TRIBUS
        * params: title, message, imageUrl
        */
        if ((!ChatTribuPresenter.isOpen)
                && remoteMessage.getData().get("id").equals("6")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("titleMessage"); //tribus name
            String topicMessage = remoteMessage.getData().get("topicMessage");
            String nameParticipant = remoteMessage.getData().get("nameParticipant");
            String message = remoteMessage.getData().get("message");
            String tribuUniqueName = remoteMessage.getData().get("tribuUniqueName");
            String idTopic = remoteMessage.getData().get("idTopic");
            String idParticipant = remoteMessage.getData().get("idParticipant");

            if(idParticipant.equals(mAuth.getCurrentUser().getUid())){
                return;
            }
            else {

                String appendMessage = nameParticipant + ": " + message;

                Bitmap bitmap = null;
                try {
                    bitmap = Picasso
                            .with(this)
                            .load(remoteMessage.getData().get("imageUrl"))
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ChatTribuActivity.class);
                intent.putExtra(FROM_NOTIFICATION, "yes");
                intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);
                intent.putExtra(TOPIC_KEY, idTopic);


                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(title);
                /*inboxStyle.setSummaryText(topicMessage);
                Spannable sb = new SpannableString(appendMessage);
                sb.setSpan(new ForegroundColorSpan(getColor(R.color.primary_text)),
                        0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
                inboxStyle.addLine(message);

                NotificationCompat.Builder groupBuilder =
                        new NotificationCompat.Builder(this)
                                .setColor(getResources().getColor(R.color.colorAccent))
                                .setContentTitle(title)
                                .setContentText(message)
                                .setGroupSummary(true)
                                .setGroup("GROUP_6")
                                .setAutoCancel(true)
                                .setStyle(inboxStyle)
                                .setSmallIcon(R.mipmap.ic_icon_notification)
                                .setLargeIcon(bitmap)
                                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentTitle(title)
                        .setContentText(appendMessage)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSmallIcon(R.mipmap.ic_icon_notification)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .setGroup("GROUP_6")
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.notify(6, groupBuilder.build());
                notificationManager.notify(idNotification, builder.build());
            }
        }



        /*
        * NOTIFICATION = NEW TRIBUS
        */
        if ((!MainPresenter.isOpen)
                && remoteMessage.getData().get("id").equals("7")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title"); //Criaram uma nova mTribu
            String message = remoteMessage.getData().get("message");
            String tribuUniqueName = remoteMessage.getData().get("tribuUniqueName");
            String idAdmin = remoteMessage.getData().get("idAdmin");

            if(idAdmin.equals(mAuth.getCurrentUser().getUid())){
                return;
            }
            else {

                Bitmap bitmap = null;
                try {
                    bitmap = Picasso
                            .with(this)
                            .load(remoteMessage.getData().get("imageUrl"))
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ProfileTribuUserActivity.class);
                intent.putExtra(FROM_NOTIFICATION, "yes");
                intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);


                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(title);
                inboxStyle.addLine(message);

                NotificationCompat.Builder groupBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setColor(getResources().getColor(R.color.colorAccent))
                                .setSmallIcon(R.mipmap.ic_icon_notification)
                                .setGroupSummary(true)
                                .setGroup("GROUP_7")
                                .setLargeIcon(bitmap)
                                .setAutoCancel(true)
                                .setStyle(inboxStyle)
                                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentTitle(title)
                        .setSmallIcon(R.mipmap.ic_icon_notification)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .setGroup("GROUP_7")
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.notify(7, groupBuilder.build());
                notificationManager.notify(idNotification, builder.build());
            }
        }


        /*
        * NOTIFICATION = NEW PARTICIPANT
        */
        if (remoteMessage.getData().get("id").equals("8")
                && remoteMessage.getData().size() > 0) {

            int idNotification = Integer.parseInt(remoteMessage.getData().get("id"));
            String title = remoteMessage.getData().get("title"); //Criaram uma nova mTribu
            String message = remoteMessage.getData().get("message");
            String tribuUniqueName = remoteMessage.getData().get("mTribu");
            String idParticipant = remoteMessage.getData().get("idParticipant");

            if(idParticipant.equals(mAuth.getCurrentUser().getUid())){
                return;
            }
            else {

                Bitmap bitmap = null;
                try {
                    bitmap = Picasso
                            .with(this)
                            .load(remoteMessage.getData().get("imageUrl"))
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, DetailTalkerActivity.class);
                intent.putExtra(FROM_NOTIFICATION, "yes");
                intent.putExtra(CONTACT_ID, idParticipant);


                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(title);
                inboxStyle.addLine(message);

                NotificationCompat.Builder groupBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(title)
                                .setSmallIcon(R.mipmap.ic_icon_notification)
                                .setColor(getResources().getColor(R.color.colorAccent))
                                .setContentText(message)
                                .setLargeIcon(bitmap)
                                .setAutoCancel(true)
                                .setGroupSummary(true)
                                .setGroup("GROUP_8")
                                .setStyle(inboxStyle)
                                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentTitle(title)
                        .setSmallIcon(R.mipmap.ic_icon_notification)
                        .setContentText(message)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .setGroup("GROUP_8")
                        .setStyle(inboxStyle)
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.notify(8, groupBuilder.build());
                notificationManager.notify(idNotification, builder.build());
            }
        }

    }

}
