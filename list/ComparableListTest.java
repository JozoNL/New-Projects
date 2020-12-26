package list;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComparableListTest {
    private final ComparableList<Integer> list = new ComparableList<>();
    private final ComparableList<Integer> test = ComparableList.of(1, 1, 1, 1, 1, 2, 2, 2, 2, 2);

    void initRandomly() {
        for (int i = 0; i < 1000; i++) {
            list.add((int) (Math.random() * Integer.MAX_VALUE));
        }
    }

    @Test
    void empty() {
        assertTrue(list.isEmpty());
        initRandomly();
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void growing() {
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        assertEquals(1000, list.size());
    }

    @Test
    void shrinking() {
        growing();
        for (int i = 999; i >= 100; i--) {
            list.remove(i);
        }
        assertEquals(100, list.size());
    }

    @Test
    void sortingAscending() {
        initRandomly();
        assertFalse(list.isSortedAscending());
        list.sortAscending();
        assertTrue(list.isSortedAscending());
    }

    @Test
    void sortingDescending() {
        initRandomly();
        assertFalse(list.isSortedDescending());
        list.sortDescending();
        assertTrue(list.isSortedDescending());
    }

    @Test
    void max() {
        assertNull(list.max());
        initRandomly();
        list.sortDescending();
        int max = list.max();
        assertEquals(max, list.get(0));
    }

    @Test
    void min() {
        assertNull(list.min());
        initRandomly();
        list.sortAscending();
        int min = list.min();
        assertEquals(min, list.get(0));
    }

    @Test
    void exception() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Disabled
    @Test
    void disabled() {
        assertEquals(0, list.size());
    }

    @Test
    void random() {
        initRandomly();
        int random = list.random();
        assertTrue(list.contains(random));
    }

    @Test
    void printing() {
        assertEquals("[]", list.toString());
        list.add(5);
        assertEquals("[5]", list.toString());
        list.add(10);
        assertEquals("[5, 10]", list.toString());
    }

    @Test
    void reverse() {
        initRandomly();
        list.sortAscending();
        assertTrue(list.isSortedAscending());
        list.reverse();
        assertTrue(list.isSortedDescending());
        list.reverse();
        assertTrue(list.isSortedAscending());
    }

    @Test
    void firstIndex() {
        assertNotNull(test);
        assertEquals(0, test.indexOf(1));
        assertEquals(5, test.indexOf(2));
        assertEquals(-1, test.indexOf(50));
    }

    @Test
    void lastIndex() {
        assertNotNull(test);
        assertEquals(4, test.lastIndexOf(1));
        assertEquals(9, test.lastIndexOf(2));
        assertEquals(-1, test.lastIndexOf(50));
    }

    @Test
    void allOccurrences() {
        ComparableList<Integer> test = new ComparableList<>();
        int ten = test.addAll(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        assertEquals(10, ten);
        ten = test.removeAll(1);
        assertEquals(10, ten);
    }

    @Test
    void set() {
        initRandomly();
        Integer old = list.set(500, 999_999);
        assertFalse(list.contains(old));
        assertEquals(999_999, list.get(500));
    }

    @Test
    void toArray() {
        initRandomly();
        Object[] test = list.toArray();
        assertEquals(list.toString(), java.util.Arrays.toString(test));
    }

    @Test
    void swap() {
        initRandomly();
        int firstOld = list.get(300);
        int secondOld = list.get(500);
        list.swap(300, 500);
        assertEquals(firstOld, list.get(500));
        assertEquals(secondOld, list.get(300));
    }

    @Test
    void remove() {
        initRandomly();
        int middle = list.get(500);
        int removed = list.remove(500);
        assertEquals(middle, removed);
        Integer element = list.get(600);
        boolean test = list.remove(element);
        assertTrue(test);
    }
}
