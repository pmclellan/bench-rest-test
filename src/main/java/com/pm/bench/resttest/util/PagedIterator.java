package com.pm.bench.resttest.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link Iterator} implementation that can be used to page through large data sets without having to
 * load the entire content into memory. Instead, the data is loaded in chunks as the iterator moves
 * through the collection.
 * <p>
 * <b>NOTE:</b> This class is not thread-safe.
 */
public class PagedIterator<T> implements Iterator<T>
{
	private final int totalElements;
	private final List<T> currentData;
	private final PageLoader<T> pageLoader;

	private int currentPage = 1;
	private int currentIndex = 0;
	private int offset = 0;

	public PagedIterator(
		int totalElements,
		@Nonnull final List<T> initialData,
		@Nonnull final PageLoader<T> pageLoader )
	{
		this.totalElements = totalElements;
		this.currentData = new ArrayList<>( requireNonNull( initialData ) );
		this.pageLoader = requireNonNull( pageLoader );
	}

	@Override
	public boolean hasNext()
	{
		return currentIndex + offset < totalElements;
	}

	@Override
	public T next()
	{
		if ( !hasNext() )
		{
			throw new NoSuchElementException();
		}

		if ( currentIndex >= currentData.size() )
		{
			loadNextPage();
		}

		return currentData.get( currentIndex++ );
	}

	private void loadNextPage()
	{
		List<T> newData = pageLoader.getPage( ++currentPage );

		if ( newData == null )
		{
			throw new RuntimeException( "Failed to load next page of data" );
		}

		offset += currentIndex;
		currentData.clear();
		currentData.addAll( newData );
		currentIndex = 0;
	}

	/**
	 * A {@code PageLoader} implementation is responsible for fetching pages of data
	 * as they are requested by a {@link PagedIterator}.
	 */
	@FunctionalInterface
	public static interface PageLoader<T>
	{
		@Nullable
		List<T> getPage( int pageNumber );
	}
}