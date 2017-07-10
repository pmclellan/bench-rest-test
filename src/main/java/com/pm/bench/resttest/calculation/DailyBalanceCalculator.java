package com.pm.bench.resttest.calculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.pm.bench.resttest.transaction.Transaction;

public class DailyBalanceCalculator implements Consumer<Transaction>
{
	private final Map<LocalDate, BigDecimal> dailyTotals = new HashMap<>();

	private LocalDate startDate;
	private LocalDate endDate;

	@Override
	public void accept( @Nullable final Transaction transaction )
	{
		if ( transaction == null )
		{
			return;
		}

		LocalDate transactionDate = transaction.getTransactionDate();
		BigDecimal transactionAmount = transaction.getAmount();
		BigDecimal currentTotal = dailyTotals.get( transactionDate );

		if ( currentTotal == null )
		{
			dailyTotals.put( transactionDate, transactionAmount );
		}
		else
		{
			dailyTotals.put( transactionDate, currentTotal.add( transactionAmount ) );
		}

		if ( startDate == null || transactionDate.isBefore( startDate ) )
		{
			startDate = transactionDate;
		}

		if ( endDate == null || transactionDate.isAfter( endDate ) )
		{
			endDate = transactionDate;
		}
	}

	@NonNull
	public Map<LocalDate, BigDecimal> getTotals()
	{
		Map<LocalDate, BigDecimal> totals = new LinkedHashMap<>();

		if ( dailyTotals.isEmpty() )
		{
			return totals;
		}

		LocalDate currentDate = startDate;
		BigDecimal runningTotal = BigDecimal.ZERO;
		BigDecimal dailyTotal;

		while ( !currentDate.isAfter( endDate ) )
		{
			dailyTotal = dailyTotals.get( currentDate );

			if ( dailyTotal != null )
			{
				runningTotal = runningTotal.add( dailyTotal );
			}

			totals.put( currentDate, runningTotal );
			currentDate = currentDate.plusDays( 1 );
		}

		return totals;
	}
}