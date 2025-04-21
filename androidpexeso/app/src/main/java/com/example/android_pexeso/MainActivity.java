package com.example.android_pexeso;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private GridLayout grid;
    private TextView scoreText;
    private final ArrayList<Integer> cards = new ArrayList<>();
    private final ArrayList<Button> buttons = new ArrayList<>();
    private int firstCard = -1, secondCard = -1;
    private int score = 0, pairsFound = 0;
    private boolean canClick = true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);

        scoreText = new TextView(this);
        scoreText.setTextSize(24);
        scoreText.setText("Score: 0");
        mainLayout.addView(scoreText);

        grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(4);
        mainLayout.addView(grid);

        Button newGameBtn = new Button(this);
        newGameBtn.setText("New Game");
        newGameBtn.setOnClickListener(v -> startGame());
        mainLayout.addView(newGameBtn);

        setContentView(mainLayout);
        startGame();
    }

    @SuppressLint("SetTextI18n")
    private void startGame() {
        grid.removeAllViews();
        buttons.clear();
        cards.clear();
        score = 0;
        pairsFound = 0;
        scoreText.setText("Score: 0");

        for (int i = 0; i < 8; i++) {
            cards.add(i);
            cards.add(i);
        }
        Collections.shuffle(cards);

        for (int i = 0; i < 16; i++) {
            Button btn = new Button(this);
            btn.setWidth(200);
            btn.setHeight(200);
            btn.setText("?");
            int finalI = i;
            btn.setOnClickListener(v -> cardClicked(finalI));
            grid.addView(btn);
            buttons.add(btn);
        }
    }

    private void cardClicked(int position) {
        if (!canClick || !buttons.get(position).getText().equals("?")) return;

        buttons.get(position).setText(String.valueOf(cards.get(position)));

        if (firstCard == -1) {
            firstCard = position;
        } else {
            secondCard = position;
            canClick = false;
            checkMatch();
        }
    }

    private void checkMatch() {
        if (cards.get(firstCard).equals(cards.get(secondCard))) {
            score += 10;
            pairsFound++;
            scoreText.setText("Score: " + score);

            new Handler().postDelayed(() -> {
                buttons.get(firstCard).setVisibility(View.INVISIBLE);
                buttons.get(secondCard).setVisibility(View.INVISIBLE);
                firstCard = -1;
                secondCard = -1;
                canClick = true;

                if (pairsFound == 8) {
                    scoreText.setText("You Win! Score: " + score);
                    new Handler().postDelayed(this::startGame, 2000);
                }
            }, 500);
        } else {
            score = Math.max(0, score - 1);
            scoreText.setText("Score: " + score);

            new Handler().postDelayed(() -> {
                buttons.get(firstCard).setText("?");
                buttons.get(secondCard).setText("?");
                firstCard = -1;
                secondCard = -1;
                canClick = true;
            }, 1000);
        }
    }
}