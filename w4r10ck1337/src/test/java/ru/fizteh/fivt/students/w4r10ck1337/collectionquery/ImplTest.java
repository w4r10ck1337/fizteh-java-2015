package ru.fizteh.fivt.students.w4r10ck1337.collectionquery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.*;
import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl.exceptions.CreateResultObjectException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Aggregates.*;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Conditions.rlike;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.OrderByConditions.asc;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.OrderByConditions.desc;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Sources.list;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl.FromStmt.from;

@RunWith(PowerMockRunner.class)
public class ImplTest {
    List<Student> students;
    List<String> answer = new ArrayList<>();

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
    public void testQuery() throws CreateResultObjectException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("2006-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                        .execute();
        statistics.forEach(t -> answer.add(t.toString()));
        assertTrue(answer.size() == 3);
        assertTrue(answer.contains(new Statistics("494", 1L, 29.0).toString()));
        assertTrue(answer.contains(new Statistics("495", 1L, 29.0).toString()));
        assertTrue(answer.contains(new Statistics("all", 1L, 30.0).toString()));
    }

    @Test(expected = CreateResultObjectException.class)
    public void testException() throws CreateResultObjectException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("2006-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, Student::getGroup, avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                        .execute();
    }
}
