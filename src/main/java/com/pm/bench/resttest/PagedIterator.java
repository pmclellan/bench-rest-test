package com.pm.bench.resttest;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
		@NonNull final List<T> initialData,
		@NonNull final PageLoader<T> pageLoader )
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

	public static interface PageLoader<T>
	{
		@Nullable
		List<T> getPage( int pageNumber );
	}
}