package me.maxdev.popularmoviesapp.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class SortTest {

    @Test
    public void testFromString() throws Exception {
        assertEquals(Sort.MOST_POPULAR, Sort.fromString("popularity.desc"));
        assertEquals(Sort.HIGHEST_RATED, Sort.fromString("vote_average.desc"));
        assertEquals(Sort.MOST_RATED, Sort.fromString("vote_count.desc"));
    }

    @Test
    public void testFromIllegalString() throws Exception {
        try {
            Sort.fromString("Illegal string");
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("No constant with text Illegal string found.", expected.getMessage());
        }
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(Sort.MOST_POPULAR.toString(), "popularity.desc");
        assertEquals(Sort.HIGHEST_RATED.toString(), "vote_average.desc");
        assertEquals(Sort.MOST_RATED.toString(), "vote_count.desc");
    }

    @Test
    public void testValueOf() throws Exception {
        assertEquals(Sort.MOST_POPULAR, Sort.valueOf("MOST_POPULAR"));
        assertEquals(Sort.HIGHEST_RATED, Sort.valueOf("HIGHEST_RATED"));
        assertEquals(Sort.MOST_RATED, Sort.valueOf("MOST_RATED"));
    }

    @Test
    public void testIllegalValueOf() throws Exception {
        try {
            Sort.valueOf("Illegal string");
            fail();
        } catch (IllegalArgumentException expected) {

        }
    }
}