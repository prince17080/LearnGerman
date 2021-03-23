package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private int current = 0, totalWords = 85, correctOption, count = 0, attempt = 0;
    Button prev_btn, next_btn, doneBtn, a1b, a2b, a3b, a4b;
    ImageButton pic_btn, audio_btn, rw_btn, pic1, pic2, pic3, pic4, audio1, audio2, audio3, audio4;
    ImageView leftArrow, rightArrow;
    TextView word_view, meaning_view, ques, visualQues, audioQues, sentence, am, vm, rm;
    EditText editText;
    MediaPlayer a1, a2, a3, a4;
    private JSONArray wordsData;
    private String currentWord;
    private JSONObject word;
    private List<Integer> indexes, option_indexes, visited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            wordsData = new JSONArray(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        indexes = new ArrayList<>();
        option_indexes = new ArrayList<>();
        visited = new ArrayList<>();
        for (int i = 0; i < totalWords; ++i) {
            indexes.add(i);
            option_indexes.add(i);
            visited.add(0);
        }
        Collections.shuffle(indexes);
        Collections.shuffle(option_indexes);

        current = 0;

        word_view = (TextView) findViewById(R.id.wordView);
        meaning_view = (TextView) findViewById(R.id.meaningView);
        vm = (TextView) findViewById(R.id.vm);
        am = (TextView) findViewById(R.id.am);
        rm = (TextView) findViewById(R.id.rm);

        leftArrow = (ImageView) findViewById(R.id.prev);
        rightArrow = (ImageView) findViewById(R.id.next);
        prev_btn = (Button) findViewById(R.id.prevBtn);
        next_btn = (Button) findViewById(R.id.nextBtn);

        setWord(current);

        // Choose Modality
        ques = (TextView) findViewById(R.id.ques);
        pic_btn = (ImageButton) findViewById(R.id.picBtn);
        audio_btn = (ImageButton) findViewById(R.id.audioBtn);
        rw_btn = (ImageButton) findViewById(R.id.rwBtn);

        // Visual Modality
        visualQues = (TextView) findViewById(R.id.visualQues);
        pic1 = (ImageButton) findViewById(R.id.picOp1);
        pic2 = (ImageButton) findViewById(R.id.picOp2);
        pic3 = (ImageButton) findViewById(R.id.picOp3);
        pic4 = (ImageButton) findViewById(R.id.picOp4);

        // Audio Modality
        audioQues = (TextView) findViewById(R.id.audioQues);
        audio1 = (ImageButton) findViewById(R.id.audioOp1);
        audio2 = (ImageButton) findViewById(R.id.audioOp2);
        audio3 = (ImageButton) findViewById(R.id.audioOp3);
        audio4 = (ImageButton) findViewById(R.id.audioOp4);
        a1b = (Button) findViewById(R.id.abutton1);
        a2b  = (Button) findViewById(R.id.abutton2);
        a3b = (Button) findViewById(R.id.abutton3);
        a4b = (Button) findViewById(R.id.abutton4);

        // rw modality
        sentence = (TextView) findViewById(R.id.sentence);
        editText = (EditText) findViewById(R.id.editText);
        doneBtn = (Button) findViewById(R.id.doneBtn);

        meaning_view.setVisibility(View.INVISIBLE);

        visualQues.setVisibility(View.INVISIBLE);
        pic1.setVisibility(View.INVISIBLE);
        pic2.setVisibility(View.INVISIBLE);
        pic3.setVisibility(View.INVISIBLE);
        pic4.setVisibility(View.INVISIBLE);

        audioQues.setVisibility(View.INVISIBLE);
        audio1.setVisibility(View.INVISIBLE);
        audio2.setVisibility(View.INVISIBLE);
        audio3.setVisibility(View.INVISIBLE);
        audio4.setVisibility(View.INVISIBLE);

        a1b.setVisibility(View.INVISIBLE);
        a2b.setVisibility(View.INVISIBLE);
        a3b.setVisibility(View.INVISIBLE);
        a4b.setVisibility(View.INVISIBLE);

        sentence.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        doneBtn.setVisibility(View.INVISIBLE);


        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = ((current - 1) % totalWords + totalWords) % totalWords;
                while (visited.get(current) != 0) {
                    current = ((current - 1) % totalWords + totalWords) % totalWords;
                }
                setWord(current);
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = (current + 1) % totalWords;
                while (visited.get(current) != 0) {
                    current = (current + 1) % totalWords;
                }
                setWord(current);
            }
        });

        pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI("pic");
            }
        });

        audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI("audio");
            }
        });
        rw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI("rw");
            }
        });

        audio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a1.isPlaying()) {
                    a1.seekTo(0);
                }
                a1.start();
            }
        });

        audio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a2.isPlaying()) {
                    a2.seekTo(0);
                }
                a2.start();
            }
        });

        audio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a3.isPlaying()) {
                    a3.seekTo(0);
                }
                a3.start();
            }
        });

        audio4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a4.isPlaying()) {
                    a4.seekTo(0);
                }
                a4.start();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString().equals(word.optString("meaning"))) {
                    Snackbar.make(v, "Yay! Correct Answer.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    if (attempt == 1) {
                        visited.set(current, 1);
                        count += 1;
                    }

                    goToNextWord();

                } else {
                    attempt += 1;
                    Snackbar.make(v, "Sorry! Wrong Answer.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void setWord(int index) {
        word = wordsData.optJSONObject(indexes.get(index));
        currentWord = word.optString("german");
        meaning_view.setText(word.optString("meaning"));
        word_view.setText(currentWord);
    }

    private void updateUI(String type) {

        if (type != "menu") {
            ques.setVisibility(View.INVISIBLE);
            pic_btn.setVisibility(View.INVISIBLE);
            audio_btn.setVisibility(View.INVISIBLE);
            rw_btn.setVisibility(View.INVISIBLE);
            vm.setVisibility(View.INVISIBLE);
            am.setVisibility(View.INVISIBLE);
            rm.setVisibility(View.INVISIBLE);
            next_btn.setVisibility(View.INVISIBLE);
            prev_btn.setVisibility(View.INVISIBLE);
            leftArrow.setVisibility(View.INVISIBLE);
            rightArrow.setVisibility(View.INVISIBLE);


            List<Integer> options = Arrays.asList(0, 1, 2, 3);
            Collections.shuffle(options);
//            correctOption = options.indexOf(0);
            correctOption = 0;
            int i = 0;
            switch (type) {
                case "pic":
                    attempt += 1;
                    visualQues.setVisibility(View.VISIBLE);
                    pic1.setVisibility(View.VISIBLE);
                    pic2.setVisibility(View.VISIBLE);
                    pic3.setVisibility(View.VISIBLE);
                    pic4.setVisibility(View.VISIBLE);
                    switch (correctOption) {
                        case 0:
                            pic1.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic2.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic3.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic4.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));

                            pic1.setOnClickListener(correctOpt);
                            pic2.setOnClickListener(wrongOpt);
                            pic3.setOnClickListener(wrongOpt);
                            pic4.setOnClickListener(wrongOpt);
                            break;
                        case 1:
                            pic2.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic1.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic3.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic4.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));

                            pic2.setOnClickListener(correctOpt);
                            pic1.setOnClickListener(wrongOpt);
                            pic3.setOnClickListener(wrongOpt);
                            pic4.setOnClickListener(wrongOpt);
                            break;
                        case 2:
                            pic3.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic2.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic1.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic4.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));

                            pic3.setOnClickListener(correctOpt);
                            pic2.setOnClickListener(wrongOpt);
                            pic1.setOnClickListener(wrongOpt);
                            pic4.setOnClickListener(wrongOpt);
                            break;

                        case 3:
                            pic4.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic2.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic3.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            pic1.setImageResource(getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "drawable", getPackageName()));

                            pic4.setOnClickListener(correctOpt);
                            pic2.setOnClickListener(wrongOpt);
                            pic3.setOnClickListener(wrongOpt);
                            pic1.setOnClickListener(wrongOpt);
                            break;
                    }

                    break;

                case "audio":
                    audioQues.setVisibility(View.VISIBLE);
                    audio1.setVisibility(View.VISIBLE);
                    audio2.setVisibility(View.VISIBLE);
                    audio3.setVisibility(View.VISIBLE);
                    audio4.setVisibility(View.VISIBLE);
                    a1b.setVisibility(View.VISIBLE);
                    a2b.setVisibility(View.VISIBLE);
                    a3b.setVisibility(View.VISIBLE);
                    a4b.setVisibility(View.VISIBLE);

                    switch (correctOption) {
                        case 0:
                            a1 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            System.out.println(wordsData.optJSONObject(option_indexes.get(i)).optString("name"));
                            a2 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            System.out.println(wordsData.optJSONObject(option_indexes.get(i)).optString("name"));
                            a3 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            System.out.println(wordsData.optJSONObject(option_indexes.get(i)).optString("name"));
                            a4 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));

                            a1b.setOnClickListener(correctOpt);
                            a2b.setOnClickListener(wrongOpt);
                            a3b.setOnClickListener(wrongOpt);
                            a4b.setOnClickListener(wrongOpt);
                            break;
                        case 1:
                            a2 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a1 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a3 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a4 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));

                            a2b.setOnClickListener(correctOpt);
                            a1b.setOnClickListener(wrongOpt);
                            a3b.setOnClickListener(wrongOpt);
                            a4b.setOnClickListener(wrongOpt);
                            break;
                        case 2:
                            a3 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a2 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a1 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a4 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));

                            a3b.setOnClickListener(correctOpt);
                            a2b.setOnClickListener(wrongOpt);
                            a1b.setOnClickListener(wrongOpt);
                            a4b.setOnClickListener(wrongOpt);
                            break;

                        case 3:
                            a4 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(indexes.get(current)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a2 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a3 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));
                            if (indexes.get(current) == option_indexes.get(i)) i++;
                            a1 = MediaPlayer.create(StartActivity.this, getResources().getIdentifier(wordsData.optJSONObject(option_indexes.get(i++)).optString("name"), "raw", getPackageName()));

                            a4b.setOnClickListener(correctOpt);
                            a2b.setOnClickListener(wrongOpt);
                            a3b.setOnClickListener(wrongOpt);
                            a1b.setOnClickListener(wrongOpt);
                            break;
                    }
                    break;

                case "rw":
                    editText.setVisibility(View.VISIBLE);
                    sentence.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);



                    break;

            }
        }
        else {

        }
    }

    private void goToNextWord() {
        meaning_view.setVisibility(View.INVISIBLE);

        visualQues.setVisibility(View.INVISIBLE);
        pic1.setVisibility(View.INVISIBLE);
        pic2.setVisibility(View.INVISIBLE);
        pic3.setVisibility(View.INVISIBLE);
        pic4.setVisibility(View.INVISIBLE);

        audioQues.setVisibility(View.INVISIBLE);
        audio1.setVisibility(View.INVISIBLE);
        audio2.setVisibility(View.INVISIBLE);
        audio3.setVisibility(View.INVISIBLE);
        audio4.setVisibility(View.INVISIBLE);
        a1b.setVisibility(View.INVISIBLE);
        a2b.setVisibility(View.INVISIBLE);
        a3b.setVisibility(View.INVISIBLE);
        a4b.setVisibility(View.INVISIBLE);

        editText.setVisibility(View.INVISIBLE);
        sentence.setVisibility(View.INVISIBLE);
        doneBtn.setVisibility(View.INVISIBLE);

        pic_btn.setVisibility(View.VISIBLE);
        audio_btn.setVisibility(View.VISIBLE);
        rw_btn.setVisibility(View.VISIBLE);
        prev_btn.setVisibility(View.VISIBLE);
        next_btn.setVisibility(View.VISIBLE);
        leftArrow.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.VISIBLE);
    }

    View.OnClickListener correctOpt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Yay! Correct Option.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            goToNextWord();

            if (attempt == 1) {
                visited.set(current, 1);
                count += 1;
            }

            if (count < totalWords) {
                current ++;
                Collections.shuffle(option_indexes);
                setWord(current);
            }
            else {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
            attempt = 0;

        }
    };

    View.OnClickListener wrongOpt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Wrong Option! Try again.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            attempt = 0;
        }
    };
}
