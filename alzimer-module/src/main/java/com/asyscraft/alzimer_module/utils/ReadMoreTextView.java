package com.asyscraft.alzimer_module.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.asyscraft.alzimer_module.R;


public class ReadMoreTextView extends AppCompatTextView {
    private static final int MAX_WORDS = 30;
    private boolean isExpanded = false;
    private String fullText;

    public ReadMoreTextView(Context context) {
        super(context);
        init();
    }

    public ReadMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReadMoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void setTextWithReadMore(String text) {
        this.fullText = text;
        String truncatedText = getTruncatedText(text);
        String displayText = truncatedText + " See More";

        SpannableString spannable = new SpannableString(displayText);
        int readMoreStart = displayText.indexOf("See More");
        int readMoreEnd = readMoreStart + "See More".length();

        // Apply color to "Read More"
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_text)),
                readMoreStart, readMoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable text
        setText(spannable);

        // Set click listener
        setOnClickListener(v -> toggleText());
    }






    private void toggleText() {
        if (isExpanded) {
            String truncatedText = getTruncatedText(fullText);
            String displayText = truncatedText + " See More";

            SpannableString spannable = new SpannableString(displayText);
            int readMoreStart = displayText.indexOf("See More");
            int readMoreEnd = readMoreStart + "See More".length();

            // Apply color to "Read More"
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_text)),
                    readMoreStart, readMoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            setText(spannable);
        } else {
            String displayText = fullText + " See Less";

            SpannableString spannable = new SpannableString(displayText);
            int readLessStart = displayText.indexOf("See Less");
            int readLessEnd = readLessStart + "See Less".length();

            // Apply color to "Read Less"
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_text)),
                    readLessStart, readLessEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            setText(spannable);
        }
        isExpanded = !isExpanded;
    }





    private String getTruncatedText(String text) {
        String[] words = text.split(" ");
        if (words.length <= MAX_WORDS) {
            return text;
        }
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < MAX_WORDS; i++) {
            truncated.append(words[i]).append(" ");
        }
        return truncated.toString().trim() + "...";
    }
}


