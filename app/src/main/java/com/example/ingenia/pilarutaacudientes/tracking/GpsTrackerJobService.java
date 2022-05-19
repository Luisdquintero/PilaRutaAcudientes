package com.example.ingenia.pilarutaacudientes.tracking;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
 ;


/**
 * Created by jmarkstar on 24/05/2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GpsTrackerJobService extends JobService {

    private static final String TAG = "GpsTrackerJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.v(TAG, "Job have started!");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {


            Intent intent = new Intent(this,Tracking.class);
            startService(intent);



        }
        else
        {
            Intent intent = new Intent(this,Tracking.class);
            startService(intent);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.v(TAG, "Job have finished!");
        return false;
    }
}
