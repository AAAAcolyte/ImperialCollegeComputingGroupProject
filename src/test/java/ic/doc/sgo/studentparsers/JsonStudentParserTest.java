package ic.doc.sgo.studentparsers;

import com.google.gson.JsonObject;
import ic.doc.sgo.Student;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonStudentParserTest {

    private final LocalDate localDate = LocalDate.of(2019, 10, 1);

    @Test
    public void returnEmptyIfJsonNoIdMember() {
        JsonObject json = new JsonObject();
        json.addProperty("gender", "Male");
        assertThat(new JsonStudentParser(json, localDate).toStudent(), is(Optional.empty()));
    }

    @Test
    public void canReturnStudentIfJsonHasIdMember() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "123");
        Student student = new Student.Builder("123").createStudent();
        assertThat(new JsonStudentParser(json, localDate).toStudent(), is(Optional.of(student)));
    }

    @Test
    public void canReturnStudentIfJsonHasAllMembers() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "123");
        json.addProperty("gender", "Male");
        json.addProperty("dob", "1980/12/1");
        json.addProperty("country", "United Kingdom");
        json.addProperty("currentCity", "London");
        json.addProperty("career", "Biology");
        json.addProperty("degree", "PhD");
        json.addProperty("workYearNum", "15y");
        json.addProperty("cohort", "18J");
        Student student = new Student.Builder("123")
                .setGender("male")
                .setAge(38)
                .setTimeZone(ZoneId.of("Europe/London"))
                .setCareer("Biology")
                .setDegree("PhD")
                .setWorkYearNum(15)
                .setCohort("18J")
                .setAdditionalDiscreteAttributeWithType("country", "United Kingdom")
                .setAdditionalDiscreteAttributeWithType("currentCity", "London")
                .createStudent();
        assertThat(new JsonStudentParser(json, localDate).toStudent(), is(Optional.of(student)));
    }

    @Test
    public void canReturnStudentIfJsonHasIdAndCountryWithoutCity() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "123");
        json.addProperty("country", "United Kingdom");
        Student student = new Student.Builder("123")
                .setTimeZone(ZoneId.of("Europe/London"))
                .setAdditionalDiscreteAttributeWithType("country", "United Kingdom")
                .createStudent();
        assertThat(new JsonStudentParser(json, localDate).toStudent(), is(Optional.of(student)));
    }

    @Test
    public void canReturnStudentIfJsonHasMembersWithAdditionalAttributes() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "123");
        json.addProperty("str", "Str");
        json.addProperty("int", 1);
        json.addProperty("float", 1.1);
        json.addProperty("bool", true);
        Student student = new Student.Builder("123")
                .addAttribute("str", "Str")
                .addAttribute("int", "1")
                .addAttribute("float", "1.1")
                .addAttribute("bool", "true")
                .createStudent();
        assertThat(new JsonStudentParser(json, localDate).toStudent(), is(Optional.of(student)));
    }
}