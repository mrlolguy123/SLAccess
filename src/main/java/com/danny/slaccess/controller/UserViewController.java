// I discovered SceneBuilder's View -> View Sample Controller Skeleton on the creation of this class,
// so the way that it typed up may or may not be different.

package com.danny.slaccess.controller;

import com.danny.slaccess.HelloApplication;
import com.danny.slaccess.db.Database;
import com.danny.slaccess.model.AccessModel;
import com.danny.slaccess.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class UserViewController {
    Database db = new Database();
    Connection con = db.connect();

    @FXML
    private TableView<UserModel> clientUserTable;
    @FXML
    private TableColumn<UserModel,Integer> infoIdColumn;
    @FXML
    private TableColumn<UserModel,String> infoNameColumn;
    @FXML
    private TableColumn<UserModel,String> infoTypeColumn;
    @FXML
    private TableColumn<UserModel,Boolean> infoAuthColumn;

    @FXML
    private TableView<AccessModel> clientAccessTable;
    @FXML
    private TableColumn<AccessModel,Integer> accessIdColumn;
    @FXML
    private TableColumn<AccessModel,String> accessInColumn;
    @FXML
    private TableColumn<AccessModel,String> accessOutColumn;

    @FXML
    private CheckBox InfoAuthCheckBox;
    @FXML
    private TextField InfoNameField;
    @FXML
    private TextField InfoTypeField;
    @FXML
    private TextField InfoUserField;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label UserTableMessage;
    @FXML
    private Button RefreshButton;
    @FXML
    private CheckBox filterIDCheckbox;
    @FXML
    private CheckBox filterDateCheckbox;
    @FXML
    private CheckBox filterTimeCheckbox;
    @FXML
    private TextField filterIDField;
    @FXML
    private DatePicker filterDateField;
    @FXML
    private TextField filterTimeField;
    @FXML
    private Label filterMessage;
    @FXML
    private RadioButton filterInButton;
    @FXML
    private RadioButton filterOutButton;

    private boolean checkID(String id){
        String user;

        if(id.isBlank()) {
            return false; }

        if(id.charAt(0) == '%' && id.charAt(1) == 'A') {
            user = id.substring(2,11);
            filterIDField.setText(user);
            InfoUserField.setText(user); //bandaid fix because checking for id in every other function requires a tiny little bit of code rework and its 1:48 am at 9/11/2023 and i want to go to sleep real badly.
        } else {
            user = id;
        }

        if(user.length() != 9) {
            return false;
        }

        return isNumeric(user);
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

    protected void refreshUserTable() throws SQLException {
        ObservableList<UserModel> UserModelList = FXCollections.observableArrayList();
        String query1 = "SELECT * FROM userTable ORDER BY name";
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(query1);

        while(rs.next())
        {
            Integer id = rs.getInt(1);
            String name = rs.getString(2);
            String type = rs.getString(3);
            boolean auth = rs.getBoolean(4);

            UserModelList.add(new UserModel(id, name, type, auth));
        }

        infoIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        infoNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        infoTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        infoAuthColumn.setCellValueFactory(new PropertyValueFactory<>("auth"));
        clientUserTable.setItems(UserModelList);
    }

    protected void refreshAccessTable() throws SQLException {
        RefreshButton.setText("Refresh Tables");
        ObservableList<AccessModel> AccessModelList = FXCollections.observableArrayList();
        String query1 = "SELECT * FROM accessTable ORDER BY inTime DESC";
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(query1);

        while(rs.next())
        {
            Integer id = rs.getInt(1);
            String in = rs.getString(2);
            String out = rs.getString(3);

            AccessModelList.add(new AccessModel(id,in,out));
        }

        accessIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        accessInColumn.setCellValueFactory(new PropertyValueFactory<>("in"));
        accessOutColumn.setCellValueFactory(new PropertyValueFactory<>("out"));
        clientAccessTable.setItems(AccessModelList);
    }
    @FXML
    protected void ApplyButtonClick() throws SQLException {
        String update = "UPDATE userTable SET auth = ? WHERE numID = ?;";
        String query = "SELECT * FROM userTable WHERE numID = ?";
        filterIDField.setText(""); // see bandaid fix checkid()

        if(checkID(InfoUserField.getText())) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(InfoUserField.getText()));
            ResultSet rs = ps.executeQuery();
            boolean check = rs.getBoolean(4);

            if (rs.getInt(1) == Integer.parseInt(InfoUserField.getText())) {
                ps = con.prepareStatement(update);
                ps.setBoolean(1, !check);
                ps.setInt(2, Integer.parseInt(InfoUserField.getText()));
                ps.executeUpdate();
                UserTableMessage.setText("Authorization was toggled.");
                InfoIDButtonClick();
                refreshUserTable();
                return;
            }
        }

        UserTableMessage.setText("Toggle Authorization Error\nInvalid User ID");
        InfoNameField.setText("");
        InfoTypeField.setText("");
        InfoAuthCheckBox.setSelected(false);
    }

    @FXML
    protected void InfoIDButtonClick() throws SQLException {
        if(checkID(InfoUserField.getText()))
        {
            String query = "SELECT * FROM userTable WHERE numID = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,Integer.parseInt(InfoUserField.getText()));
            ResultSet rs = ps.executeQuery();

            if (rs.getInt(1) == Integer.parseInt(InfoUserField.getText())) {
                InfoNameField.setText(rs.getString(2));
                InfoTypeField.setText(rs.getString(3));
                InfoAuthCheckBox.setSelected(rs.getBoolean(4));
                filterIDField.setText(""); // see bandaid fix checkid()
                return;
            }
        }
        filterIDField.setText(""); // see bandaid fix checkid()
        UserTableMessage.setText("Search by User ID Error\nInvalid User ID");
    }

    @FXML
    protected void LogoutButtonClick() throws IOException, SQLException { // 'X' button closes the entire application, logout brings you back to log in screen
        con.close();
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.hide();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        stage.getIcons().add(new Image(String.valueOf(HelloApplication.class.getResource("icon.jpg"))));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void RefreshButtonClick() throws SQLException {
        refreshAccessTable();
        refreshUserTable();
    }

    @FXML
    protected void filterIDCheckboxClick()
    {
        if(!filterIDCheckbox.isSelected())
        {
            filterIDField.setText("");
            filterIDField.setDisable(true);
        }
        else {
            filterIDField.setText("");
            filterIDField.setDisable(false);
        }
    }

    @FXML
    protected void filterDateCheckboxClick()
    {
        if(!filterDateCheckbox.isSelected())
        {
            filterDateField.setValue(null);
            filterDateField.setDisable(true);
            filterTimeCheckbox.setSelected(false);
            filterTimeCheckbox.setDisable(true);
            filterTimeField.setText("");
            filterTimeField.setDisable(true);
        }
        else {
            filterDateField.setDisable(false);
            filterTimeCheckbox.setDisable(false);
        }
    }

    @FXML
    protected void filterTimeCheckboxClick()
    {
        if(!filterTimeCheckbox.isSelected())
        {
            filterTimeField.setText("");
            filterTimeField.setDisable(true);
        }
        else {
            filterTimeField.setText("");
            filterTimeField.setDisable(false);
        }
    }

    @FXML
    protected void filterRemoveButtonClick() throws SQLException
    {
        filterIDCheckbox.setSelected(false);
        filterIDField.setText("");
        filterIDField.setDisable(true);
        filterDateCheckbox.setSelected(false);
        filterDateField.setValue(null);
        filterDateField.setDisable(true);
        filterTimeCheckbox.setSelected(false);
        filterTimeField.setText("");
        filterTimeField.setDisable(true);
        refreshAccessTable();
    }

    @FXML
    protected void filterApplyButtonClick() throws SQLException {
        String queryID = "'%'"; // Used for 1
        String queryInOut = "'%'"; // Used for 2 and 4
        String queryDateTime = "'%'"; // Used for 3

        if(filterIDCheckbox.isSelected() && !(filterIDField.getText().isBlank())) {
            if(!checkID(filterIDField.getText())) {
                filterMessage.setText("Filter Error - User ID Invalid");
                return;
            }

            queryID = (filterIDField.getText());
        }

        if(filterDateCheckbox.isSelected() && filterDateField.getValue() != null) {
            if(filterInButton.isSelected()) { queryInOut = "inTime"; }
            else { queryInOut = "outTime"; }

            queryDateTime = "'" + filterDateField.getValue().toString() + " ";
        }

        if(filterTimeCheckbox.isSelected() && !(filterTimeField.getText().isBlank()))
        {
            String ftf = filterTimeField.getText();
            try {
                if (isNumeric(ftf.substring(0, 2)) && ftf.charAt(2) == ':' && isNumeric(ftf.substring(3, 5))) {
                    int a = Integer.parseInt(ftf.substring(0, 2)), b = Integer.parseInt(ftf.substring(3, 5));
                    if (a <= 24 && a >= 1 && b <= 60 && b >= 0) {
                        queryDateTime = queryDateTime + filterTimeField.getText() + "%'";
                    } else {
                        filterMessage.setText("Filter Time Error - Invalid Time");
                        return;
                    }
                }
            } catch (Exception e)
            {
                filterMessage.setText("Filter Time Error - Invalid Time");
                return;
            }
        }
        else if (!queryDateTime.equals("'%'")) {
            queryDateTime = queryDateTime + "%'";
        }

        System.out.println("SELECT * FROM accessTable WHERE numID LIKE " + queryID + " and "+queryInOut + " LIKE "+queryDateTime+" ORDER BY "+queryInOut+" DESC");
        InfoUserField.setText(""); // see bandaid fix in checkid()

        RefreshButton.setText("Refresh Tables");
        String query = "SELECT * FROM accessTable WHERE numID LIKE " + queryID + " and "+queryInOut + " LIKE "+queryDateTime+" ORDER BY "+queryInOut+" DESC";
        ObservableList<AccessModel> AccessModelList = FXCollections.observableArrayList();
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(query);

        while(rs.next())
        {
            Integer id = rs.getInt(1);
            String in = rs.getString(2);
            String out = rs.getString(3);

            AccessModelList.add(new AccessModel(id,in,out));
        }

        accessIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        accessInColumn.setCellValueFactory(new PropertyValueFactory<>("in"));
        accessOutColumn.setCellValueFactory(new PropertyValueFactory<>("out"));
        clientAccessTable.setItems(AccessModelList);
    }

    @FXML
    protected void filterInButtonClick()
    {
        if (filterInButton.isSelected())
        {
            filterOutButton.setSelected(false);
            filterInButton.setSelected(true);
            return;
        }
        filterInButton.setSelected(true);
    }

    @FXML
    protected void filterOutButtonClick()
    {
        if (filterOutButton.isSelected())
        {
            filterInButton.setSelected(false);
            filterOutButton.setSelected(true);
            return;
        }
        filterOutButton.setSelected(true);
    }





}