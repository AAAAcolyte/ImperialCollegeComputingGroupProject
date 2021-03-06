package ic.doc.sgo.groupingstrategies.vectorspacestrategy;

import ic.doc.sgo.Attribute;
import ic.doc.sgo.Constraint;
import ic.doc.sgo.Student;
import ic.doc.sgo.groupingstrategies.FixedPointStrategy;
import org.junit.Test;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class VectorSpaceTest {
    private Double eps = 1e-6;

    private Constraint constraint = new Constraint.Builder(3, 4)
            .setMinFemale(1)
            .setMinMale(2)
            .setTimezoneDiff(2)
            .setAgeDiff(3)
            .createConstrain();

    private VectorSpace vectorSpace = FixedPointStrategy.Converters.VectorSpaceFromConstraint(constraint, new ArrayList<>());

    private Student s1 = new Student.Builder("1")
            .setTimeZone(ZoneId.of("UTC+1"))
            .setAge(1)
            .setGender("male").createStudent();

    private Student s2 = new Student.Builder("2")
            .setTimeZone(ZoneId.of("UTC+2"))
            .setAge(2)
            .setGender("male").createStudent();

    private Student s3 = new Student.Builder("3")
            .setTimeZone(ZoneId.of("UTC+1"))
            .setAge(1)
            .setGender("female")
            .createStudent();


    private Student s4 = new Student.Builder("4")
            .setTimeZone(ZoneId.of("UTC+2"))
            .setAge(2)
            .setGender("female")
            .createStudent();

    private Student s5 = new Student.Builder("5")
            .setTimeZone(ZoneId.of("UTC+2"))
            .setAge(2)
            .setGender("male")
            .createStudent();

    private Student s6 = new Student.Builder("6")
            .setAge(3)
            .setGender("female")
            .setTimeZone(ZoneId.of("UTC+7"))
            .createStudent();

    private Student s7 = new Student.Builder("7")
            .setAge(8)
            .setGender("female")
            .setTimeZone(ZoneId.of("UTC+1"))
            .createStudent();

    private Node n1 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s1, constraint);
    private Node n2 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s2, constraint);
    private Node n3 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s3, constraint);
    private Node n4 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s4, constraint);
    private Node n5 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s5, constraint);
    private Node n6 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s6, constraint);
    private Node n7 = FixedPointStrategy.Converters.NodeFromStudentAndConstraint(s7, constraint);

    private Cluster c1 = Cluster.from(new ArrayList<>());
    private Cluster c2 = Cluster.from(new ArrayList<>());

    @Test
    public void testIsValidCluster() {

        // test lower bound
        c1.add(n1);
        assertFalse(vectorSpace.isValidCluster(c1));

        // test upper bound
        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5)));
        assertFalse(vectorSpace.isValidCluster(c1));


        // test invalid timezone
        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n3, n6)));
        Double difference = vectorSpace.getBiggestDifferenceInClusterOf(Attribute.TIMEZONE.getName(), c1);
        assertTrue(6 - eps <= difference && difference <= 6 + eps);
        assertFalse(vectorSpace.isValidCluster(c1));

        // test invalid age
        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n3, n7)));
        difference = vectorSpace.getBiggestDifferenceInClusterOf(Attribute.AGE.getName(), c1);
        assertTrue(7 - eps <= difference && difference <= 7 + eps);
        assertFalse(vectorSpace.isValidCluster(c1));

        // test invalid Gender
        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n5)));
        assertFalse(vectorSpace.isValidCluster(c1));

        //test valid group size, timezone, age, gender
        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n3)));
        assertTrue(vectorSpace.isValidCluster(c1));
    }

    @Test
    public void testGetInvalidNodesFromCluster() {
        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6, n7)));
        assertFalse(vectorSpace.isValidCluster(c1));
        vectorSpace.getInvalidNodesFromCluster(c1);
        assertTrue(vectorSpace.isValidCluster(c1));

        c1.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2)));
        assertFalse(vectorSpace.isValidCluster(c1));
        List<Node> invalidNode = vectorSpace.getInvalidNodesFromCluster(c1);
        assertFalse(vectorSpace.isValidCluster(c1));
        assertEquals(0, c1.size());
        assertEquals(2, invalidNode.size());
    }

    @Test
    public void testIsBetterFitIfSwap() {
        c1.clear();
        c2.clear();
        c1.addAll(new ArrayList<>(Arrays.asList(n1, n2, n3)));
        // TODO: need to test
    }

    @Test
    public void testPropertyCalculation() {
        VectorSpace.Property property = new VectorSpace.Property(12.0, VectorSpace.Type.CIRCLE, 0.0);
        assertTrue(Math.abs(property.getDifferenceBetween(1.0, 1.0) - 0.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(1.0, -11.0) - 12.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(1.0, 3.0) - 2.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(-1.0, -11.0) - 10.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(2.0, -4.0) - 6.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(1.0, -8.0) - 9.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(-4.0, -3.0) - 1.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(-4.0, 12.0) - 8.0) <= eps);
        assertTrue(Math.abs(property.getDifferenceBetween(-3.0, 12.0) - 9.0) <= eps);
    }

}