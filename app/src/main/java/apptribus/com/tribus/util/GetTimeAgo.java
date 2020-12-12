package apptribus.com.tribus.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import apptribus.com.tribus.R;

/**
 * Created by User on 7/26/2017.
 */

@SuppressLint("Registered")
public class GetTimeAgo extends Application {

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(Date date, Context ctx) {

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        //MENOS DE UM MINUTO
        if (dim == 0) {
            if(ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_term_just_now);
            /*timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +
                    ctx.getResources().getString(R.string.date_util_term_a) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_minute);*/
            }
        }

        //HÁ UM MINUTO
        else if (dim == 1) {
            if(ctx != null) {
                return ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
            }
        }

        //HÁ *** MINUTOS
        else if (dim >= 2 && dim <= 44) {
            if(ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + dim + ctx.getResources().getString(R.string.date_util_unit_minutes);
            }
        }

        //HÁ UMA HORA
        else if (dim >= 45 && dim <= 89) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_unit_one_hour);
            }
        }
        /*else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                    ctx.getResources().getString(R.string.date_util_term_an)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_hour);
        }*/

        //HÁ UMA HORA E MEIA
        else if (dim >= 90 && dim <= 119) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_unit_one_and_half_hour);
            }
        }

        /*else if (dim >= 90 && dim <= 119) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_an)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_half_hour);
        }*/


        //HÁ 2*,3*... HORAS
        else if (dim >= 120 && dim <= 1439) {
            if(ctx != null){
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                        (Math.round(dim / 60)) + ctx.getResources().getString(R.string.date_util_unit_hours);
            }
        }

        //HÁ UM DIA
        else if (dim >= 1440 && dim <= 2879) {
            if(ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                        ctx.getResources().getString(R.string.date_util_unit_day);
            }
        }
        /*else if (dim >= 1440 && dim <= 2519) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        }*/

        //HÁ *** DIAS
        else if (dim >= 2880 && dim <= 43199) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 1440)) + " "
                        + ctx.getResources().getString(R.string.date_util_unit_days);
            }
        }

        //HÁ UM MÊS
        else if (dim >= 43200 && dim <= 86399) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                        ctx.getResources().getString(R.string.date_util_unit__one_month);
            }
        }

        //HÁ *** MESES
        else if (dim >= 86400 && dim <= 525599) {
            if(ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                        (Math.round(dim / 43200)) + " " +
                        ctx.getResources().getString(R.string.date_util_unit_months);
            }
        }

        //HÁ UM ANO
        else if (dim >= 525600 && dim <= 655199) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "
                        + ctx.getResources().getString(R.string.date_util_unit_one_year);
            }
        }

        //HÁ MAIS DE UNO
        else if (dim >= 655200 && dim <= 914399) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "
                        + ctx.getResources().getString(R.string.date_util_term_a) + " " +
                        ctx.getResources().getString(R.string.date_util_unit_year);
            }
        }

        //HÁ DOIS ANOS
        else if (dim >= 914400 && dim <= 1051199) {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " +
                        ctx.getResources().getString(R.string.date_util_unit_years);
            }
        }

        //HÁ 3*, 4*... ANOS
        else {
            if (ctx != null) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "
                        + (Math.round(dim / 525600)) + " " +
                        ctx.getResources().getString(R.string.date_util_unit_years);
            }
        }


        return  timeAgo; //timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }
}
