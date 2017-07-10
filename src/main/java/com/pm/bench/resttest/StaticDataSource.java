package com.pm.bench.resttest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.pm.bench.resttest.PagedIterator.PageLoader;
import com.pm.bench.resttest.transaction.Transaction;

public class StaticDataSource
{
	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalDate TOMORROW = TODAY.plusDays( 1 );
	private static final LocalDate TODAY_PLUS_FIVE = TODAY.plusDays( 5 );
	private static final LocalDate TODAY_PLUS_EIGHT = TODAY.plusDays( 8 );

	private final List<Transaction> data = ImmutableList.of(
		new Transaction( TODAY, new BigDecimal( "10.10" ) ),
		new Transaction( TODAY, new BigDecimal( "10.10" ) ),
		new Transaction( TOMORROW, new BigDecimal( "10.10" ) ),
		new Transaction( TOMORROW, new BigDecimal( "10.10" ) ),
		new Transaction( TODAY, new BigDecimal( "10.10" ) ),
		new Transaction( TODAY_PLUS_FIVE, new BigDecimal( "10.100" ) ),
		new Transaction( TODAY_PLUS_FIVE, new BigDecimal( "10.10" ) ),
		new Transaction( TODAY_PLUS_FIVE, new BigDecimal( "10.10" ) ),
		new Transaction( TODAY_PLUS_EIGHT, new BigDecimal( "10.10" ) ) );

	private final PageLoader<Transaction> loader = new PageLoader<Transaction>()
		{
			@Override
			public @Nullable List<Transaction> getPage( int pageNumber )
			{
				if ( pageNumber == 2 )
				{
					return data.subList( 4, 8 );
				}
				else if ( pageNumber == 3 )
				{
					return data.subList( 8, 9 );
				}
				else
				{
					return null;
				}
			}
		};

	public Iterator<Transaction> getData()
	{
		return new PagedIterator<Transaction>( data.size(), data.subList( 0, 4 ), loader );
	}
}