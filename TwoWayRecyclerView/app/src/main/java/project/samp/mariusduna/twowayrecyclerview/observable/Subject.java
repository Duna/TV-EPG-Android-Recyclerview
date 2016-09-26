package project.samp.mariusduna.twowayrecyclerview.observable;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public class Subject {
    private int dx;
    private int initialPosition;
    private Handler handler = new Handler();
    private List<ObservableRecyclerView> observers = new ArrayList<ObservableRecyclerView>();
    private int positionInList;
    private double currentTime;

    public Handler getHandler() {
        return handler;
    }

    public int getState() {
        return dx;
    }

    //this is in pixels
    public int getInitialPosition() {
        return initialPosition;
    }

    public void setState(int dx) {
        this.dx = dx;
        /*Log.d("POS", "DX position: " + dx);
        Log.d("POS", "Initial position: " + initialPosition);*/
        this.initialPosition = initialPosition - dx;
        notifyAllObservers();
    }

    public void attach(ObservableRecyclerView observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void notifyAllObservers() {
        for (ObservableRecyclerView observer : observers) {
            observer.update();
        }
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
        Log.d("POS", "Current time " + currentTime);
    }
}