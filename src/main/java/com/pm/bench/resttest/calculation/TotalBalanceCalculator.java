package com.pm.bench.resttest.calculation;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.pm.bench.resttest.transaction.Transaction;

/**
 * {@link Consumer} implementation that will calculate total of all {@link Transaction}s
 * that it consumes.
 * <p>
 * <b>NOTE:</b> This class is not thread-safe.
 */
public class TotalBalanceCalculator implements Consumer<Transaction>
{
	private BigDecimal total = BigDecimal.ZERO;

	@Override
	public void accept( @Nullable final Transaction transaction )
	{
		if ( transaction != null )
		{
			total = total.add( transaction.getAmount() );
		}
	}
	
	@NonNull
	public BigDecimal getTotal()
	{
		return total;
	}
}