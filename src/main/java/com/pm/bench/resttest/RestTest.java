package com.pm.bench.resttest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.pm.bench.resttest.calculation.DailyBalanceCalculator;
import com.pm.bench.resttest.calculation.TotalBalanceCalculator;
import com.pm.bench.resttest.config.SpringConfig;
import com.pm.bench.resttest.datasource.DataSourceException;
import com.pm.bench.resttest.datasource.MalformedDataException;
import com.pm.bench.resttest.datasource.rest.TransactionDataSource;

@Component
public class RestTest
{
	private static final Log LOG = LogFactory.getLog( TransactionDataSource.class );
	
	private final TransactionDataSource dataSource;

	@Autowired
	public RestTest( final TransactionDataSource dataSource )
	{
		this.dataSource = dataSource;
	}

	public void runTest()
	{
		System.out.println( "Starting Calculation...\n" );
		
		TotalBalanceCalculator totalBalanceCalc = new TotalBalanceCalculator();
		DailyBalanceCalculator dailyBalanceCalc = new DailyBalanceCalculator();

		try
		{
			dataSource.getData().forEachRemaining(
				tx -> {
					totalBalanceCalc.accept( tx );
					dailyBalanceCalc.accept( tx );
				} );
		}
		catch ( MalformedDataException mfde )
		{
			LOG.error( mfde.getMessage(), mfde );
			System.out.println( "Invalid transaction data received from source. Calculation failed." );
			return;
		}
		catch ( DataSourceException dse )
		{
			LOG.error( dse.getMessage(), dse );
			System.out.println( "Failed to obtain transaction data from source. Calculation failed." );
			return;
		}
		catch ( RuntimeException re )
		{
			LOG.error( re.getMessage(), re );
			System.out.println( "Unexpected error occured while processing transaction data. Calculation failed." );
			return;
		}

		System.out.println( "Total Balance: " + totalBalanceCalc.getTotal() + "\n" );

		System.out.println( "Daily Totals" );
		dailyBalanceCalc.getTotals()
				.entrySet()
				.stream()
				.forEach( ( e ) -> System.out.println( "Daily Balance: " + e.getKey() + " => " + e.getValue() ) );
		
		System.out.println( "\nTest complete." );
	}

	@SuppressWarnings( "resource" )
	public static void main( String[] args ) throws Exception
	{
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext( SpringConfig.class );
		RestTest test = context.getBean( RestTest.class );
		test.runTest();
	}
}