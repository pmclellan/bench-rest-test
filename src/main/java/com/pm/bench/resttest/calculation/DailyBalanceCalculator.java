package com.pm.bench.resttest.calculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.pm.bench.resttest.transaction.Transaction;

/**
 * {@link Consumer} implementation that will calculate running daily balances from
 * all {@link Transaction}s that it consumes.
 * <p>
 * <b>NOTE:</b> This class is not thread-safe.
 */
public class DailyBalanceCalculator implements Consumer<Transaction>
{
	private static final Log LOG = LogFactory.getLog( DailyBalanceCalculator.class );
	
	private final Map<LocalDate, BigDecimal> dailyTotals = new HashMap<>();

	private LocalDate startDate;
	private LocalDate endDate;

	@Override
	public void accept( @Nullable final Transaction transaction )
	{
		if ( LOG.isDebugEnabled() )
		{
			LOG.debug( String.format( "Consuming transaction [%s]", transaction ) );
		}
		
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

	/**
	 * Returns a sorted map containing daily balances, in ascending date order, for dates between the oldest
	 * and newest transactions that have been consumed. All dates between these bounds will be included
	 * even if no transactions occurred on that date.
	 * <p>
	 * If not transactions have been consumed then an empty map will be returned.
	 * 
	 * @return Map of daily balances, or empty map if not transactions have been consumed
	 */
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