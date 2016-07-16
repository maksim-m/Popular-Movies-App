package me.maxdev.popularmoviesapp.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DiscoverResponseTest {

    private static final List<Integer> NUMBERS = new ArrayList<>();

    static {
        NUMBERS.add(1);
        NUMBERS.add(2);
        NUMBERS.add(3);
    }

    private static final int PAGE = 1;
    private static final int TOTAL_PAGES = 1;

    private DiscoverResponse<Integer> discoverResponse;

    @Before
    public void setUp() throws Exception {
        discoverResponse = new DiscoverResponse<>(PAGE, NUMBERS, TOTAL_PAGES, NUMBERS.size());
    }

    @After
    public void tearDown() throws Exception {
        discoverResponse = null;
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(PAGE, discoverResponse.getPage());
        assertEquals(TOTAL_PAGES, discoverResponse.getTotalPages());
        assertEquals(NUMBERS.size(), discoverResponse.getTotalResults());
        assertEquals(NUMBERS, discoverResponse.getResults());
    }
}