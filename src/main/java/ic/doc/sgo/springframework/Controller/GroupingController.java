package ic.doc.sgo.springframework.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ic.doc.sgo.Constraint;
import ic.doc.sgo.Group;
import ic.doc.sgo.Student;
import ic.doc.sgo.groupingstrategies.GroupingStrategy;
import ic.doc.sgo.groupingstrategies.RandomGroupingStrategy;
import ic.doc.sgo.springframework.Service.GroupingService;
import ic.doc.sgo.studentadapters.JsonStudentAdapter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/")
public class GroupingController {
  @Resource private GroupingService groupingService;

  @PostMapping(value = "/allocateGroups")
  public @ResponseBody String groupStudents(@RequestBody String json) {
    Gson gson = new Gson();
    List<Group> groupList = getAllocatedGroupFromJson(json, groupingService);
    // return a string which is in the form of a JsonObject
    return gson.toJson(getResultJsonObj(groupList));
  }

  public static List<Group> getAllocatedGroupFromJson(String json, GroupingService groupingService) {
    Gson gson = new Gson();
    JsonObject parsedJson = gson.fromJson(json, JsonObject.class);
    JsonArray studentsJsonArray = parsedJson.getAsJsonArray("students");
    JsonObject constraintJsonObject = parsedJson.getAsJsonObject("filters");

    // Use Gson to convert json to object
    Constraint constraint = gson.fromJson(constraintJsonObject, Constraint.class);

    List<Student> studentList = new ArrayList<>();
    LocalDate now = LocalDate.now();
    for (int i = 0; i < studentsJsonArray.size(); i++) {
      Optional<Student> optionalStudent =
              new JsonStudentAdapter(studentsJsonArray.get(i).getAsJsonObject(), now).toStudent();
      optionalStudent.ifPresent(studentList::add);
    }
    return groupingService.groupStudent(studentList, constraint);
  }

  private JsonObject getResultJsonObj(List<Group> groupList) {
    JsonObject result = new JsonObject();
    JsonArray studentsJsonArray = new JsonArray();
    for (Group group : groupList) {
      for (Student student : group.getStudents()) {
        JsonObject studentJsonObject = new JsonObject();
        studentJsonObject.addProperty("id", student.getId());
        // Add group id for each student json object
        studentJsonObject.addProperty("groupId", group.getId());
        studentsJsonArray.add(studentJsonObject);
      }
    }
    long numOfGroup = groupList.stream()
            .filter(group -> group.getId() != Group.UNKNOWN_ID && group.getId() != Group.UNALLOC_ID)
            .count();
    long numOfUnallocStu = groupList.stream()
            .filter(group -> group.getId() == Group.UNALLOC_ID)
            .map(Group::size).reduce(0, Integer::sum);
    result.add("students", studentsJsonArray);
    result.addProperty("numOfGroup", numOfGroup);
    result.addProperty("numOfUnalloc", numOfUnallocStu);
    return result;
  }
}
