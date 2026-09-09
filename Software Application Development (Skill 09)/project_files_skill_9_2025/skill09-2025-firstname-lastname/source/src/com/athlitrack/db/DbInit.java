package com.athlitrack.db;

/**
 * Avvia solo l'inizializzazione del database.
 * Usato dallo script di build per creare il file database/athlitrack.db
 * senza dover avviare l'interfaccia grafica.
 */
public class DbInit {

    public static void main(String[] args) throws Exception {
        Database.inizializza();
        System.out.println("Database inizializzato correttamente.");
    }
}