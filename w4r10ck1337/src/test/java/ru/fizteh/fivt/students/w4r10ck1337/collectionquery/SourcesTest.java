package ru.fizteh.fivt.students.w4r10ck1337.collectionquery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Sources.list;
import static ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Sources.set;

@RunWith(PowerMockRunner.class)
public class SourcesTest {
    List<Integer> resultList;
    Set<Integer> resultSet;

    @Before
    public void setUp() {
        resultList = list(0, 1, 2, 3, 4);
        resultSet = set(0, 1, 2, 3, 4);
    }

    @Test
    public void testList() {
        for (int i = 0; i < 5; i++) {
            assertTrue(resultList.contains(i));
        }
    }

    @Test
    public void testSet() {
        for (int i = 0; i < 5; i++) {
            assertTrue(resultList.contains(i));
        }
    }
}
