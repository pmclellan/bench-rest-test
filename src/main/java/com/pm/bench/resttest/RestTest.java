package com.pm.bench.resttest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

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
		TotalBalanceCalculator totalBalanceCalc = new TotalBalanceCalculator();
		DailyBalanceCalculator dailyBalanceCalc = new DailyBalanceCalculator();

		dataSource.getData().forEachRemaining( tx -> {
			totalBalanceCalc.accept( tx );
			dailyBalanceCalc.accept( tx );
		} );

		System.out.println( "Total Balance: " + totalBalanceCalc.getTotal() );

		dailyBalanceCalc.getTotals().entrySet().stream()
						.forEach( ( e ) -> System.out.println( "Daily Balance: " + e.getKey() + " => " + e.getValue() ) );
	}

	@SuppressWarnings( "resource" )
	public static void main( String[] args ) throws Exception
	{
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext( SpringConfig.class );
		RestTest test = context.getBean( RestTest.class );
		test.runTest();
	}
}