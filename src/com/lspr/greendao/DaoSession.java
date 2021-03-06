package com.lspr.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.lspr.greendao.record;

import com.lspr.greendao.recordDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig recordDaoConfig;

    private final recordDao recordDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        recordDaoConfig = daoConfigMap.get(recordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        recordDao = new recordDao(recordDaoConfig, this);

        registerDao(record.class, recordDao);
    }
    
    public void clear() {
        recordDaoConfig.getIdentityScope().clear();
    }

    public recordDao getRecordDao() {
        return recordDao;
    }

}
