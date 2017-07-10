package com.pm.bench.resttest;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Transaction
{
	@NonNull
	private final LocalDate transactionDate;
	
	@NonNull
	private final BigDecimal amount;
	
	public Transaction(
			@NonNull final LocalDate transactionDate,
			@NonNull final BigDecimal amount )
	{
		this.transactionDate = requireNonNull( transactionDate );
		this.amount = requireNonNull( amount );
	}

	@NonNull
	public LocalDate getTransactionDate()
	{
		return transactionDate;
	}

	@NonNull
	public BigDecimal getAmount()
	{
		return amount;
	}
}