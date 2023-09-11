package com.danny.slaccess.controller;

import com.danny.slaccess.HelloApplication;
import com.danny.slaccess.db.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class HelloController {
    Database db = new Database();
    Connection con = db.connect();

    @FXML
    private Button LoginButton;
    @FXML
    private TextField userIDField;
    @FXML
    private CheckBox adminCheck;
    @FXML
    private PasswordField passField;
    @FXML
    private Label passLabel;
    @FXML
    private Label loginMessage;

    @FXML
    protected void loginButtonPressed () throws SQLException {
        String initial = userIDField.getText();
        String user = "";

        if(initial.isBlank()) {
            loginMessage.setText("Invalid Credentials.");

            return; }

        if(initial.charAt(0) == '%' && initial.charAt(1) == 'A') {
            user = initial.substring(2,11);
        } else {
            user = initial;
        }

        if(user.length() != 9) {
            loginMessage.setText("Invalid Credentials.");
            return;
        }

        if(!(isNumeric(user))) {
            loginMessage.setText("Invalid Credentials.");
            return;
        }

        if(adminCheck.isSelected()){
            String idCheck = "SELECT * FROM adminCreds WHERE numID=?";
            try{
                PreparedStatement ps = con.prepareStatement(idCheck);
                ps.setInt(1,Integer.parseInt(user));
                ResultSet rs = ps.executeQuery();
                if(rs.getInt(1) == Integer.parseInt(user) && rs.getString(2).equals(passField.getText())) {
                    con.close();
                    loginMessage.setText("You are now logged in.");
                    Stage stage = (Stage) passField.getScene().getWindow();
                    stage.hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
                    stage.getIcons().add(new Image(String.valueOf(HelloApplication.class.getResource("icon.jpg"))));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setScene(scene);
                    stage.show();
                }
                else {
                    loginMessage.setText("Invalid Credentials.");
                }
            } catch (SQLException | IOException e) {
                System.out.println(e);
            }

        }
        else {
            enterDB(Integer.parseInt(user));
        }
    }
    @FXML
    protected void adminCheckPressed()
    {
        if(passField.isVisible()) {
            passField.setVisible(false);
            passLabel.setVisible(false);
            passField.setText("");
            LoginButton.setText("Scan");

            return;
        }
        passField.setVisible(true);
        passLabel.setVisible(true);
        LoginButton.setText("Login");
    }

    private boolean isNumeric(String a) {
        try {
            int num = Integer.parseInt(a);
        } catch (NumberFormatException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    private void enterDB(int id) throws SQLException
    {
        String temp = LocalDateTime.now().toString();
        String now = temp.substring(0,10) + " " + temp.substring(11,19);
        System.out.println(temp);

        String queryCheckState = "SELECT * FROM accessTable WHERE numID=? AND outTime IS NULL";
        String queryCheckAuth = "SELECT * FROM userTable WHERE numID=?";
        String UpdateEntry1 = "INSERT INTO accessTable (numID, inTime, outTime) VALUES (?, ?, null);";
        String UpdateEntry2 = "UPDATE accessTable SET outTime = ? WHERE numID = ? AND outTime IS NULL;";

        PreparedStatement ps = con.prepareStatement(queryCheckState);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if(rs.getInt(1) == 0) { // Run if Student is outside of Sun Lab (when outTime is not null)
            ps = con.prepareStatement(queryCheckAuth);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.getInt(1) == id && rs.getBoolean(4)) {
                ps = con.prepareStatement(UpdateEntry1);
                ps.setInt(1, id);
                ps.setString(2, now);
                ps.executeUpdate();
                loginMessage.setText("Your entry to the Sun Lab has been recorded.");
            } else {
                loginMessage.setText("You are not currently authorized to enter. Contact an administrator for help.");
            }
        }
        else { // Run if student is currently in the lab (outTime IS NULL)
            ps = con.prepareStatement(UpdateEntry2);
            ps.setString(1, now);
            ps.setInt(2, id);
            ps.executeUpdate();
            loginMessage.setText("Your exit from the Sun Lab has been recorded.");
        }


    }
}