package athlitrack;

/**
 * Errore del database senza "checked exception": così il codice dei
 * controller resta leggibile (niente try/catch ovunque) e gli errori
 * vengono mostrati all'utente con un dialogo invece di far crashare l'app.
 */
public class DbException extends RuntimeException {
    public DbException(String message) { super(message); }
    public DbException(String message, Throwable cause) { super(message, cause); }
}
