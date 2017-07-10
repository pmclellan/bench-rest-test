package com.pm.bench.resttest.calculation;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pm.bench.resttest.calculation.DailyBalanceCalculator;
import com.pm.bench.resttest.transaction.Transaction;

public class DailyBalanceCalculatorTest
{
	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalDate TOMORROW = TODAY.plusDays( 1 );
	private static final LocalDate DAY_AFTER_TOMORROW = TOMORROW.plusDays( 1 );

	private DailyBalanceCalculator calculator;
	
	@Before
	public void setUp()
	{
		calculator = new DailyBalanceCalculator();
	}
	
	@Test
	public void shouldReturnEmptyMapWhenNoTransactionsAreInput()
	{
		assertThat( calculator.getTotals().size(), is( 0 ) );
	}
	
	@Test
	public void shouldReturnCorrectTotalForSingleDateWithMultipleTransactions()
	{
		calculator.accept( new Transaction( TODAY, new BigDecimal( "10.15" ) ) );
		calculator.accept( new Transaction( TODAY, new BigDecimal( "-20" ) ) );
		calculator.accept( new Transaction( TODAY, new BigDecimal( "53.88" ) ) );
		
		Map<LocalDate, BigDecimal> totals = calculator.getTotals();
		assertThat( totals.size(), is( 1 ) );
		assertThat( totals.get( TODAY ), is( new BigDecimal( "44.03" ) ) );
	}
	
	@Test
	public void shouldAcceptNullTransactions()
	{
		calculator.accept( new Transaction( TODAY, new BigDecimal( "10.10" ) ) );
		calculator.accept( null );
		calculator.accept( new Transaction( TODAY, new BigDecimal( "53.88" ) ) );
		
		Map<LocalDate, BigDecimal> totals = calculator.getTotals();
		assertThat( totals.size(), is( 1 ) );
		assertThat( totals.get( TODAY ), is( new BigDecimal( "63.98" ) ) );
	}

	@Test
	public void shouldReturnCorrectTotalForMultipleDatesWithSingleTransaction()
	{
		calculator.accept( new Transaction( TODAY, new BigDecimal( "10.15" ) ) );
		calculator.accept( new Transaction( TOMORROW, new BigDecimal( "-20" ) ) );
		calculator.accept( new Transaction( DAY_AFTER_TOMORROW, new BigDecimal( "53.88" ) ) );
		
		Map<LocalDate, BigDecimal> totals = calculator.getTotals();
		assertThat( totals.size(), is( 3 ) );
		assertThat( totals.get( TODAY ), is( new BigDecimal( "10.15" ) ) );
		assertThat( totals.get( TOMORROW ), is( new BigDecimal( "-9.85" ) ) );
		assertThat( totals.get( DAY_AFTER_TOMORROW ), is( new BigDecimal( "44.03" ) ) );
	}
	
	@Test
	public void shouldReturnCorrectTotalForMultipleDatesWithMultipleTransactions()
	{
		calculator.accept( new Transaction( TODAY, new BigDecimal( "10.15" ) ) );
		calculator.accept( new Transaction( TOMORROW, new BigDecimal( "-20" ) ) );
		calculator.accept( new Transaction( DAY_AFTER_TOMORROW, new BigDecimal( "22.22" ) ) );
		
		calculator.accept( new Transaction( TODAY, new BigDecimal( "5.00" ) ) );
		calculator.accept( new Transaction( TOMORROW, new BigDecimal( "50.22" ) ) );
		calculator.accept( new Transaction( DAY_AFTER_TOMORROW, new BigDecimal( "-100" ) ) );
		
		Map<LocalDate, BigDecimal> totals = calculator.getTotals();
		assertThat( totals.size(), is( 3 ) );
		assertThat( totals.get( TODAY ), is( new BigDecimal( "15.15" ) ) );
		assertThat( totals.get( TOMORROW ), is( new BigDecimal( "45.37" ) ) );
		assertThat( totals.get( DAY_AFTER_TOMORROW ), is( new BigDecimal( "-32.41" ) ) );
	}
	
	@Test
	public void shouldReturnTotalsInAscendingDateOrderRegardlessOfInputOrder()
	{
		calculator.accept( new Transaction( DAY_AFTER_TOMORROW, new BigDecimal( "53.88" ) ) );
		calculator.accept( new Transaction( TODAY, new BigDecimal( "10.15" ) ) );
		calculator.accept( new Transaction( TOMORROW, new BigDecimal( "-20" ) ) );
		
		assertThat( calculator.getTotals().keySet(), contains( TODAY, TOMORROW, DAY_AFTER_TOMORROW ) );
	}
	
	@Test
	public void shouldReturnTotalsForDatesInRangeWhereNoTransactionsWereInput()
	{
		calculator.accept( new Transaction( TODAY, new BigDecimal( "10.15" ) ) );
		calculator.accept( new Transaction( DAY_AFTER_TOMORROW, new BigDecimal( "22.22" ) ) );
		
		Map<LocalDate, BigDecimal> totals = calculator.getTotals();
		assertThat( totals.size(), is( 3 ) );
		assertThat( totals.get( TODAY ), is( new BigDecimal( "10.15" ) ) );
		assertThat( totals.get( TOMORROW ), is( new BigDecimal( "10.15" ) ) );
		assertThat( totals.get( DAY_AFTER_TOMORROW ), is( new BigDecimal( "32.37" ) ) );
	}
}