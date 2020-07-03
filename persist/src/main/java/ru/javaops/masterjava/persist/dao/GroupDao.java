package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    @SqlUpdate("INSERT INTO groups (id, type) VALUES (:id, CAST(:type AS GROUP_TYPE)) ")
    public abstract int insert(@BindBean Group group);

    @SqlQuery("SELECT * FROM groups LIMIT :it")
    public abstract List<Group> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE groups")
    @Override
    public abstract void clean();

    //    https://habrahabr.ru/post/264281/
    @SqlBatch("INSERT INTO groups (id, type) VALUES (:id, CAST(:type AS GROUP_TYPE)) ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<Group> groups, @BatchChunkSize int chunkSize);
}
