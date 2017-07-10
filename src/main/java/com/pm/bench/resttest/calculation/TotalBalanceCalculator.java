package com.pm.bench.resttest.calculation;

import java.math.BigDecimal;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	
	@Nonnull
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
	
	@Nonnull
	public BigDecimal getTotal()
	{
		return total;
	}
}