package org.remdev.wetweex.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import org.remdev.wetweex.ProgressView;
import org.remdev.wetweex.R;

import java.util.List;

public class TweexProgressView extends FrameLayout implements ProgressView {

    private View secondaryProgress;
    private View messageDialog;
    private TextView messageView;

    public TweexProgressView(@NonNull Context context) {
        this(context, null);
    }

    public TweexProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TweexProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TweexProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_tweex_progress, this, true);
        secondaryProgress = findViewById(R.id.secondary_progress);
        messageDialog = findViewById(R.id.message_wrapper);
        messageView = findViewById(R.id.message);
    }

    @Override
    public void show() {
        showWithOptionalMessage(null);
    }

    @Override
    public void show(@NonNull String message) {
        showWithOptionalMessage(message);
    }

    @Override
    public void show(boolean indeterminate, List<String> messages) {
        StringBuilder messageBuilder = new StringBuilder();
        for (String message : messages) {
            messageBuilder.append(message).append('\n');
        }
        if (messageBuilder.length() > 0) {
            messageBuilder.deleteCharAt(messageBuilder.length() - 1);
        }
        showWithOptionalMessage(messageBuilder.toString());
    }

    private void showWithOptionalMessage(@Nullable String message) {
        setVisibility(VISIBLE);
        messageView.setText(message);
        boolean withMessage = message != null && message.trim().isEmpty() == false;
        if (withMessage) {
            secondaryProgress.setVisibility(GONE);
            messageDialog.setVisibility(VISIBLE);
        } else {
            secondaryProgress.setVisibility(VISIBLE);
            messageDialog.setVisibility(GONE);
        }
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }
}
