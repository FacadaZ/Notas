package mains;

import conexion.Conexion;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Notas1";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        Conexion conexion = Conexion.getInstance();

        try {
            conexion.conectar(DB_URL, DB_USER, DB_PASSWORD);

            do {
                System.out.println("OPCIONES:");
                System.out.println("1 - Ver estudiantes");
                System.out.println("2 - Registrar nuevo estudiante");
                System.out.println("3 - Registrar nuevo estudiante con materias y notas");
                System.out.println("4 - Ver materias de estudiante");
                System.out.println("5 - ver Notas De Estudiante En Materia");
                System.out.println("6 - Actualizar estudiante");
                System.out.println("7 - Actualizar materia");
                System.out.println("8 - Eliminar estudiante");
                System.out.println("9 - Eliminar materia");
                System.out.println("0 - Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                        case 1:
                            verEstudiantes();
                        break;
                        case 2:
                            registrarNuevoEstudiante();
                        break;
                        case 3:
                            registrarNuevoEstudianteConMateriasYNotas();
                        break;
                        case 4:
                            verMateriasDeEstudiante();
                        break;
                        case 5:
                            verNotasDeEstudianteEnMateria();
                        break;
                        case 6:
                            actualizarEstudiante();
                        break;
                        case 7:
                            actualizarMateria();
                        break;
                        case 8:
                            eliminarEstudiante();
                        break;
                        case 9:
                            eliminarMateria();
                        break;
                    case 0:
                        System.out.println("CERRANDO PROGRAMA");
                        break;
                    default:
                        System.out.println("Opción inválida. Por favor, seleccione una opción válida.");
                }
            } while (opcion != 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexion.desconectar();
        }
    }



    private static void verEstudiantes() throws SQLException {
        Connection connection = Conexion.getInstance().getConexion();

        String selectEstudiantesSQL = "SELECT * FROM estudiantes";
        try (PreparedStatement statement = connection.prepareStatement(selectEstudiantesSQL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Lista de estudiantes:");
                System.out.println("ID\tNombre\tApellido\tFecha de nacimiento");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_estudiante");
                    String nombre = resultSet.getString("nombre");
                    String apellido = resultSet.getString("apellido");
                    Date fechaNacimiento = resultSet.getDate("fecha_nacimiento");
                    System.out.println(id + "\t" + nombre + "\t" + apellido + "\t" + fechaNacimiento);
                }
            }
        }
    }
    private static void verMateriasDeEstudiante() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        Connection connection = Conexion.getInstance().getConexion();
        String selectEstudiantesSQL = "SELECT id_estudiante, nombre, apellido FROM estudiantes";
        try (PreparedStatement statementEstudiantes = connection.prepareStatement(selectEstudiantesSQL)) {
            try (ResultSet resultSet = statementEstudiantes.executeQuery()) {
                System.out.println("Lista de estudiantes:");
                System.out.println("ID\tNombre\tApellido");
                while (resultSet.next()) {
                    int idEstudiante = resultSet.getInt("id_estudiante");
                    String nombre = resultSet.getString("nombre");
                    String apellido = resultSet.getString("apellido");
                    System.out.println(idEstudiante + "\t" + nombre + "\t" + apellido);
                }
            }
        }


        System.out.println("Ingrese el ID del estudiante del que desea ver las materias:");
        int idEstudiante = scanner.nextInt();
        scanner.nextLine();


        String selectMateriasSQL = "SELECT id_materia, nombre FROM materias WHERE id_estudiante = ?";
        try (PreparedStatement statementMaterias = connection.prepareStatement(selectMateriasSQL)) {
            statementMaterias.setInt(1, idEstudiante);
            try (ResultSet resultSet = statementMaterias.executeQuery()) {
                System.out.println("Materias del estudiante:");
                System.out.println("ID\tNombre de la materia");
                while (resultSet.next()) {
                    int idMateria = resultSet.getInt("id_materia");
                    String nombreMateria = resultSet.getString("nombre");
                    System.out.println(idMateria + "\t" + nombreMateria);
                }
            }
        }
    }



    private static void registrarNuevoEstudiante() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el nombre del estudiante:");
        String nombre = scanner.nextLine();

        System.out.println("Ingrese el apellido del estudiante:");
        String apellido = scanner.nextLine();

        System.out.println("Ingrese la fecha de nacimiento del estudiante (formato YYYY-MM-DD):");
        String fechaNacimientoString = scanner.nextLine();
        Date fechaNacimiento = Date.valueOf(fechaNacimientoString);

        Connection connection = Conexion.getInstance().getConexion();

        String insertEstudianteSQL = "INSERT INTO estudiantes (nombre, apellido, fecha_nacimiento) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertEstudianteSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, nombre);
            statement.setString(2, apellido);
            statement.setDate(3, fechaNacimiento);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el estudiante ");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idEstudiante = generatedKeys.getInt(1);
                    System.out.println("El estudiante " + idEstudiante + " ha sido registrado exitosamente.");
                } else {
                    throw new SQLException("No se pudo obtener el ID del estudiante.");
                }
            }
        }
    }

    private static void registrarNuevoEstudianteConMateriasYNotas() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el nombre del estudiante:");
        String nombre = scanner.nextLine();

        System.out.println("Ingrese el apellido del estudiante:");
        String apellido = scanner.nextLine();

        System.out.println("Ingrese la fecha de nacimiento del estudiante (formato YYYY-MM-DD):");
        String fechaNacimientoString = scanner.nextLine();
        Date fechaNacimiento = Date.valueOf(fechaNacimientoString);

        Connection connection = Conexion.getInstance().getConexion();
        String selectCarrerasSQL = "SELECT id_carrera, nombre FROM carreras";
        try (PreparedStatement statementCarreras = connection.prepareStatement(selectCarrerasSQL)) {
            try (ResultSet resultSet = statementCarreras.executeQuery()) {
                System.out.println("Lista de carreras:");
                while (resultSet.next()) {
                    int idCarrera = resultSet.getInt("id_carrera");
                    String nombreCarrera = resultSet.getString("nombre");
                    System.out.println(idCarrera + " - " + nombreCarrera);
                }
            }
        }

        System.out.println("Ingrese el ID de la carrera del estudiante:");
        int idCarrera = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese el nombre de la materia:");
        String nombreMateria = scanner.nextLine();

        System.out.println("Ingrese la cantidad de créditos de la materia:");
        int creditos = scanner.nextInt();

        System.out.println("Ingrese la nota del estudiante en la materia:");
        double nota = scanner.nextDouble();

        String insertEstudianteSQL = "INSERT INTO estudiantes (nombre, apellido, fecha_nacimiento) VALUES (?, ?, ?)";
        try (PreparedStatement statementEstudiante = connection.prepareStatement(insertEstudianteSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statementEstudiante.setString(1, nombre);
            statementEstudiante.setString(2, apellido);
            statementEstudiante.setDate(3, fechaNacimiento);
            int affectedRows = statementEstudiante.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el estudiante");
            }

            try (ResultSet generatedKeys = statementEstudiante.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idEstudiante = generatedKeys.getInt(1);
                    System.out.println("El estudiante " + idEstudiante + " ha sido registrado exitosamente.");

                    String insertMateriaSQL = "INSERT INTO materias (id_estudiante, id_carrera, nombre, creditos) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement statementMateria = connection.prepareStatement(insertMateriaSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        statementMateria.setInt(1, idEstudiante);
                        statementMateria.setInt(2, idCarrera);
                        statementMateria.setString(3, nombreMateria);
                        statementMateria.setInt(4, creditos);
                        affectedRows = statementMateria.executeUpdate();

                        if (affectedRows == 0) {
                            throw new SQLException("No se pudo insertar la materia para el estudiante");
                        }

                        try (ResultSet generatedKeysMateria = statementMateria.getGeneratedKeys()) {
                            if (generatedKeysMateria.next()) {
                                int idMateria = generatedKeysMateria.getInt(1);

                                String insertNotaSQL = "INSERT INTO notas (id_estudiante, id_materia, id_carrera, nota) VALUES (?, ?, ?, ?)";
                                try (PreparedStatement statementNota = connection.prepareStatement(insertNotaSQL)) {
                                    statementNota.setInt(1, idEstudiante);
                                    statementNota.setInt(2, idMateria);
                                    statementNota.setInt(3, idCarrera);
                                    statementNota.setDouble(4, nota);
                                    statementNota.executeUpdate();
                                    System.out.println("La materia y la nota asociada han sido registradas exitosamente.");
                                } catch (SQLException e) {
                                    throw new SQLException("No se pudo insertar la nota para el estudiante.");
                                }
                            }
                        }
                    }
                } else {
                    throw new SQLException("No se pudo obtener el ID del estudiante.");
                }
            }
        }
    }
    private static void verNotasDeEstudianteEnMateria() throws SQLException {
        Connection connection = Conexion.getInstance().getConexion();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el ID del estudiante:");
        int idEstudiante = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese el ID de la materia:");
        int idMateria = scanner.nextInt();
        scanner.nextLine();

        connection = Conexion.getInstance().getConexion();
        String selectNotasSQL = "SELECT nota FROM notas WHERE id_estudiante = ? AND id_materia = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectNotasSQL)) {
            statement.setInt(1, idEstudiante);
            statement.setInt(2, idMateria);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Notas del estudiante en la materia:");
                while (resultSet.next()) {
                    double nota = resultSet.getDouble("nota");
                    System.out.println("Nota: " + nota);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void actualizarEstudiante() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = Conexion.getInstance().getConexion();

        System.out.println("Ingrese el ID del estudiante que desea actualizar:");
        int idEstudiante = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese el nuevo nombre del estudiante:");
        String nuevoNombre = scanner.nextLine();

        System.out.println("Ingrese el nuevo apellido del estudiante:");
        String nuevoApellido = scanner.nextLine();

        System.out.println("Ingrese la nueva fecha de nacimiento del estudiante (formato YYYY-MM-DD):");
        String nuevaFechaNacimiento = scanner.nextLine();

        String updateEstudianteSQL = "UPDATE estudiantes SET nombre = ?, apellido = ?, fecha_nacimiento = ? WHERE id_estudiante = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateEstudianteSQL)) {
            statement.setString(1, nuevoNombre);
            statement.setString(2, nuevoApellido);
            statement.setDate(3, Date.valueOf(nuevaFechaNacimiento));
            statement.setInt(4, idEstudiante);

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("la infromacion del estudiante ha sido actualizada correctamente.");
            } else {

            }
        }
    }
    private static void actualizarMateria() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = Conexion.getInstance().getConexion();

        System.out.println("ingresar el id de la materia que desea actualizar");
        int idMateria = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese el nuevo nombre de la materia");
        String nuevoNombreMateria = scanner.nextLine();

        System.out.println("Ingrese la nueva cantidad de créditos de la materia");
        int nuevosCreditos = scanner.nextInt();
        scanner.nextLine();

        String updateMateriaSQL = "UPDATE materias SET nombre = ?, creditos = ? WHERE id_materia = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateMateriaSQL)) {
            statement.setString(1, nuevoNombreMateria);
            statement.setInt(2, nuevosCreditos);
            statement.setInt(3, idMateria);

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("La información de la materia ha sido actualizada correctamente");
            } else {
                System.out.println("No se encontró ninguna materia con el ID proporcionado");
            }
        }
    }
    private static void eliminarEstudiante() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = Conexion.getInstance().getConexion();

        System.out.println("Ingrese el ID del estudiante que desea eliminar:");
        int idEstudiante = scanner.nextInt();
        scanner.nextLine();

        try {
            String deleteMateriasSQL = "DELETE FROM materias WHERE id_estudiante = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteMateriasSQL)) {
                statement.setInt(1, idEstudiante);
                statement.executeUpdate();
            }

            String deleteEstudianteSQL = "DELETE FROM estudiantes WHERE id_estudiante = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteEstudianteSQL)) {
                statement.setInt(1, idEstudiante);

                int filasEliminadas = statement.executeUpdate();
                if (filasEliminadas > 0) {
                    System.out.println("El estudiante ha sido eliminado correctamente.");
                } else {
                    System.out.println("No se encontró ningún estudiante con el ID proporcionado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("elminia primero las materias  asociadas al estudiante");
        }
    }


    private static void eliminarMateria() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = Conexion.getInstance().getConexion();

        System.out.println("Ingrese el ID de la materia que desea eliminar:");
        int idMateria = scanner.nextInt();
        scanner.nextLine();

        String deleteNotasSQL = "DELETE FROM notas WHERE id_materia = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteNotasSQL)) {
            statement.setInt(1, idMateria);
            statement.executeUpdate();
        }

        String deleteMateriaSQL = "DELETE FROM materias WHERE id_materia = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteMateriaSQL)) {
            statement.setInt(1, idMateria);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("La materia ha sido eliminada correctamente.");
            } else {
                System.out.println("No se encontró ninguna materia con el ID proporcionado.");
            }
        }
    }



}