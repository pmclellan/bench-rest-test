package com.pm.bench.resttest.datasource.rest;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Preconditions;
import com.pm.bench.resttest.PagedIterator;
import com.pm.bench.resttest.datasource.DataSourceException;
import com.pm.bench.resttest.datasource.MalformedDataException;
import com.pm.bench.resttest.transaction.Transaction;
import com.pm.bench.resttest.transaction.TransactionPage;

@Component
public class TransactionDataSource
{
	private static final Log LOG = LogFactory.getLog( TransactionDataSource.class );
	
	private static final Consumer<TransactionPage> INITIAL_COUNT_VALIDATOR = new TotalCountValidator();
	
	private final RestTemplate restTemplate;
	private final Function<Integer, String> urlGenerator;
	
	@Autowired
	public TransactionDataSource(
		@Nonnull final RestTemplate restTemplate,
		@Qualifier( "urlGenerator" ) @Nonnull final Function<Integer, String> urlGenerator )
	{
		this.restTemplate = requireNonNull( restTemplate );
		this.urlGenerator = requireNonNull( urlGenerator );
	}
	
	/**
	 * Returns {@link Iterator} to step through all available transactions.
	 * 
	 * @return transaction iterator
	 */
	@SuppressWarnings( "unchecked" )
	public Iterator<Transaction> getData()
	{
		TransactionPage firstPage =
			getDataPage( 1, new PageNumberValidator( 1 ), INITIAL_COUNT_VALIDATOR );
		
		int totalCount = firstPage.getTotalCount();
		TotalCountValidator countValidator = new TotalCountValidator( totalCount );
		
		return new PagedIterator<>(
			totalCount,
			firstPage.getTransactions(),
			(i) -> getDataPage( i,  new PageNumberValidator( i ), countValidator ).getTransactions() );
	}
	
	@SuppressWarnings( "unchecked" )
	private TransactionPage getDataPage( int pageNumber, Consumer<TransactionPage>... validators )
	{
		TransactionPage page = null;
		String requestUrl = urlGenerator.apply( pageNumber );
		
		if ( LOG.isDebugEnabled() )
		{
			LOG.debug(
				String.format( "Requesting page [%s] of transaction data with URL [%s]", pageNumber, requestUrl ) );
		}
		
		try
		{
			page = restTemplate.getForObject( requestUrl, TransactionPage.class );
		}
		catch ( HttpMessageNotReadableException nre )
		{
			throw new MalformedDataException( "Failed to process contents of REST service response.", nre );
		}
		catch ( RestClientResponseException rcre )
		{
			throw new DataSourceException( 
				String.format(
					"Received unexpected response from REST service. Status code [%s] was returned.",
					rcre.getRawStatusCode() ),
				rcre );
		}
		catch ( RestClientException rce )
		{
			throw new DataSourceException( "Error occurred while communicating with REST service.", rce );
		}
		catch ( RuntimeException re )
		{
			throw new DataSourceException( "Unexpected error occurred while retrieving transaction data.", re );
		}
		
		if ( validators != null )
		{
			for ( Consumer<TransactionPage> validator : validators )
			{
				validator.accept( page );
			}
		}
		
		return page;
	}
	
	/**
	 * {@link Consumer} implementation used to validate that transaction count expressed within
	 * a given {@link TransactionPage} is non-negative and, optionally, matches an expected value.
	 * <p>
	 * This validator is used to ensure data remains consistent during page traversal.
	 */
	private static class TotalCountValidator implements Consumer<TransactionPage>
	{
		private final int expectedCount;
		
		/**
		 * Creates validator that will ensure transaction count is non-negative
		 */
		private TotalCountValidator()
		{
			this.expectedCount = -1;
		}
		
		/**
		 * Creates validator that will ensure transaction count is non-negative
		 * and matches the given value
		 */
		private TotalCountValidator( int expectedCount )
		{
			Preconditions.checkArgument( expectedCount >= 0, "Expected count must be non-negative" );
			this.expectedCount = expectedCount;
		}
		
		@Override
		public void accept( final TransactionPage page )
		{
			int totalCount = page.getTotalCount();
			
			if ( totalCount < 0 )
			{
				throw new MalformedDataException(
					String.format( "Bad data: total transaction count is negative [%s]", totalCount ) );
			}
			
			if ( expectedCount >= 0 && totalCount != expectedCount )
			{
				throw new MalformedDataException(
					String.format( 
						"Bad data: transactionCount [%s] does not match expected value [%s]",
						totalCount,
						expectedCount ) );
			}
		}
	}
	
	/**
	 * {@link Consumer} implementation used to validate that page number expressed within
	 * a given {@link TransactionPage} matches an expected value.
	 * <p>
	 * This validator is used to ensure the correct page is returned by the REST service.
	 */
	private static class PageNumberValidator implements Consumer<TransactionPage>
	{
		private final int expectedPageNumber;
		
		private PageNumberValidator( int expectedPageNumber )
		{
			this.expectedPageNumber = expectedPageNumber;
		}
		
		@Override
		public void accept( final TransactionPage page )
		{
			if ( page.getPageNumber() != expectedPageNumber )
			{
				throw new MalformedDataException(
					String.format( 
						"Bad data: page number [%s] does not match expected value [%s]",
						page.getPageNumber(),
						expectedPageNumber ) );
			}
		}
	}
}