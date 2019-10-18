package ic.doc.sgo;

import java.util.*;

public class Group {
    public static final int UNKNOWN_ID = -1;
    public static final int UNALLOC_ID = 0;

    private int id = UNKNOWN_ID;
    private final List<Student> studentList;

    private Group(List<Student> students) {
        studentList = students;
    }

    private Group(int id, List<Student> students) {
        this.id = id;
        studentList = students;
    }

    public static Group from(List<Student> students) {
        return new Group(students);
    }

    public static Group from(int id, List<Student> students) {
        return new Group(id, students);
    }

    public static Group of(Student... students) {
        return new Group(new ArrayList<>(Arrays.asList(students)));
    }

    public static Group of(int id, Student... students) {
        return new Group(id, new ArrayList<>(Arrays.asList(students)));
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", studentList=" + studentList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id &&
                Objects.equals(studentList, group.studentList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentList);
    }

    public boolean add(Student student) {
        if (!studentList.contains(student)) {
            Group origin = student.getGroup();
            if (origin != null) {
                origin.remove(student);
            }
            studentList.add(student);
            student.setGroup(this);
            return true;
        }
        return true;
    }

    public boolean addAll(Collection<Student> students) {
        return studentList.addAll(students);
    }

    public boolean remove(Student student) {
        if (studentList.contains(student)) {
            studentList.remove(student);
            student.setGroup(null);
            return true;
        }
        return false;
    }

    public void clear() {
        studentList.clear();
    }

    public int size() {
        return studentList.size();
    }

    public List<Student> getStudents() {
        return this.studentList;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean contains(Student s1) {
        return studentList.contains(s1);
    }
}