package me.mortuza.offlinetimer.roomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import me.mortuza.offlinetimer.model.SpeedUPModel;

@Dao
interface SpeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContent(SpeedUPModel speedUPModel);

    @Query("SELECT * FROM speed")
    List<SpeedUPModel> getAllSpeedList();

    @Query("SELECT max(TopSpeed) FROM speed")
    int getLastContent();
}
