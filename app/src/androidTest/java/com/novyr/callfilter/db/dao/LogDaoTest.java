package com.novyr.callfilter.db.dao;

import com.novyr.callfilter.db.CallFilterDatabase;
import com.novyr.callfilter.db.LiveDataTestUtil;
import com.novyr.callfilter.db.entity.LogEntity;
import com.novyr.callfilter.db.entity.enums.Action;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;

public class LogDaoTest {

    private CallFilterDatabase mDatabase;
    private LogDao mLogDao;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CallFilterDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();

        mLogDao = mDatabase.logDao();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void getLogAfterInserted() throws InterruptedException {
        LogEntity entity = new LogEntity(new Date(), Action.BLOCKED, "5551111");

        mLogDao.insert(entity);

        List<LogEntity> entities = LiveDataTestUtil.getValue(mLogDao.findAll());

        assertEquals(1, entities.size());

        assertEquals(entity.getCreated().getTime(), entities.get(0).getCreated().getTime());
        assertEquals(entity.getAction(), entities.get(0).getAction());
        assertEquals(entity.getNumber(), entities.get(0).getNumber());
    }
}