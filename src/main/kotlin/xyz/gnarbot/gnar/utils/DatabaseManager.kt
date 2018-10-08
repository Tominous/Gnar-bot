package xyz.gnarbot.gnar.utils

import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.Bot
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import com.google.gson.Gson



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
        executeUpdate("CREATE TABLE $tableName VALUES (${options.sortedArray()})")

    }

    fun setData(tableName: String,id: Long, options : HashMap<String, String>) {
        for(s: String in options.keys) {
            //UPDATE TABLE SET KEY = VALUE WHERE ID = ID
            executeUpdate("UPDATE $tableName SET $s = ${options.getValue(s)} WHERE ID = $id")
        }
    }

    fun deleteData(tableName: String,id: Long) {
        //DELETE FROM TABLE WHERE ID = ID
        executeUpdate("DELETE FROM $tableName WHERE ID = $id")
    }

    fun selectData(tableName: String,id: Long) : ResultSet? {
        //SELECT * FROM TABLE WHERE ID = ID
        return executeQuery("SELECT * FROM $tableName WHERE ID = $id")
    }

    fun <T> insertData(tableName: String,id: Long, options : T) {
        //INSERT INTO TABLE VALUES (KEY, VALUE, ...)
        val gson = Gson()
        val json = gson.toJson(options) //convert
        println(json)
        println("INSERT INTO $tableName (id, dataObject) VALUES (dataObject = '$json') WHERE ID = $id")
        executeUpdate("INSERT INTO $tableName (id, dataobject) VALUES ($id, '$json')")
    }

    private fun executeUpdate(s: String) : ResultSet? {
        val statement : Statement = conn!!.createStatement()
        statement.executeUpdate(s)
        return if (statement.resultSet == null ) statement.resultSet else null
    }

    private fun executeQuery(s: String) : ResultSet? {
        val statement : Statement = conn!!.createStatement()
        statement.executeQuery(s)
        return if (statement.resultSet == null ) statement.resultSet else null
    }

    fun checkIfExists(id: String, table: String) : Boolean {
        return executeQuery("SELECT * FROM $table WHERE ID = $id") == null
    }
}