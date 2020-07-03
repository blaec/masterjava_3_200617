package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;

import java.util.List;

public class GroupTestData {
    public static Group TOPJAVA06;
    public static Group TOPJAVA07;
    public static Group TOPJAVA08;
    public static Group MASTERJAVA01;
    public static Group MASTERJAVA02;
    public static Group BASEJAVA25;
    public static Group BASEJAVA26;
    public static Group BASEJAVA27;
    public static List<Group> FIRST7_GROUPS;

    public static void init() {
        TOPJAVA06 = new Group("topjava06", GroupType.FINISHED);
        TOPJAVA07 = new Group("topjava07", GroupType.FINISHED);
        TOPJAVA08 = new Group("topjava08", GroupType.CURRENT);
        MASTERJAVA01 = new Group("masterjava01", GroupType.FINISHED);
        MASTERJAVA02 = new Group("masterjava02", GroupType.CURRENT);
        BASEJAVA25 = new Group("basejava25", GroupType.FINISHED);
        BASEJAVA26 = new Group("basejava26", GroupType.CURRENT);
        BASEJAVA27 = new Group("basejava27", GroupType.REGISTERING);
        FIRST7_GROUPS = ImmutableList.of(BASEJAVA25, BASEJAVA26, MASTERJAVA01, MASTERJAVA02, TOPJAVA06, TOPJAVA07, TOPJAVA08);
    }

    public static void setUp() {
        GroupDao dao = DBIProvider.getDao(GroupDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST7_GROUPS.forEach(dao::insert);
            dao.insert(BASEJAVA27);
        });
    }
}
