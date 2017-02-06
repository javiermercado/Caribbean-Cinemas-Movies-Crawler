package com.JavierMercado;


import com.omertron.omdbapi.OMDBException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;




public class Main{

    static ConnectDB db = new ConnectDB();

    public static void main(String[] args) throws IOException, SQLException, OMDBException {


        db.deleteData(); //eliminar todo los datos para solo mantener la informacion de la ultima actualizacion
        extraerInfo(); //extrae informacion y la añade a la tabla de 'records' gracias al "insertData()" dentro de extraerInfo()
        db.updateResumen(); //actualiza columna de resumen en tabla de 'records'
        db.getRating(); //utiliza la data de 'records' para buscar los ratings con el API de OMDB y los añade a una nueva tabla llamada 'rating'
        db.updateRating(); // actualiza la tabla de 'records' con los datos obtenidos de la tabla de 'rating'

    }

    public static String extraerInfo() throws IOException, SQLException, OMDBException {

        //Posters de la pelicula
        Document home = Jsoup.connect("http://caribbeancinemas.com/es/now-showing/").timeout(10 * 1000).get();
        Elements pos = home.select("div.column.three-fourth.column_column");

        for (Element element : pos.select("div.one-fourth.column")) {

            //Obtiene URL de la pelicula
            String pelicula_url = element.select("div.one-fourth.column a").attr("abs:href");
            //System.out.println(pelicula_url);

            //Obtiene URL de las imagenes (posters)
            String poster_url = element.select("div.one-fourth.column img").attr("src");
            //System.out.println("http://caribbeancinemas.com" + poster_url);

            //Separar el url en partes para obtener ID
            String string = pelicula_url;
            String[] parts = string.split("/");
//            String part1 = parts[0];
//            String part2 = parts[1];
//            String part3 = parts[2];
//            String part4 = parts[3];
//            String part5 = parts[4];
            String part6 = parts[5]; //ID
            String pelicula_id = part6;

            //Titulo de la pelicula con el URL que obtenemos del primer query
            Document titulo = Jsoup.connect(pelicula_url).timeout(10 * 1000).get();
            Elements tit = titulo.select("div#Subheader"); //titulo
            Elements cont = titulo.select("div.column.two-third"); //contenido

            String pelicula_nombre = "";
            for (Element element1 : tit.select("div.column.one")){

                pelicula_nombre = element1.text().toString();
               // System.out.println("Titulo: " + pelicula_nombre);

            }

            //Contenido (Synopsis)
            String pelicula_datos1 = "";
            String pelicula_datos2 = "";

            for (Element ignored : cont) {

                Elements bTags = titulo.select("div.column.two-third > div b");

                if (bTags.size() > 8) {
                    //En algunas peliculas, el sinopsis está en el tag #7 o el #8, por lo tanto hay que guardar los 2 datos y despues usar el necesario

                    Element label = bTags.get(7); //Escritor o Movie Synopsis
                    Element label2 = bTags.get(8); //Escritor o Movie Synopsis
                    String resumen = label.parent().childNode(label.siblingIndex() + 1).toString();
                    String resumen2 = label2.parent().childNode(label2.siblingIndex() + 1).toString();
                    //System.out.println(label.text() + "\n" + resumen);
                    //System.out.println(label2.text() + "\n" + resumen2);

                    pelicula_datos1 = label.text() + "\n" + resumen;
                    pelicula_datos2 = label2.text() + "\n" + resumen2;
                }
            }

            // ****** FECHA De cuando se ejecuta el programa *****
            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            java.sql.Timestamp fecha = new java.sql.Timestamp(now.getTime());


            // Añadir la informacion extraida del website a la base de datos
            db.insertData(pelicula_id, pelicula_url, poster_url, pelicula_nombre, "resumen","", fecha.toString(), pelicula_datos1, pelicula_datos2);

        }
        return "";
    }


}

