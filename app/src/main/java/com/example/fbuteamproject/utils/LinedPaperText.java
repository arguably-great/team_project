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


    private Paint linePainter;

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

        linePainter = new Paint();

//        linePainter.setARGB(200, 0, 0, 0);
        linePainter.setStyle(Paint.Style.FILL);
        linePainter.setColor(getResources().getColor(R.color.linedPaperBlue) ) ;

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
                    linePainter
            );

            baseline += lineHeight;

        }

        super.onDraw(canvas);
    }
}
