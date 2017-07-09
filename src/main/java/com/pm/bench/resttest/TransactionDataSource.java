package com.pm.bench.resttest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public class TransactionDataSource
{
	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalDate TOMORROW = TODAY.plusDays( 1 );
	private static final LocalDate TODAY_PLUS_FIVE = TODAY.plusDays( 5 );
	private static final LocalDate TODAY_PLUS_EIGHT = TODAY.plusDays( 8 );
	
	private final List<Transaction> data =
			ImmutableList.of(
					new Transaction( TODAY, new BigDecimal( "10.10" ) ),
					new Transaction( TODAY, new BigDecimal( "10.10" ) ),
					new Transaction( TOMORROW, new BigDecimal( "10.10" ) ),
					new Transaction( TOMORROW, new BigDecimal( "10.10" ) ),
					new Transaction( TODAY, new BigDecimal( "10.10" ) ),
					new Transaction( TODAY_PLUS_FIVE, new BigDecimal( "10.100" ) ),
					new Transaction( TODAY_PLUS_FIVE, new BigDecimal( "10.10" ) ),
					new Transaction( TODAY_PLUS_FIVE, new BigDecimal( "10.10" ) ),
					new Transaction( TODAY_PLUS_EIGHT, new BigDecimal( "10.10" ) ) );
	
	public Stream<Transaction> getData()
	{
		return data.stream();
	}
}