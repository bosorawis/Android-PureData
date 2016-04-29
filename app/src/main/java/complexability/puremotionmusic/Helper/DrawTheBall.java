package complexability.puremotionmusic.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import complexability.puremotionmusic.R;

/**
 * Created by turbo on 4/28/2016.
 */
public class DrawTheBall extends View {
    Bitmap l_ball;
    int x = 0;
    int y = 0;

    public DrawTheBall(Context context) {
        super(context);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);

    }

    public DrawTheBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);

    }

    public DrawTheBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);

    }

    public DrawTheBall(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        l_ball = BitmapFactory.decodeResource(getResources(), R.drawable.left_or_ball);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rectangle = new Rect();
        rectangle.set(0,0,canvas.getWidth(),canvas.getHeight());
        //x+=2;
        //y+=2;


        Paint white = new Paint();
        Paint black = new Paint();
        white.setColor(Color.WHITE);
        white.setStyle(Paint.Style.FILL);

        black.setStyle(Paint.Style.STROKE);
        black.setColor(Color.BLACK);
        canvas.drawRect(rectangle, black);
        canvas.drawRect(rectangle,white);
        if(x < canvas.getWidth()){
            x += 2;
        }
        else{
            x=0;
        }
        if (y<canvas.getHeight()){
            y+=2;
        }
        else{
            y=0;
        }
        canvas.drawBitmap(l_ball,x,y,new Paint());
        invalidate();


    }

}
