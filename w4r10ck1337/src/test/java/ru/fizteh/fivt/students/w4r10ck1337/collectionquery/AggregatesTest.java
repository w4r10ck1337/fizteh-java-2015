package ru.fizteh.fivt.students.w4r10ck1337.collectionquery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.List;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student;

import static org.junit.Assert.assertEquals;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Aggregates.*;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Sources.list;

@RunWith(PowerMockRunner.class)
public class AggregatesTest {
    List<Student> students;

    @Before
    public void setUp() {
        students = list(
                student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                student("smith", LocalDate.parse("1986-08-06"), "495"),
                student("petrov", LocalDate.parse("2006-08-06"), "494"),
                student("petrov", LocalDate.parse("2006-08-06"), null));
    }

    @Test
    public void testMin() {
        assertEquals(Long.valueOf(9), ((Aggregates.Aggregator) min(Student::age)).apply(students));
    }

    @Test
    public void testMax() {
        assertEquals(Long.valueOf(29), ((Aggregates.Aggregator) max(Student::age)).apply(students));
    }

    @Test
    public void testCount() {
        assertEquals(Long.valueOf(4), ((Aggregates.Aggregator) count(Student::getGroup)).apply(students));
    }

    @Test
    public void testAvg() {
        assertEquals(21, (double) ((Aggregates.Aggregator) avg(Student::age)).apply(students), 0.1);
    }
}
