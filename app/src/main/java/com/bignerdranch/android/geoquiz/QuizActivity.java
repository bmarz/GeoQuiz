package com.bignerdranch.android.geoquiz;

//import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mCheatsRemainingTV;
    private int mCheatsRemaining = 3;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEATS = "cheats";
    private static final int CHILD_ID = 0;

    private boolean mIsCheater;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(CHEATS, mCheatsRemaining);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatsRemaining = savedInstanceState.getInt(CHEATS, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mCheatsRemainingTV = (TextView) findViewById(R.id.cheats_remaining_textview);
        mCheatsRemainingTV.setText("Cheats: " + String.valueOf(mCheatsRemaining));

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCheatsRemaining > 0) {
                    cheatCounting();
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, CHILD_ID);
                }
                else {
                    Toast.makeText(QuizActivity.this, R.string.no_cheats, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void cheatCounting() {
        mCheatsRemaining -= 1;
        mCheatsRemainingTV.setText("Cheats: " + String.valueOf(mCheatsRemaining));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CHILD_ID) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    private int mCurrentIndex = 0;
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageReasId = 0;

        if (mIsCheater) {
            messageReasId = R.string.judgment_toast;
        }
        else {
            if ( userPressedTrue == answerIsTrue) {
                messageReasId = R.string.correct_toast;
            }
            else {
                messageReasId = R.string.incorrect_toast;
            }
        }

        Toast toast = Toast.makeText(QuizActivity.this, messageReasId,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,250);
        toast.show();
    }
}
