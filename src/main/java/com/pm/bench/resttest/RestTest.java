package com.pm.bench.resttest;

public class RestTest
{
	private final TransactionDataSource dataSource;
	
	public RestTest( final TransactionDataSource dataSource )
	{
		this.dataSource = dataSource;
	}
	
	public void runTest()
	{
		TotalBalanceCalculator totalBalanceCalc = new TotalBalanceCalculator();
		DailyBalanceCalculator dailyBalanceCalc = new DailyBalanceCalculator();
		
		dataSource.getData()
				.forEach( tx -> { totalBalanceCalc.accept( tx ); dailyBalanceCalc.accept( tx ); } );
		
		System.out.println( totalBalanceCalc.getTotal() );
		System.out.println( dailyBalanceCalc.getTotals() );
	}
	
	public static void main( String[] args ) throws Exception
	{
		RestTest test = new RestTest( new TransactionDataSource() );
		test.runTest();
	}
}