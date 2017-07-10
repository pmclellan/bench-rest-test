package com.pm.bench.resttest.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.pm.bench.resttest.util.PagedIterator;
import com.pm.bench.resttest.util.PagedIterator.PageLoader;

@SuppressWarnings( "null" )
@RunWith( MockitoJUnitRunner.class )
public class PagedIteratorTest
{
	@Mock
	private PageLoader<Integer> pageLoader;

	private PagedIterator<Integer> iterator;

	@Test( expected = NullPointerException.class )
	public void shouldFailInstantiationWhenInitialDataIsNull()
	{
		new PagedIterator<>( 0, null, pageLoader );
	}

	@Test( expected = NullPointerException.class )
	public void shouldFailInstantiationWhenPageLoaderIsNull()
	{
		new PagedIterator<>( 0, new ArrayList<>(), null );
	}

	@Test
	public void shouldReturnFalseForHasNextWhenIteratorIsEmpty()
	{
		iterator = new PagedIterator<>( 0, new ArrayList<>(), pageLoader );
		assertThat( iterator.hasNext(), is( false ) );
	}

	@Test
	public void shouldSupportIterationThroughSinglePageOfData()
	{
		List<Integer> data = Lists.newArrayList( 1, 2, 3 );
		iterator = new PagedIterator<>( data.size(), data, pageLoader );
		assertExpectedValues( iterator, data );
	}

	@Test
	public void shouldSupportIterationThroughMultiplePagesOfData()
	{
		List<Integer> data = Lists.newArrayList( 1, 2, 3, 4, 5, 6, 7 );
		when( pageLoader.getPage( 2 ) ).thenReturn( data.subList( 3, 6 ) );
		when( pageLoader.getPage( 3 ) ).thenReturn( data.subList( 6, 7 ) );

		iterator = new PagedIterator<>( data.size(), data.subList( 0, 3 ), pageLoader );
		assertExpectedValues( iterator, data );
	}

	@Test( expected = RuntimeException.class )
	public void shouldThrowExceptionWhenPageLoadFails()
	{
		List<Integer> data = Lists.newArrayList( 1, 2, 3 );
		when( pageLoader.getPage( 2 ) ).thenReturn( null );

		iterator = new PagedIterator<>( data.size() + 1, data, pageLoader );

		while ( iterator.hasNext() )
		{
			iterator.next();
		}
	}

	private void assertExpectedValues( final Iterator<Integer> iterator, List<Integer> expectedValues )
	{
		int index = 0;

		while ( iterator.hasNext() )
		{
			assertThat( iterator.next(), is( expectedValues.get( index++ ) ) );
		}

		assertThat( index, is( expectedValues.size() ) );
	}
}