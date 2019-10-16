package ic.doc.sgo.studentadapters;

import com.google.gson.JsonObject;
import com.neovisionaries.i18n.CountryCode;
import ic.doc.sgo.Student;

import java.time.LocalDate;
import java.util.Optional;

public class JsonStudentAdapter implements StudentAdapter {
    private final JsonObject studentJson;

    public JsonStudentAdapter(JsonObject studentJson) {
        this.studentJson = studentJson;
    }

    @Override
    public Optional<Student> toStudent() {
        if (!studentJson.has("id")) {
            return Optional.empty();
        }
        Student.Builder studentBuilder;
        studentBuilder = new Student.Builder(studentJson.get("id").getAsString());
        LocalDate now = LocalDate.now();
        for (String key : studentJson.keySet()) {
            switch (key) {
                case "id":
                    // do nothing
                    break;
                case "country":
                    String countryName = studentJson.get(key).getAsString();
                    studentBuilder.setCountryCode(CountryCode.findByName(countryName).get(0));
                    break;
                case "currentCity":
                    String cityName = studentJson.get(key).getAsString();
                    //TODO: convert city name to zoneId
                    break;
                case "gender":
                    studentBuilder.setGender(studentJson.get(key).getAsString());
                    break;
                case "dob":
                    String dobStr = studentJson.get(key).getAsString();
                    studentBuilder.setAge(JsonFormatUtil.dobToAge(dobStr, now));
                    break;
                case "career":
                    studentBuilder.setCareer(studentJson.get(key).getAsString());
                    break;
                case "degree":
                    studentBuilder.setDegree(studentJson.get(key).getAsString());
                    break;
                case "workYearNum":
                    String yearNumStr = studentJson.get(key).getAsString();
                    double yearNum = JsonFormatUtil.yearNumStringToDouble(yearNumStr);
                    studentBuilder.setWorkYearNum(yearNum);
                    break;
                case "cohort":
                    studentBuilder.setCohort(studentJson.get(key).getAsString());
                    break;
                default:
                    studentBuilder.addAttribute(key, studentJson.get(key));
            }
        }
        return Optional.of(studentBuilder.createStudent());
    }
}
