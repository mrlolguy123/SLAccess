module com.danny.slaccess {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.danny.slaccess to javafx.fxml;
    exports com.danny.slaccess;
    exports com.danny.slaccess.controller;
    opens com.danny.slaccess.controller to javafx.fxml;
    opens com.danny.slaccess.model to javafx.base; // THIS GUY WAS THE SOURCE OF ALL MY PROBLEMS ON ONE SINGULAR DAY!!!!!!!!!!!!!!!!
}