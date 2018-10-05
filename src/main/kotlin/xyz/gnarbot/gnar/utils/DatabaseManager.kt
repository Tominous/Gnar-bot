package xyz.gnarbot.gnar.utils

import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.Bot
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class DatabaseManager(val bot: Bot) {

    private val LOG = LoggerFactory.getLogger("Database")
    private val conn : Connection?

    init {
        conn = establishConnection()
    }

    private fun establishConnection() : Connection? {
        var c : Connection? = null
        try {
            Class.forName("org.postgresql.Driver")
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gnar", bot.credentials.databaseUsername, bot.credentials.databasePassword)
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(0)
        }
        if (c == null) {
            System.exit(0)
        } else {
            return c
        }
        System.exit(0)
        return null
    }

    fun createTable(tableName: String, options: Array<String>) {
        //CREATE TABLE TABLENAME (OPTIONS)
        executeStatement("CREATE TABLE $tableName VALUES (${options.sortedArray()})")

    }

    fun setData(tableName: String,id: Long, options : HashMap<String, String>) {
        for(s: String in options.keys) {
            //UPDATE TABLE SET KEY = VALUE WHERE ID = ID
            executeStatement("UPDATE $tableName SET $s = ${options.getValue(s)} WHERE ID = $id")
        }
    }

    fun deleteData(tableName: String,id: Long) {
        //DELETE FROM TABLE WHERE ID = ID
        executeStatement("DELETE FROM $tableName WHERE ID = $id")
    }

    fun selectData(tableName: String,id: Long) : ResultSet {
        //SELECT * FROM TABLE WHERE ID = ID
        return executeStatement("SELECT * FROM $tableName WHERE ID = $id")
    }

    fun <T> insertData(tableName: String,id: Long, options : T) {
        //INSERT INTO TABLE VALUES (KEY, VALUE, ...)
        print(options)
        executeStatement("INSERT INTO $tableName VALUES $options WHERE ID = $id")
    }

    private fun executeStatement(s: String) : ResultSet {
        val statement : Statement = conn!!.createStatement()
        statement.executeQuery(s)
        conn.commit()
        return statement.resultSet
    }
}