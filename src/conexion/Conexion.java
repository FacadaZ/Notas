package conexion;

import java.sql.*;

public class Conexion {
    private static String database = "Notas1";
    private static String host = "127.0.0.1";
    private static String port = "5432";
    private static final String URL = "jdbc:postgresql://" + host + ":" + port + "/" + database;
    public static final String USUARIO = "postgres";
    public static final String PASSWORD = "root";

    private static Conexion instance;

    private Connection conexion;

    public Conexion() {
    }

    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

    public void conectar(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    public Connection getConexion() {
        return conexion;
    }

    public void desconectar()  {
        try {
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet consulta(String sql) throws SQLException {
        Statement sentencia = conexion.createStatement();
        return sentencia.executeQuery(sql);
    }
    public void ejecutarSQL(String sql) {
        try {
            Statement st= conexion.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
