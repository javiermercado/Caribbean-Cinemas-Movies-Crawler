Caribbean Cinemas Movies Crawler
================================

Descripción:
- Extraer información de películas en cartelera de Caribbean Cinemas, guardar toda la información en una base de datos (SQL) y extraer el "rating" de estas películas utilizando un API de [OMDB](http://omdbapi.com/) creado por [Omertron](https://github.com/Omertron/api-omdb/).

Futuras actualizaciones:
- Añadir un sistema de "calendarización", ya sea [cron4j](http://www.sauronsoftware.it/projects/cron4j/) o [Quartz](http://www.quartz-scheduler.org/), para que el programa se ejecute 1 o 2 días por semana.
- Crear un website para mostrar las mejores 3-5 películas de la semana, utilizando el rating obtenido de OMDB. (utilizando JSP o PHP)

Proyecto personal creado por: [Javier Mercado](https://github.com/javiermercado)

Librerías utilizadas:
[Gson](https://github.com/google/gson), [Jsoup](https://jsoup.org/download), [log4j](https://logging.apache.org/log4j/1.2/index.html), [slf4j](http://www.slf4j.org/)

