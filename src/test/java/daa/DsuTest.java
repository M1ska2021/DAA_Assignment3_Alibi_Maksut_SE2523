package daa;

import daa.ds.Dsu;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DsuTest {

    static class NaiveDisjointSet {
        private final int[] id;
        private int count;

        NaiveDisjointSet(int n) {
            this.id = new int[n];
            this.count = n;
            for (int i = 0; i < n; i++) {
                id[i] = i;
            }
        }

        void union(int x, int y) {
            int idX = id[x];
            int idY = id[y];
            if (idX == idY) return;
            for (int i = 0; i < id.length; i++) {
                if (id[i] == idY) {
                    id[i] = idX;
                }
            }
            count--;
        }

        boolean same(int x, int y) {
            return id[x] == id[y];
        }

        int components() {
            return count;
        }
    }

    @Test
    public void testRandomUnionsAgreeWithReference() {
        int n = 500;
        int operations = 2000;
        Random rnd = new Random(12345);

        Dsu dsu = new Dsu(n);
        NaiveDisjointSet ref = new NaiveDisjointSet(n);

        assertEquals(n, dsu.components());
        assertEquals(n, ref.components());

        for (int op = 0; op < operations; op++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            dsu.union(u, v);
            ref.union(u, v);
            assertEquals(ref.components(), dsu.components());
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boolean fastSame = dsu.find(i) == dsu.find(j);
                boolean refSame = ref.same(i, j);
                assertEquals(refSame, fastSame);
            }
        }

        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < n; i++) {
            roots.add(dsu.find(i));
        }
        assertEquals(ref.components(), roots.size());
        assertEquals(dsu.components(), roots.size());
    }

    @Test
    public void testCountersTrackOperations() {
        Dsu dsu = new Dsu(5);
        assertEquals(0, dsu.getOps());

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(2, 3));
        assertEquals(3, dsu.components());
        assertEquals(2, dsu.getUnionCount());
        assertTrue(dsu.getFindCount() >= 4);

        boolean duplicate = dsu.union(0, 1);
        org.junit.jupiter.api.Assertions.assertFalse(duplicate);
        assertEquals(2, dsu.getUnionCount());
    }
}
