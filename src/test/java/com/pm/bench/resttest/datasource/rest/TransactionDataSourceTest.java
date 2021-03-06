package com.pm.bench.resttest.datasource.rest;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Iterators;
import com.pm.bench.resttest.datasource.DataSourceException;
import com.pm.bench.resttest.datasource.MalformedDataException;
import com.pm.bench.resttest.transaction.Transaction;
import com.pm.bench.resttest.transaction.TransactionPage;

@SuppressWarnings( "null" )
public class TransactionDataSourceTest
{
	private static final Function<Integer, String> URL_GENERATOR = ( i ) -> i + ".json";
	private static final RestTemplate TEMPLATE = new RestTemplate();
	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule( new JavaTimeModule() );

	private MockRestServiceServer mockServer;
	private TransactionDataSource dataSource;

	@Before
	public void setUp()
	{
		mockServer = MockRestServiceServer.createServer( TEMPLATE );
		dataSource = new TransactionDataSource( TEMPLATE, URL_GENERATOR );
	}

	@Test( expected = NullPointerException.class )
	public void shouldFailInstantiationWhenRestTemplateIsNull()
	{
		new TransactionDataSource( null, URL_GENERATOR );
	}

	@Test( expected = NullPointerException.class )
	public void shouldFailInstantiationWhenUrlGeneratorIsNull()
	{
		new TransactionDataSource( TEMPLATE, null );
	}

	@Test
	public void shouldRequestFirstPageOfDataWhenGetDataIsInvoked() throws Exception
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( 0, 1, 0 ) );
		dataSource.getData();
		mockServer.verify();
	}

	@Test
	public void shouldRequestAllPagesWhenIteratingThroughData() throws Exception
	{
		int transactionCount = 8;
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( transactionCount, 1, 3 ) );
		mockServer.expect( requestTo( URL_GENERATOR.apply( 2 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( transactionCount, 2, 3 ) );
		mockServer.expect( requestTo( URL_GENERATOR.apply( 3 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( transactionCount, 3, 2 ) );

		Iterator<Transaction> data = dataSource.getData();
		Iterators.advance( data, transactionCount );
		mockServer.verify();
	}

	@Test
	public void shouldNotGenerateAdditionalRequestsWhenAllDataIsOnSinglePage() throws Exception
	{
		int transactionCount = 3;
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( transactionCount, 1, transactionCount ) );

		Iterator<Transaction> data = dataSource.getData();
		Iterators.advance( data, transactionCount );
		mockServer.verify();
	}

	@Test( expected = MalformedDataException.class )
	public void shouldThrowMalformedDataExceptionWhenJsonStringIsInvalid()
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withSuccess( "{ invalid-json }", MediaType.APPLICATION_JSON ) );
		dataSource.getData();
	}

	@Test( expected = DataSourceException.class )
	public void shouldThrowDataSourceExceptionWhenClientErrorCodeIsReturned()
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withStatus( HttpStatus.NOT_FOUND ) );
		dataSource.getData();
	}

	@Test( expected = DataSourceException.class )
	public void shouldThrowDataSourceExceptionWhenServerErrorCodeIsReturned()
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withServerError() );
		dataSource.getData();
	}

	@Test( expected = MalformedDataException.class )
	public void shouldThrowMalformedDataExceptionWhenTotalCountIsNegative() throws Exception
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( -1, 1, 0 ) );
		dataSource.getData();
	}

	@Test( expected = MalformedDataException.class )
	public void shouldThrowMalformedDataExceptionWhenTotalCountChanges() throws Exception
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( 3, 1, 1 ) );
		
		// Second page returns different total transaction count from first
		mockServer.expect( requestTo( URL_GENERATOR.apply( 2 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( 5, 2, 2 ) );
		
		Iterator<Transaction> data = dataSource.getData();
		Iterators.advance( data, 3 );
	}

	@Test( expected = MalformedDataException.class )
	public void shouldThrowMalformedDataExceptionWhenPageNumberIsInvalid() throws Exception
	{
		mockServer.expect( requestTo( URL_GENERATOR.apply( 1 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( 3, 1, 1 ) );
		
		// Number of second page returned does not match expected value
		mockServer.expect( requestTo( URL_GENERATOR.apply( 2 ) ) ).andExpect( method( HttpMethod.GET ) )
						.andRespond( withTransactionPage( 3, 3, 2 ) );
		
		Iterator<Transaction> data = dataSource.getData();
		Iterators.advance( data, 3 );
	}

	private DefaultResponseCreator withTransactionPage( int totalCount, int pageNumber, int numTransactions )
					throws JsonProcessingException
	{
		TransactionPage page = newPage( totalCount, pageNumber, numTransactions );
		String jsonString = MAPPER.writeValueAsString( page );
		return withSuccess( jsonString, MediaType.APPLICATION_JSON );
	}

	private TransactionPage newPage( int totalCount, int pageNumber, int numTransactions )
	{
		List<Transaction> transactions = new ArrayList<>( numTransactions );

		for ( int i = 0; i < numTransactions; i++ )
		{
			transactions.add( new Transaction( LocalDate.now(), new BigDecimal( "10.10" ) ) );
		}

		return new TransactionPage( totalCount, pageNumber, transactions );
	}
}