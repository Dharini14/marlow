package com.marlow.bankingapp.models

import slick.jdbc.MySQLProfile.api._

case class Transaction(id: Long, accountId: Long, amount: BigDecimal, transactionType: String)


class Transactions(tag: Tag) extends Table[Transaction](tag, "transactions") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key
  def accountId = column[Long]("account_id") // Foreign key linking to accounts
  def transactionType = column[String]("transaction_type") // Transaction amount
  def amount = column[BigDecimal]("amount") // Type": DEPOSIT / WITHDRAWAL

  // Foreign key constraint
  def accountFk = foreignKey("account_fk", accountId, TableQuery[Accounts])(_.id, onDelete = ForeignKeyAction.Cascade)

  def * = (id, accountId, amount, transactionType) <> (Transaction.tupled, Transaction.unapply)

}
