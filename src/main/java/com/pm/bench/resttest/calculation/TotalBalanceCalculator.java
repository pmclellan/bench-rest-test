package com.pm.bench.resttest.calculation;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static final Log LOG = LogFactory.getLog( TotalBalanceCalculator.class );
	
	private BigDecimal total = BigDecimal.ZERO;

	@Override
	public void accept( @Nullable final Transaction transaction )
	{
		if ( LOG.isDebugEnabled() )
		{
			LOG.debug( String.format( "Consuming transaction [%s]", transaction ) );
		}
		
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