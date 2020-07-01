package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public class CityTestData {
    public static City KYIV;
    public static City MOSCOW;
    public static City ST_PETERSBURG;
    public static City MINSK;
    public static List<City> FIRST3_CITIES;

    public static void init() {
        KYIV = new City("kiv", "Киев");
        MOSCOW = new City("mow", "Москва");
        ST_PETERSBURG = new City("spb", "Санкт-Петербург");
        MINSK = new City("mnsk", "Минск");
        FIRST3_CITIES = ImmutableList.of(KYIV, MINSK, MOSCOW);
    }

    public static void setUp() {
        CityDao dao = DBIProvider.getDao(CityDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST3_CITIES.forEach(dao::insert);
            dao.insert(ST_PETERSBURG);
        });
    }
}
