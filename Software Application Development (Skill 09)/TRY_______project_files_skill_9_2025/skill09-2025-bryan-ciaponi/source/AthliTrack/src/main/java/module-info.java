module ch.athlitrack.athlitrack {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    opens ch.athlitrack.athlitrack to javafx.fxml;
    exports ch.athlitrack.athlitrack;
}