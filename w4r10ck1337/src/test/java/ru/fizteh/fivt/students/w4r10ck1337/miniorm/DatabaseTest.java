package ru.fizteh.fivt.students.w4r10ck1337.miniorm;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class DatabaseTest {
    @Ignore
    @Test
    public void testQuery () throws DatabaseException, ClassNotFoundException {
        DatabaseService<Integer, TestClass> db = new DatabaseService<>(TestClass.class);
        ArrayList<TestClass> testArray = new ArrayList<>();
        testArray.add(new TestClass(1, "asd1", 1.1));
        testArray.add(new TestClass(2, "asd2", 1.2));
        testArray.add(new TestClass(3, "asd3", 1.3));
        testArray.add(new TestClass(4, "asd4", 1.4));

        List<TestClass> response;

        db.dropTable();
        db.createTable();
        for (TestClass t : testArray) {
            db.insert(t);
        }
        response = db.queryForAll();

        for (int i = 0; i < 4; i++) {
            assertTrue(testArray.get(i).equals(response.get(i)));
        }

        testArray.get(1).value1 = "bcd";
        db.update(testArray.get(1));
        response = db.queryForAll();

        for (int i = 0; i < 4; i++) {
            assertTrue(testArray.get(i).equals(response.get(i)));
        }

        assertTrue(testArray.get(1).equals(db.queryById(testArray.get(1).key)));
        db.delete(testArray.get(2));
        testArray.remove(2);

        response = db.queryForAll();
        for (int i = 0; i < 3; i++) {
            assertTrue(testArray.get(i).equals(response.get(i)));
        }

        db.dropTable();
        db.createTable();
        assertTrue(db.queryForAll().size() == 0);
    }
}
