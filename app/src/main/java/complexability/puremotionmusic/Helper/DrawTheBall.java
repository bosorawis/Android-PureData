package complexability.puremotionmusic.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import complexability.puremotionmusic.R;

/**
 * Created by turbo on 4/28/2016.
 */
public class DrawTheBall extends View {
    private static final String TAG = "DrawTheBall";
    Bitmap l_ball;
    int bmpWidth;
    int bmpHeight;
    int x = 0;
    int y = 0;
    int height = 1;
    int width = 1;
    public DrawTheBall(Context context) {
        super(context);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);
        bmpHeight = l_ball.getHeight();
        bmpWidth = l_ball.getWidth();
    }

    public DrawTheBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);
        bmpHeight = l_ball.getHeight();
        bmpWidth = l_ball.getWidth();
    }

    public DrawTheBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);
        bmpHeight = l_ball.getHeight();
        bmpWidth = l_ball.getWidth();

    }

    public DrawTheBall(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);
        bmpHeight = l_ball.getHeight();
        bmpWidth = l_ball.getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rectangle = new Rect();
        rectangle.set(0,0,canvas.getWidth(),canvas.getHeight());
        height = canvas.getHeight();
        width = canvas.getWidth();
        //x+=2;
        //y+=2;


        Paint white = new Paint();
        Paint black = new Paint();
        white.setColor(Color.TRANSPARENT);
        white.setStyle(Paint.Style.FILL);

        black.setStyle(Paint.Style.STROKE);
        black.setColor(Color.BLACK);
        canvas.drawRect(rectangle, black);
        canvas.drawRect(rectangle,white);
        //Log.d(TAG, "Draw x: " + Integer.toString(x)+ "\t\ty: " + Integer.toString(y));

        canvas.drawBitmap(l_ball, x -bmpWidth/2, y-bmpHeight/2, new Paint());
        invalidate();


    }
    public void updateValue(float rec_x, float rec_y){
        x = (int) (((rec_x+180.0)/360.0)*width);
        y = (int) (((rec_y+180.0)/360.0)*height);
        //Log.d(TAG, "x: " + Integer.toString((int) x)+ "\t\ty: " + Integer.toString((int) y));
    }
}
