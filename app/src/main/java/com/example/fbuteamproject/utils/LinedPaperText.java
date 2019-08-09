package com.example.fbuteamproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.fbuteamproject.R;

@SuppressLint("AppCompatCustomView")
public class LinedPaperText extends EditText {

    private static final float VERTICAL_OFFSET_SCALING_FACTOR = 0.1f;
    private static final int RED_MARGIN_OFFSET = 5;


    private Paint horizontalLinePainter;
    private Paint verticalLinePainter;

    private Rect reusableRect;


    public LinedPaperText(Context context){
        super(context);
        init();
    }
    public LinedPaperText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinedPaperText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }



    private void init(){
        reusableRect = new Rect();

        horizontalLinePainter = new Paint();
        horizontalLinePainter.setStyle(Paint.Style.FILL);
        horizontalLinePainter.setColor(getResources().getColor(R.color.linedPaperBlue) ) ;

        verticalLinePainter = new Paint();
        verticalLinePainter.setStyle(Paint.Style.FILL);
        verticalLinePainter.setColor(getResources().getColor(R.color.linedPaperRedMargin) ) ;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int viewHeight = getHeight();

        int lineHeight = getLineHeight();

        int verticalOffset = (int) (VERTICAL_OFFSET_SCALING_FACTOR * lineHeight);

        int numLines = viewHeight / lineHeight;

        if (getLineCount() > numLines){
            numLines = getLineCount();
        }

        int baseline = getLineBounds(0, reusableRect);

        //Start drawing all the lines
        for(int i = 0; i < numLines; i++){

            canvas.drawLine(
                    reusableRect.left,
                    baseline + verticalOffset,
                    reusableRect.right,
                    baseline + verticalOffset,
                    horizontalLinePainter
            );

            canvas.drawLine(
                    reusableRect.left - RED_MARGIN_OFFSET,
                    baseline + verticalOffset,
                    reusableRect.left - RED_MARGIN_OFFSET,
                    baseline - lineHeight,
                    verticalLinePainter
            );

            baseline += lineHeight;

        }

        super.onDraw(canvas);
    }
}
