package me.maxdev.popularmoviesapp.data;

import android.content.SharedPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SortHelperTest {

    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;

    private SortHelper mockSortHelper;

    @Mock
    SharedPreferences mockBrokenSharedPreferences;

    private Sort sort;

    @Test
    public void testGetSortByPreference() throws Exception {
        sort = Sort.MOST_POPULAR;
        mockSortHelper = createMockSharedPreference();
        assertEquals(sort, mockSortHelper.getSortByPreference());
    }

    @Test
    public void testSaveSortByPreference() throws Exception {
        sort = Sort.HIGHEST_RATED;
        mockSortHelper = createMockSharedPreference();

        mockSortHelper.saveSortByPreference(sort);
        assertEquals(sort, mockSortHelper.getSortByPreference());
    }

    private SortHelper createMockSharedPreference() {
        when(mockSharedPreferences.getString(eq("sortBy"), anyString()))
                .thenReturn(sort.toString());

        when(mockEditor.commit()).thenReturn(true);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        return new SortHelper(mockSharedPreferences);
    }
}