package com.prototest.prima;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prototest.prima.contentprovider.PrimaContentProvider;
import com.prototest.prima.datastructures.BatteryStats;
import com.prototest.prima.datastructures.MemoryStats;
import com.prototest.prima.datastructures.ProcessorStats;
import com.prototest.prima.monitor.SystemMonitor;

public class MainActivity extends Activity {

   static final String TAG = "MainActivity";
   private static SystemMonitor batteryMonitor;
   private static SystemMonitor memoryMonitor;
   private static SystemMonitor processorMonitor;
   private static boolean recording = false;
   private TextView recordingLabel;
   private static Timer timer;
   private int elapsedTime = 0;
   private TextView stopwatchText;
   private ImageButton recordingImageButton;
   private View notificationLabel;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate()");
      setContentView(R.layout.activity_main);
      stopwatchText = (TextView) findViewById(R.id.stopwatch);
      batteryMonitor = new SystemMonitor(new BatteryStats());
      memoryMonitor = new SystemMonitor(new MemoryStats());
      try {
         processorMonitor = new SystemMonitor(new ProcessorStats());
      } catch (IOException e) {
         Log.e(TAG, e.getMessage());
      }

      recordingLabel = (TextView) findViewById(R.id.recording_status);
      recordingImageButton = (ImageButton) findViewById(R.id.rec_toggle_button);
      notificationLabel = (RelativeLayout) findViewById(R.id.notification);

      if (!recording) {
         stopwatchText.setVisibility(View.INVISIBLE);
         notificationLabel.setVisibility(View.INVISIBLE);
      } else {
         stopwatchText.setVisibility(View.VISIBLE);
         notificationLabel.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      Log.d(TAG, "onCreateOptionsMenu()");
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   public void switchToGraph(View view) {
      Log.d(TAG, "switchToGraph()");
      Intent intent = new Intent(MainActivity.this, SimpleXYPlotActivity.class);
      startActivity(intent);
   }

   public void toggleRecording(View view) throws IOException {
      if (!recording) {
         PrimaContentProvider.dropAllTables();
         PrimaContentProvider.createAllTables();

         startRecording();
         recording = true;
      } else {
         stopRecording(view);
         recording = false;
      }
   }

   private void startRecording() {
      Log.d(TAG, "startRecording");
      startTimer();
      batteryMonitor.StartMonitoring();
      memoryMonitor.StartMonitoring();
      processorMonitor.StartMonitoring();
      recordingLabel.setText(R.string.stop);
      recordingImageButton.setImageResource(R.drawable.stop);
      stopwatchText.setVisibility(View.VISIBLE);
      notificationLabel.setVisibility(View.VISIBLE);

   }

   private void stopRecording(View view) {
      Log.d(TAG, "stopRecording");
      stopTimer();
      batteryMonitor.StopMonitoring();
      memoryMonitor.StopMonitoring();
      processorMonitor.StopMonitoring();
      recordingLabel.setText(R.string.start);
      recordingImageButton.setImageResource(R.drawable.start);
      stopwatchText.setVisibility(View.INVISIBLE);
      notificationLabel.setVisibility(View.INVISIBLE);
      switchToGraph(view);
   }

   private void stopTimer() {
      timer.cancel();
      timer = null;
      elapsedTime = 0;
      stopwatchText.setText(formatStopWatchTime(elapsedTime));
   }

   private void startTimer() {
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {

         public void run() {
            mHandler.obtainMessage(1).sendToTarget();
            elapsedTime += 1; // increase every sec;

         }
      }, 0, 1000);
   }

   private Handler mHandler = new Handler() {
      public void handleMessage(Message msg) {
         stopwatchText.setText(formatStopWatchTime(elapsedTime)); // this is the textview
      }
   };

   private String formatStopWatchTime(int elapsedSeconds) {
      String timeText;
      int hours = elapsedSeconds / 3600;
      int minutes = elapsedSeconds / 60;
      int seconds = elapsedSeconds / 1;
      int displayMinutes = minutes % 60;
      int displaySeconds = seconds % 60;
      return String.format("%02d:%02d:%02d", hours, displayMinutes, displaySeconds);
   }
}
