package com.example.myweatherapp.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myweatherapp.R;

import java.util.ArrayList;

// A custom view to render raindrops on the screen.
public class RainView extends View {
    private Paint paint;
    // A list to hold individual raindrop objects
    private ArrayList<Raindrop> raindrops;

    public RainView(Context context, int screenWidth, int screenHeight) {
        super(context);
        // Set the view's layout parameters to match parent's width and height

        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);
        // Initialize paint object with desired properties for raindrops
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        raindrops = new ArrayList<>();
// Populate the raindrops list with 100 raindrop instances
        for (int i = 0; i < 100; i++) {
            raindrops.add(new Raindrop(screenWidth, screenHeight));
        }

    }
    // Called when the view should render its content
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Raindrop drop : raindrops) {
            drop.update();
            canvas.drawCircle(drop.x, drop.y, 5, paint);
        }

        invalidate(); // Force redraw, animating the view
    }
}
