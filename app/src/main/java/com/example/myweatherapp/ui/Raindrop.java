package com.example.myweatherapp.ui;

import android.view.ViewGroup;


// This class represents a single raindrop and handles its position and movement.
public class Raindrop {
    // Position coordinates of the raindrop and speed
    float x, y, speed;

    int screenWidth, screenHeight;

    // Constructor takes screen dimensions as parameters
    public Raindrop(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
// Randomly position the raindrop's y and x-coordinate anywhere within the screen width and height
        x = (float) (Math.random() * this.screenWidth);
        y = 0 - (float) (Math.random() * this.screenHeight);
        // Assign a random falling speed between 5 and 20 for the raindrop
        speed = (float) (Math.random() * 15 + 5);
    }
    // Updates the position of the raindrop, simulating it falling down
    public void update() {
        y += speed;
        // If the raindrop has fallen off the bottom of the screen:  Reset its x and y-coordinate to outside the screen with a random offset
        if (y > screenHeight) {
            y = 0 - (float) (Math.random() * screenHeight);
            x = (float) (Math.random() * screenWidth);
        }
    }
}
