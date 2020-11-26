package us.rfsmassacre.heavenlib.spigot.managers;

import org.bukkit.Bukkit;
import us.rfsmassacre.heavenlib.spigot.plugins.SpigotPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class DatabaseManager<T, X> extends Manager
{
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
            Connection connection = createConnection();
            locale.sendLocale(Bukkit.getConsoleSender(), "mysql.connected", "{table}", tableName);
            closeConnection(connection);
        }
        catch (SQLException | ClassNotFoundException exception)
        {
            locale.sendLocale(Bukkit.getConsoleSender(), "mysql.failed");
        }
    }

    private Connection createConnection() throws SQLException, ClassNotFoundException
    {
        synchronized (this)
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + hostname
                    + ":" + port + "/" + database + "?autoReconnect=true&useSSL="
                    + Boolean.toString(ssl).toLowerCase(), username, password);

            return connection;
        }
    }
    /*
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
     */
    public void closeConnection(Connection connection) throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            return;
        }

        connection.close();
    }

    private PreparedStatement prepareStatement(Connection connection, String sql) throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            return null;
        }

        return connection.prepareStatement(sql);
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

    //Update object into the database.
    public abstract void update(T t, boolean async);

    //X is the identifier in order to pull the data from the database.
    public abstract void query(X x, boolean async, CallbackQuery<T> callback);

    //Removes object from database
    public abstract void delete(X x, boolean async);

    //Load a set of objects from the database.
    public Set<T> loadSet(ResultSet result) throws SQLException
    {
        Set<T> set = new HashSet<>();
        while (result.next())
        {
            set.add(load(result));
        }
        return set;
    }

    //Save a set of objects into the database
    public void saveSet(Set<T> set, boolean async)
    {
        for (T t : set)
        {
            save(t, async);
        }
    }

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
                Connection connection = createConnection();
                PreparedStatement statement = prepareStatement(connection, sql);
                ResultSet result = statement.executeQuery();
                T t = load(result);

                Bukkit.getScheduler().runTask(instance, () ->
                {
                    callback.execute(t);
                });

                closeConnection(connection);
            }
            catch (SQLException | ClassNotFoundException exception)
            {
                //Do nothing
                exception.printStackTrace();
            }
        });
    }
    protected void doSyncQuery(String sql, final CallbackQuery<T> callback)
    {
        try
        {
            Connection connection = createConnection();
            PreparedStatement statement = prepareStatement(connection, sql);
            ResultSet result = statement.executeQuery(sql);
            T t = load(result);
            callback.execute(t);
            closeConnection(connection);
        }
        catch (SQLException | ClassNotFoundException exception)
        {
            //Do nothing
            exception.printStackTrace();
        }
    }

    protected void doAsyncUpdate(String... sqls)
    {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () ->
        {
            try
            {
                Connection connection = createConnection();
                for (String sql : sqls)
                {
                    try
                    {
                        PreparedStatement statement = prepareStatement(connection, sql);
                        statement.executeUpdate(sql);
                    }
                    catch (Exception exception)
                    {
                        //Do nothing
                        if (exception.getMessage().contains("Duplicate column name"))
                        {
                            continue;
                        }

                        exception.printStackTrace();
                    }
                }
                closeConnection(connection);
            }
            catch (ClassNotFoundException | SQLException exception)
            {
                exception.printStackTrace();
            }
        });
    }
    protected void doSyncUpdate(String... sqls)
    {
        try
        {
            Connection connection = createConnection();
            for (String sql : sqls)
            {
                try
                {
                    PreparedStatement statement = prepareStatement(connection, sql);
                    statement.executeUpdate(sql);
                }
                catch (Exception exception)
                {
                    //Do nothing
                    if (exception.getMessage().contains("Duplicate column name"))
                    {
                        continue;
                    }

                    exception.printStackTrace();
                }
            }
            closeConnection(connection);
        }
        catch (ClassNotFoundException | SQLException exception)
        {
            exception.printStackTrace();
        }
    }
}
