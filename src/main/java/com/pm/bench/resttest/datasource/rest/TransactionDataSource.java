package com.pm.bench.resttest.datasource.rest;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
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

import com.pm.bench.resttest.PagedIterator;
import com.pm.bench.resttest.datasource.DataSourceException;
import com.pm.bench.resttest.datasource.MalformedDataException;
import com.pm.bench.resttest.transaction.Transaction;
import com.pm.bench.resttest.transaction.TransactionPage;

@Component
public class TransactionDataSource
{
	private static final Log LOG = LogFactory.getLog( TransactionDataSource.class );
	
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
	
	public Iterator<Transaction> getData()
	{
		TransactionPage firstPage = getDataPage( 1 );
		
		return new PagedIterator<>(
			firstPage.getTotalCount(),
			firstPage.getTransactions(),
			(i) -> getDataPage( i ).getTransactions() );
	}
	
	private TransactionPage getDataPage( int pageNumber )
	{
		String requestUrl = urlGenerator.apply( pageNumber );
		
		if ( LOG.isDebugEnabled() )
		{
			LOG.debug(
				String.format( "Requesting page [%s] of transaction data with URL [%s]", pageNumber, requestUrl ) );
		}
		
		try
		{
			return restTemplate.getForObject( requestUrl, TransactionPage.class );
		}
		catch ( HttpMessageNotReadableException nre )
		{
			LOG.error( "Failed to process contents of REST service response.", nre );
			throw new MalformedDataException( "Bad data received from REST service", nre );
		}
		catch ( RestClientResponseException rcre )
		{
			LOG.error(
				String.format(
					"Received unexpected response from REST service. Status code [%s] was returned.",
					rcre.getRawStatusCode() ),
				rcre );
			throw new DataSourceException( "Bad response received from REST service", rcre );
		}
		catch ( RestClientException rce )
		{
			LOG.error( "Error occurred while communicating with REST service.", rce );
			throw new DataSourceException( "Failed to communicate with REST service", rce );
		}
		catch ( RuntimeException re )
		{
			LOG.error( "Unexpected error occurred while retrieving transaction data.", re );
			throw new DataSourceException( "Failed to retrieve transaction data from source", re );
		}
	}
}