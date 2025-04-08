package com.example.aplicacionweb;

import java.util.Base64;

public class utilidades {   static String url_consulta = "http://192.168.1.16:5984/agenda/_design/agenda/_view/agenda";
    static String url_mto = "http://192.168.1.16:5984/agenda";
    static String user = "admin";
    static String passwd = "hola123";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user + ":" + passwd).getBytes());
    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}
