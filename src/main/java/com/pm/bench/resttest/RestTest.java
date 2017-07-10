package com.pm.bench.resttest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.pm.bench.resttest.calculation.DailyBalanceCalculator;
import com.pm.bench.resttest.calculation.TotalBalanceCalculator;
import com.pm.bench.resttest.config.SpringConfig;
import com.pm.bench.resttest.datasource.rest.TransactionDataSource;

@Component
public class RestTest
{
	private final TransactionDataSource dataSource;

	@Autowired
	public RestTest( final TransactionDataSource dataSource )
	{
		this.dataSource = dataSource;
	}

	public void runTest()
	{
		System.out.println( "\nStarting Calculation...\n" );
		
		TotalBalanceCalculator totalBalanceCalc = new TotalBalanceCalculator();
		DailyBalanceCalculator dailyBalanceCalc = new DailyBalanceCalculator();

		dataSource.getData().forEachRemaining( tx -> {
			totalBalanceCalc.accept( tx );
			dailyBalanceCalc.accept( tx );
		} );

		System.out.println( "Total Balance: " + totalBalanceCalc.getTotal() + "\n" );

		System.out.println( "Daily Totals" );
		dailyBalanceCalc.getTotals().entrySet().stream()
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