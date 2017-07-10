package com.pm.bench.resttest;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionDataSource
{
	private final RestTemplate restTemplate;
	private final Function<Integer, String> urlGenerator;
	
	@Autowired
	public TransactionDataSource(
		@NonNull final RestTemplate restTemplate,
		@Qualifier( "urlGenerator" ) @NonNull final Function<Integer, String> urlGenerator )
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
		return restTemplate.getForObject( urlGenerator.apply( pageNumber ), TransactionPage.class );
	}
}