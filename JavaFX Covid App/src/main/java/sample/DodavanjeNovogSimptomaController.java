package main.java.sample;

import hr.java.covidportal.model.Simptom;
import hr.java.covidportal.model.Zupanija;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DodavanjeNovogSimptomaController implements Initializable {

    @FXML
    private TextField nazivSimptoma;

    @FXML
    private ComboBox vrijednosti;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vrijednosti.getItems().addAll("PRODUKTIVNO", "INTENZIVNO", "VISOKA", "JAKA");
    }

    public void dodajNoviSimptom() {
        String naziv = nazivSimptoma.getText();
        String vrijednost = vrijednosti.getValue().toString();

        ObservableList<Simptom> simp = Main.getObservableListSimptomi();
        long id = simp.get(simp.size() - 1).getId() + 1;

        try {
            Main.getDatabase().spremiSimptom(new Simptom(id, naziv, vrijednost));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Main.ucitajSimptome();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spremanje podataka");
        alert.setHeaderText(null);
        alert.setContentText("Podatci o simptomu uspješno spremljeni");

        alert.showAndWait();
    }
}
