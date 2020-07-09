package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;

@Slf4j
public class ProjectProcessor {
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public void process(StaxStreamProcessor processor) throws XMLStreamException {
        val existingProjects = projectDao.getAsMap();
        val existingGroups = groupDao.getAsMap();
//        val newProjects = new ArrayList<Project>();
//        val newGroups = new ArrayList<Group>();


        while (processor.startElement("Project", "Projects")) {
            String projectName = processor.getAttribute("name");
            Project project;
            if ((project = existingProjects.get(projectName)) == null) {
                project = new Project(projectName, processor.getElementValue("description"));
//                newProjects.add(project);
                projectDao.insert(project);
            }
            while (processor.startElement("Group", "Project")) {
                String groupName = processor.getAttribute("name");
                if (!existingGroups.containsKey(groupName)) {
                    Group group = new Group(groupName, GroupType.valueOf(processor.getAttribute("type")), project.getId());
//                    newGroups.add(group);
                    groupDao.insert(group);
                }
            }
        }
//        if (groupNames.isEmpty()) {
//            throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
//        }
//
//        while (processor.startElement("City", "Cities")) {
//            val ref = processor.getAttribute("id");
//            if (!map.containsKey(ref)) {
//                newCities.add(new City(ref, processor.getText()));
//            }
//        }
//        log.info("Insert batch " + newCities);
//        cityDao.insertBatch(newCities);
//        return cityDao.getAsMap();
    }
}
