package main.java.sample;

import hr.java.covidportal.model.Zupanija;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DodavanjeNoveZupanijeController implements Initializable {

    @FXML
    private TextField nazivZupanije;
    @FXML
    private TextField brojStanovnika;
    @FXML
    private TextField brojZarazenih;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void dodajNovuZupaniju() {
        String naziv = nazivZupanije.getText();
        String stanovnici = brojStanovnika.getText();
        String zarazeni = brojZarazenih.getText();

        ObservableList<Zupanija> zup = Main.getObservableListZupanije();
        long id = zup.get(zup.size() - 1).getId() + 1;

        try {
            Main.getDatabase().spremiZupaniju(new Zupanija(id, naziv, Integer.parseInt(stanovnici), Integer.parseInt(zarazeni)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Main.ucitajZupanije();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spremanje podataka");
        alert.setHeaderText(null);
        alert.setContentText("Podatci o županiji uspješno spremljeni");

        alert.showAndWait();
    }
}
