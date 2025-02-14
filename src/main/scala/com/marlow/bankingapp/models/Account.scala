package com.marlow.bankingapp.models

import slick.jdbc.MySQLProfile.api._

case class Account(id: Long, accountNumber: String, balance: BigDecimal)

class Accounts(tag: Tag) extends Table[Account](tag, "accounts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key with auto-increment
  def accountNumber = column[String]("account_number", O.Unique) // Unique account number
  def balance = column[BigDecimal]("balance") // Account balance

  def * = (id, accountNumber, balance) <> (Account.tupled, Account.unapply)
}