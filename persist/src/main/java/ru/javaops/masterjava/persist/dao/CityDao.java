package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    @SqlUpdate("TRUNCATE users, cities")
    @Override
    public abstract void clean();

    @SqlUpdate("INSERT INTO cities (id, name) VALUES (:id, :name) ")
    public abstract void insert(@BindBean City city);

    @SqlBatch("INSERT INTO cities (id, name) VALUES (:id, :name) ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<City> cities, @BatchChunkSize int chunkSize);

    @SqlQuery("SELECT * FROM cities ORDER BY name LIMIT :it")
    public abstract List<City> getWithLimit(@Bind int limit);

    @SqlQuery("SELECT * FROM cities ORDER BY name")
    public abstract List<City> getCities();
}
