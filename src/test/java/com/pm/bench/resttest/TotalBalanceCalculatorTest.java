package com.pm.bench.resttest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class TotalBalanceCalculatorTest
{
	private TotalBalanceCalculator calculator;
	
	@Before
	public void setUp()
	{
		calculator = new TotalBalanceCalculator();
	}
	
	@Test
	public void shouldReturnZeroWhenNoTransactionsAreInput()
	{
		assertThat( calculator.getTotal(), is( BigDecimal.ZERO ) );
	}
	
	@Test
	public void shouldReturnTotalValueOfGivenTransactions()
	{
		calculator.accept( newTransaction( "12.23" ) );
		calculator.accept( newTransaction( "-18.99" ) );
		calculator.accept( newTransaction( "3" ) );
		
		assertThat( calculator.getTotal(), is( new BigDecimal( "-3.76" ) ) );
	}
	
	@Test
	public void shouldAcceptNullTransactions()
	{
		calculator.accept( newTransaction( "12.23" ) );
		calculator.accept( null );
		
		assertThat( calculator.getTotal(), is( new BigDecimal( "12.23" ) ) );
	}
	
	@Test
	public void shouldNotRoundOutputValueToTwoDecimalPlaces()
	{
		calculator.accept( newTransaction( "12.233" ) );
		assertThat( calculator.getTotal(), is( new BigDecimal( "12.233" ) ) );
	}
	
	private Transaction newTransaction( String amount )
	{
		return new Transaction( LocalDate.now(), new BigDecimal( amount ) );
	}
}