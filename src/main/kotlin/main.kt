import java.lang.Exception
import java.sql.DriverManager

fun main(args: Array<String>) {
    print("Enter 1 to run unsafe code, or 2 to run safe code: ")
    when (readLine()!!) {
        "1" -> unsafeCode()
        "2" -> safeCode()
    }
}

private fun unsafeCode() {
    print("Enter user_id: ")
    val userId = readLine()!!
    val acctBalanceQuery = "SELECT acctNum, balance FROM accounts WHERE user_id = $userId;"
    DriverManager.getConnection("jdbc:mariadb://localhost:3306/mydb", "user", "password").use {
        it.createStatement().use {
            println("Running the query:")
            println(acctBalanceQuery)
            it.executeQuery(acctBalanceQuery).use { rs ->
                println("AcctNum Balance")
                println("---------------")
                while (rs.next()) {
                    println("${rs.getInt("acctNum")} ${rs.getFloat("balance")}")
                }
            }
        }
    }
}

private fun safeCode() {
    print("Enter user_id: ")
    val userId = readLine()!!
    try {
        DriverManager.getConnection("jdbc:mariadb://localhost:3306/mydb", "user", "password").use {
            val preparedStatement = it.prepareStatement("SELECT acctNum, balance FROM accounts WHERE user_id = ?;")
            preparedStatement.setInt(1, userId.toInt())
            println("Running the query:")
            println(preparedStatement)
            preparedStatement.executeQuery().use { rs ->
                println("AcctNum Balance")
                println("---------------")
                while (rs.next()) {
                    println("${rs.getInt("acctNum")} ${rs.getFloat("balance")}")
                }
            }
        }
    } catch (nfe: NumberFormatException) {
        nfe.printStackTrace()
        println("Trying to inject SQL huh?")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}