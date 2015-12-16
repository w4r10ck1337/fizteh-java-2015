package ru.fizteh.fivt.students.w4r10ck1337.collectionquery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student;

import static org.junit.Assert.assertTrue;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.OrderByConditions.asc;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.OrderByConditions.desc;

@RunWith(PowerMockRunner.class)
public class OrderByConditionsTest {
    Student student1, student2, student3;

    @Before
    public void setUp() {
        student1 = student("ivanov", LocalDate.parse("1986-08-06"), "494");
        student2 = student("sidorov", LocalDate.parse("1986-08-06"), "495");
        student3 = student("smith", LocalDate.parse("1986-08-06"), "495");
    }

    @Test
    public void testAsc() {
        assertTrue(asc(Student::getGroup).compare(student1, student2) < 0);
        assertTrue(asc(Student::getGroup).compare(student2, student1) > 0);
        assertTrue(asc(Student::getGroup).compare(student2, student3) == 0);
    }

    @Test
    public void testDesc() {
        assertTrue(desc(Student::getGroup).compare(student1, student2) > 0);
        assertTrue(desc(Student::getGroup).compare(student2, student1) < 0);
        assertTrue(desc(Student::getGroup).compare(student2, student3) == 0);
    }
}
