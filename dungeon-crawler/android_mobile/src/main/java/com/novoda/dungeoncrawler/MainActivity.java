package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements GamePauseObservable.OnToggleListener {

    private static final int NUM_OF_SQUARES = 25;

    private DungeonCrawlerGame game;
    private GamePauseObservable gamePauseObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = new AndroidDeviceDisplay(this, findViewById(R.id.scrollView), NUM_OF_SQUARES);

        Screensaver screensaver = new Screensaver(display, NUM_OF_SQUARES);
        JoystickActuator joystickActuator = new AndroidViewJoystickActuator(findViewById(R.id.joystick));
        LogcatSoundEffectsPlayer soundEffectsPlayer = new LogcatSoundEffectsPlayer();
        ArduinoLoop looper = new ArduinoLoop();
        game = InitHack.newInstance(NUM_OF_SQUARES, display, this::updateLives, joystickActuator, soundEffectsPlayer, screensaver, looper);

        findViewById(R.id.button2).setOnClickListener(v -> {
            game.start();
            Log.d("TUT", "Game restarting");
        });

        game.start();
        Log.d("TUT", "Game starting");

        gamePauseObservable = new GamePauseObservable(this);
        gamePauseObservable.startObserving();
    }

    private void updateLives(int lives) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.lives_text_view)).setText("Lives " + lives));
    }

    @Override
    public void onPauseGame() {
        Toast.makeText(this, "Game Paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResumeGame() {
        Toast.makeText(this, "Game Resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        game.stop();
        gamePauseObservable.stopObserving();
        super.onDestroy();
    }
}
