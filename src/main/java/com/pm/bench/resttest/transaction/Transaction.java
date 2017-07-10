package com.pm.bench.resttest.transaction;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * This class represents a single financial transaction.
 */
public class Transaction
{
	@NonNull
	private final LocalDate transactionDate;

	@NonNull
	private final BigDecimal amount;

	@JsonCreator
	public Transaction(
		@JsonProperty( "Date" ) @NonNull final LocalDate transactionDate,
		@JsonProperty( "Amount" ) @NonNull final BigDecimal amount )
	{
		this.transactionDate = requireNonNull( transactionDate );
		this.amount = requireNonNull( amount );
	}

	@NonNull
	@JsonProperty( "Date" )
	public LocalDate getTransactionDate()
	{
		return transactionDate;
	}

	@NonNull
	@JsonProperty( "Amount" )
	public BigDecimal getAmount()
	{
		return amount;
	}
	
	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper( this )
						.add( "transactionDate", transactionDate )
						.add( "amount", amount )
						.toString();
	}
}