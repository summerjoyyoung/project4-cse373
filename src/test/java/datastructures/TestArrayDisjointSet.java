package datastructures;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.interfaces.IDisjointSet;
import misc.BaseTest;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestArrayDisjointSet extends BaseTest {
    private <T> IDisjointSet<T> createForest(T[] items) {
        IDisjointSet<T> forest = new ArrayDisjointSet<>();
        for (T item : items) {
            forest.makeSet(item);
        }
        return forest;
    }

    private <T> void check(IDisjointSet<T> forest, T[] items, int[] expectedIds) {
        for (int i = 0; i < items.length; i++) {
            assertEquals(expectedIds[i], forest.findSet(items[i]));
        }
    }

    @Test(timeout=SECOND)
    public void testMakeSetAndFindSetSimple() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        for (int i = 0; i < 5; i++) {
            check(forest, items, new int[] {0, 1, 2, 3, 4});
        }
    }

    @Test(timeout=SECOND)
    public void testUnionSimple() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        forest.union("a", "b");
        int id1 = forest.findSet("a");
        assertTrue(id1 == 0 || id1 == 1);
        assertEquals(id1, forest.findSet("b"));

        forest.union("c", "d");
        int id2 = forest.findSet("c");
        assertTrue(id2 == 2 || id2 == 3);
        assertEquals(id2, forest.findSet("d"));

        assertEquals(4, forest.findSet("e"));
    }

    @Test(timeout=SECOND)
    public void testUnionUnequalTrees() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        forest.union("a", "b");
        int id = forest.findSet("a");

        forest.union("a", "c");

        for (int i = 0; i < 5; i++) {
            check(forest, items, new int[] {id, id, id, 3, 4});
        }
    }

    @Test(timeout=SECOND)
    public void testIllegalFindSet() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        try {
            forest.findSet("f");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
    }

    @Test(timeout=SECOND)
    public void testIllegalUnion() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        try {
            forest.union("a", "f");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }

        forest.union("a", "b");

        try {
            forest.union("a", "b");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
    }

    @Test(timeout=SECOND)
    public void testUnion1() {
        Integer[] items = new Integer[100];
        for (int i = 0; i < items.length; i++) {
            items[i] = i;
        }
        IDisjointSet<Integer> forest = this.createForest(items);

        for (int i = 0; i < items.length - 1; i++) {
            forest.union(i, i + 1);
        }
        
        int id = forest.findSet(0);
        
        int[] expected = new int[100];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = id;
        }

        for (int i = 0; i < 5; i++) {
            check(forest, items, expected);
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testUnion2() {
        int big = 1000000;
        Integer[] items = new Integer[big];
        for (int i = 0; i < items.length; i++) {
            items[i] = i;
        }
        IDisjointSet<Integer> forest = this.createForest(items);

        for (int i = 0; i < items.length - 1; i++) {
            forest.union(i, i + 1);
        }
        
        int id = forest.findSet(0);
        
        int[] expected = new int[big];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = id;
        }

        check(forest, items, expected);
        assertTrue(forest.findSet(0) == forest.findSet(big - 1));
        assertTrue(forest.findSet(10) == 0);
    }
    
    @Test(timeout=SECOND)
    public void testSameSet() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);
        

        forest.union("a", "b");
        
        forest.union("c", "d");
        
        forest.union("b", "c");
        
        try {
            forest.union("a", "d");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // nothing!
        }
    }
    
    @Test(timeout=SECOND)
    public void testSesame() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);
        
        forest.union("a",  "b");
        assertTrue(forest.findSet("c") == 2);
        assertTrue(forest.findSet("b") == forest.findSet("a"));
        
        forest.union("a", "c");

        assertTrue(forest.findSet("c") == 0);
        
        forest.union("d", "e");
        
        forest.union("c", "d");
        
        assertTrue(forest.findSet("b") == forest.findSet("d"));
        
        
    }
    
    @Test(timeout=SECOND)
    public void testSesame2() {
        String[] items = new String[] {};
        IDisjointSet<String> forest = this.createForest(items);
        
        try {
            forest.findSet("a");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // nothing!
        }                            
    }
    
    @Test(timeout=SECOND)
    public void testSesame3() {
        String[] items = new String[] {null, "a", "b"};
        IDisjointSet<String> forest = this.createForest(items);
        
        forest.union(null, "a");
        
        assertTrue(forest.findSet(null) == forest.findSet("a"));
        
        forest.union("b", null);
        
        assertTrue(forest.findSet("b") == forest.findSet("a"));
    }
    
    @Test(timeout=4 * SECOND)
    public void testLargeForest() {
        IDisjointSet<Integer> forest = new ArrayDisjointSet<>();
        forest.makeSet(0);

        int numItems = 5000;
        for (int i = 1; i < numItems; i++) {
            forest.makeSet(i);
            forest.union(0, i);
        }

        int cap = 6000;
        int id = forest.findSet(0);
        for (int i = 0; i < cap; i++) {
            for (int j = 0; j < numItems; j++) {
                assertEquals(id, forest.findSet(j));
            }
        }
    }
}
