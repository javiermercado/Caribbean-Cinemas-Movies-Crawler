package com.JavierMercado;

/**
 * Created on 1/21/2017.
 */

import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.tools.OmdbBuilder;

import java.sql.*;

public class ConnectDB {

    //Información para conectarnos a la base de datos

    String databaseURL = "jdbc:mysql://localhost:3306/testing";
    String user = "root";
    String password = "";
    Connection conn = null;

    private Statement st = null; //querys
    private ResultSet rs = null; //resultados
    static OmdbApi omdb = new OmdbApi();

    public ConnectDB() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(databaseURL, user, password);
            //st = conn.createStatement();

            if (conn != null) {
                System.out.println("Conectado a la base de datos");
            }else {
                cerrarConexion();
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("No se pudo conseguir el 'driver' de la base de datos");
            ex.printStackTrace();

        } catch (SQLException ex) {
            //Mostrar error si el usuario o la contraseña está incorrecto o es inválido
            System.out.println("Un error ha ocurrido. Usuario/contraseña es invalido");
        }
    }

    public void insertData(String pelicula_id, String pelicula_url, String poster_url, String pelicula_nombre, String pelicula_resumen, String rating, String fecha, String pelicula_datos1, String pelicula_datos2) throws SQLException {

        //Método para insertar los datos en la base de datos, en sus respectivas columnas

        String query = "INSERT INTO `records`(`pelicula_id`, `pelicula_url`,`pelicula_poster`, `pelicula_nombre`, `pelicula_resumen`, `rating`, `fecha`, `pelicula_datos1`, `pelicula_datos2`) " +
                "VALUES ('" + pelicula_id + "','" + pelicula_url + "','" + poster_url + "','" + pelicula_nombre.replaceAll("'", "''") + "','', '', '" + fecha + "','" + pelicula_datos1.replaceAll("'", "''") + "','" + pelicula_datos2.replaceAll("'", "''")+"')";

        try {
            st = conn.createStatement();
            //rs = st.executeQuery(query);
            st.executeUpdate(query);

            System.out.println("Datos añadidos a la base de datos!");


        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public String getRating() {

        //Método para extraer los "ratings" de cada pelicula guardada en la tabla de 'records' utilizando el API de OMDB

        String query = "SELECT `pelicula_nombre` FROM records";
        String xnombre = null;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                xnombre = rs.getString("pelicula_nombre");
                OmdbVideoFull result = null;

                try {
                    result = omdb.getInfo(new OmdbBuilder()
                            .setTitle(xnombre)
                            .setTomatoesOn()
                            .setTomatoes(true)
                            .build());

                } catch (Exception e) {
                    result = omdb.getInfo(new OmdbBuilder()
                            .setImdbId("tt4458206") //Le puse el ID de una pelicula de bajo rating ya que no aparece por algun error, etc.
                            .setTomatoesOn()
                            .setTomatoes(true)
                            .build());
                }

                String rating = result.getImdbRating();
                insertRating(xnombre,rating); //Guardamos la informacion a la base de datos en la tabla de "rating"

            }

        } catch(Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    public void insertRating(String xnombre,String rating) throws SQLException {

        //Método para insertar los "ratings" obtenidos del API de OMDB en la tabla de 'rating'

        st = conn.createStatement();
        String queryRating = "INSERT INTO `rating`(`pelicula_nombre`, `pelicula_rating`) "
                + "VALUES ('"+xnombre.replaceAll("'", "''")+"','"+rating+"')";
        st.executeUpdate(queryRating);
    }

    public void updateRating(){

        //Método para actualizar columna de Rating en la tabla de 'Records'
        String query = "UPDATE `rating`, `records` SET records.rating = rating.pelicula_rating WHERE rating.pelicula_nombre = records.pelicula_nombre ";

        try {
            st = conn.createStatement();
            st.executeUpdate(query);

            System.out.println("Los 'ratings' han sido actualizados en la tabla principal!");


        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void updateResumen() throws SQLException {

        //Método para actualizar columna de Resumen en la tabla de 'Records'

        String query1 = "UPDATE `records` SET `pelicula_resumen` = `pelicula_datos1` WHERE `pelicula_datos1` LIKE '%MOVIE SYNOPSIS%'";
        String query2 = "UPDATE `records` SET `pelicula_resumen` = `pelicula_datos2` WHERE `pelicula_datos2` LIKE '%MOVIE SYNOPSIS%'";

        //*************** PRIMERO (pelicula_datos1)
        try {
            st = conn.createStatement();
            st.executeUpdate(query1);
            System.out.println("La columna 'pelicula_resumen' ha sido actualizada con la columna `pelicula_datos1'.");
        } catch(Exception ex){
            System.out.println(ex);
        }

        //*************** SEGUNDO (pelicula_datos2)

        try {
            st = conn.createStatement();
            st.executeUpdate(query2);
            System.out.println("La columna 'pelicula_resumen' ha sido actualizada con la columna `pelicula_datos2'.");
        } catch(Exception ex){
            System.out.println(ex);
        }
    }

    public void getData(){

        //Método para extraer los datos de la base de datos

        String query = "SELECT * FROM records";
        //String query2 = "SELECT * FROM `records` WHERE `pelicula_datos1` LIKE '%MOVIE SYNOPSIS%' OR `pelicula_datos2` LIKE '%MOVIE SYNOPSIS%'";
        //System.out.println("Records from Database: " + "\n" + query2);

        try {
            //st = conn.prepareStatement(query);
            st = conn.createStatement();
            rs = st.executeQuery(query);
            //System.out.println("Records from Database: (rs)" + "\n");// + rs);
            while(rs.next()){
                String xid = rs.getString("pelicula_id");
                String xnombre = rs.getString("pelicula_nombre");
                String xurl = rs.getString("pelicula_url");
                String xposter = rs.getString("pelicula_poster");
                String xresumen = rs.getString("pelicula_resumen");
                String xfecha = rs.getString("fecha");
                String xdatos1 = rs.getString("pelicula_datos1");
                String xdatos2 = rs.getString("pelicula_datos2");
                //System.out.println("Nombre: " + xnombre + "\n" + "ID: " + xid + "\n" + "Random: " + xrandom + "\n" + "Poster: " + xposter + "\n" + "Datos: " + xdatos1 + "\n" + "Datos2: " + xdatos2);
            }
        } catch(Exception ex){
            System.out.println(ex);
        }


    }

    public int getDbSize() throws SQLException {

        //Método que se iba a utilizar para saber cuantas películas hay en el sistema

        String query = "SELECT COUNT(*) as cant FROM records";
        st = conn.createStatement();
        rs = st.executeQuery(query);
        rs.next();
        int count = rs.getInt("cant");
        //System.out.println("Records from Database: (rs)" + "\n" + count);

        return count;
    }

    public void deleteData(){

        //Método para eliminar todos los datos de las tablas

        String query = "TRUNCATE TABLE records";
        String query2 = "TRUNCATE TABLE rating";

        try {
            st = conn.createStatement();
            st.executeUpdate(query);
            st.executeUpdate(query2);

            System.out.println("Todos los datos han sido eliminados!");


        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void cerrarConexion() {

        //Método para cerrar conexión a la base de datos

        try {
            System.out.println("Conexión con base de datos ha sido cerrada!");
            conn.close();
            st.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
