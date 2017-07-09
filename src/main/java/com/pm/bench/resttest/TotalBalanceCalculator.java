package com.pm.bench.resttest;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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