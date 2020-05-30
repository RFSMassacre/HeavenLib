package us.rfsmassacre.heavenlib.spigot.managers;

import org.bukkit.Bukkit;
import us.rfsmassacre.heavenlib.spigot.plugins.SpigotPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Set;

public abstract class DatabaseManager<T, X> extends Manager
{
    private Connection connection;
    private String hostname;
    private String database;
    private String username;
    private String password;
    private int port;
    private boolean ssl;

    protected String tableName;

    protected LocaleManager locale;

    public DatabaseManager(SpigotPlugin instance, String tableName)
    {
        super(instance);

        ConfigManager config = instance.getConfigManager();
        this.hostname = config.getString("mysql.hostname");
        this.database = config.getString("mysql.database");
        this.username = config.getString("mysql.username");
        this.password = config.getString("mysql.password");
        this.port = config.getInt("mysql.port");
        this.ssl = config.getBoolean("mysql.ssl");

        this.tableName = tableName;

        this.locale = instance.getLocaleManager();

        //Notify if it can connect to a server.
        try
        {
            openConnection();
            locale.sendLocale(Bukkit.getConsoleSender(), "mysql.connected", "{table}", tableName);
        }
        catch (SQLException | ClassNotFoundException exception)
        {
            locale.sendLocale(Bukkit.getConsoleSender(), "mysql.failed");
        }
    }

    private void openConnection() throws SQLException, ClassNotFoundException
    {
        if (connection != null && !connection.isClosed())
        {
            return;
        }

        synchronized (this)
        {
            if (connection != null && !connection.isClosed())
            {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + hostname
                    + ":" + port + "/" + database + "?autoReconnect=true&useSSL="
                    + Boolean.toString(ssl).toLowerCase(), username, password);
        }
    }

    private void closeConnection() throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            return;
        }

        connection.close();
    }

    private Statement createStatement() throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            return null;
        }

        return connection.createStatement();
    }

    //Create a table based on the data of the object
    protected void createTable(String primaryKey)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                primaryKey + " NOT NULL PRIMARY KEY)";

        doSyncUpdate(sql);
    }
    protected void addColumns(String...columnTypes)
    {
        ArrayList<String> columns = new ArrayList<>();
        for (String columnType : columnTypes)
        {
            String column = "ALTER TABLE " + tableName + " " +
                    "ADD " + columnType;

            columns.add(column);
        }
        doSyncUpdate(columns.toArray(new String[0]));
    }

    /*
     * In order to retrieve data, my research shows that you need to pass an Object first, and the modifications
     * to that object within the same thread will be the thing to do so. This means you pass empty objects
     * so they can be filled from the same address.
     */

    //Transfer a result set to an object.
    public abstract T load(ResultSet result) throws SQLException;

    //Save object into the database.
    public abstract void save(T t, boolean async);

    //Save a set of objects into the database
    public abstract void saveSet(Set<T> set, boolean async);

    //X is the identifier in order to pull the data from the database.
    public abstract void query(X x, boolean async, CallbackQuery<T> callback);

    //Removes object from database
    public abstract void delete(X x, boolean async);

    /*
     * Other things to make retrieving data possible.
     */
    public interface CallbackQuery<T>
    {
        void execute(T t);
    }

    protected void doAsyncQuery(String sql, final CallbackQuery<T> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () ->
        {
            try
            {
                openConnection();

                Statement statement = createStatement();
                ResultSet result = statement.executeQuery(sql);
                T t = load(result);

                Bukkit.getScheduler().runTask(instance, () ->
                {
                    callback.execute(t);
                });
            }
            catch (SQLException | ClassNotFoundException exception)
            {
                //Do nothing
            }
        });
    }
    protected void doSyncQuery(String sql, final CallbackQuery<T> callback)
    {
        try
        {
            openConnection();

            Statement statement = createStatement();
            ResultSet result = statement.executeQuery(sql);
            T t = load(result);
            callback.execute(t);
        }
        catch (SQLException | ClassNotFoundException exception)
        {
            //Do nothing
        }
    }

    protected void doAsyncUpdate(String... sqls)
    {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () ->
        {
            try
            {
                openConnection();

                Statement statement = createStatement();
                for (String sql : sqls)
                {
                    try
                    {
                        statement.executeUpdate(sql);
                    }
                    catch (Exception exception)
                    {
                        //Do nothing
                        //exception.printStackTrace();
                    }
                }
            }
            catch (ClassNotFoundException | SQLException exception)
            {
                //exception.printStackTrace();
            }
        });
    }
    protected void doSyncUpdate(String... sqls)
    {
        try
        {
            openConnection();

            Statement statement = createStatement();
            for (String sql : sqls)
            {
                try
                {
                    statement.executeUpdate(sql);
                }
                catch (Exception exception)
                {
                    //Do nothing
                    //exception.printStackTrace();
                }
            }
        }
        catch (ClassNotFoundException | SQLException exception)
        {
            //exception.printStackTrace();
        }
    }
}
