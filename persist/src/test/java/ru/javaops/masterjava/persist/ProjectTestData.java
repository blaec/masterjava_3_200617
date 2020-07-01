package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

public class ProjectTestData {
    public static Project BASEJAVA;
    public static Project TOPJAVA;
    public static Project MASTERJAVA;
    public static List<Project> FIRST2_PROJECTS;

    public static void init() {
        BASEJAVA = new Project("basejava", "Basejava");
        TOPJAVA = new Project("topjava", "Topjava");
        MASTERJAVA = new Project("masterjava", "Masterjava");
        FIRST2_PROJECTS = ImmutableList.of(TOPJAVA, MASTERJAVA);
    }

    public static void setUp() {
        ProjectDao dao = DBIProvider.getDao(ProjectDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST2_PROJECTS.forEach(dao::insert);
            dao.insert(BASEJAVA);
        });
    }
}
