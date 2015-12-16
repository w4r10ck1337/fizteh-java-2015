package ru.fizteh.fivt.students.w4r10ck1337.collectionquery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Conditions.like;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Conditions.rlike;

@RunWith(PowerMockRunner.class)
public class ConditionsTest {
    @Test
    public void testLike() {
        assertTrue(like(Student::getName, "%_ov").test(student("ivanov", LocalDate.parse("1986-08-06"), "494")));
        assertFalse(like(Student::getName, "%_ov").test(student("smith", LocalDate.parse("1986-08-06"), "495")));
    }

    @Test
    public void testRlike() {
        assertTrue(rlike(Student::getName, ".*ov").test(student("ivanov", LocalDate.parse("1986-08-06"), "494")));
        assertFalse(rlike(Student::getName, ".*ov").test(student("smith", LocalDate.parse("1986-08-06"), "495")));
    }
}
