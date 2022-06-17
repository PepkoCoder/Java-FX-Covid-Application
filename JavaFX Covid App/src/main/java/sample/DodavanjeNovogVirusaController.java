package main.java.sample;

import hr.java.covidportal.model.Bolest;
import hr.java.covidportal.model.Simptom;
import hr.java.covidportal.model.Virus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DodavanjeNovogVirusaController implements Initializable {

    @FXML
    private TextField nazivVirusa;

    @FXML
    private ListView simptomi;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        simptomi.setItems(Main.getObservableListSimptomi());
        simptomi.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void dodajNoviVirus() {
        String naziv = nazivVirusa.getText();
        ObservableList<Simptom> simp = simptomi.getSelectionModel().getSelectedItems();

        try {
            Main.getDatabase().spremiBolest(new Virus(1, naziv, simp), true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spremanje podataka");
        alert.setHeaderText(null);
        alert.setContentText("Podatci o virusu uspješno spremljeni");

        alert.showAndWait();
    }
}
