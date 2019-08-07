package me.mortuza.offlinetimer.roomDatabase;

import java.util.List;

import me.mortuza.offlinetimer.model.SpeedUPModel;

public class DatabaseInitializer {
    public static void insert(AppDatabase db, SpeedUPModel speedUPModel) {
        db.speedDao().insertContent(speedUPModel);
    }

    public static List<SpeedUPModel> getAllSpeed(AppDatabase db) {
        return db.speedDao().getAllSpeedList();
    }

    public static int getLastContent(AppDatabase db) {
        return db.speedDao().getLastContent();
    }
}
