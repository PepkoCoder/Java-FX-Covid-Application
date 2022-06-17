package main.java.sample;

import hr.java.covidportal.model.*;
import javafx.collections.FXCollections;
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
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class DodavanjeNoveOsobeController implements Initializable {

    @FXML
    private TextField imeOsobe;
    @FXML
    private TextField prezimeOsobe;
    @FXML
    private TextField dan;
    @FXML
    private TextField mjesec;
    @FXML
    private TextField godina;

    @FXML
    private ListView zupanija;
    @FXML
    private ListView bolest;
    @FXML
    private ListView kontakti;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        zupanija.setItems(Main.getObservableListZupanije());

        ObservableList<Bolest> bolesti = FXCollections.observableArrayList();
        bolesti.addAll(Main.getObservableListBolesti());
        bolesti.addAll(Main.getObservableListVirusi());
        bolest.setItems(bolesti);

        kontakti.setItems(Main.getObservableListOsobe());
        kontakti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void dodajNovuOsobu() {
        String ime = imeOsobe.getText();
        String prezime = prezimeOsobe.getText();

        LocalDate datumRodjenja = LocalDate.of(Integer.parseInt(godina.getText()), Integer.parseInt(mjesec.getText()), Integer.parseInt(dan.getText()));

        Zupanija zup = (Zupanija) zupanija.getSelectionModel().getSelectedItem();
        Bolest bol = (Bolest) bolest.getSelectionModel().getSelectedItem();

        ObservableList<Osoba> kon = kontakti.getSelectionModel().getSelectedItems();

        try {
            Main.getDatabase().spremiOsobu(new Osoba.Builder(1).imena(ime, prezime).godine(datumRodjenja).uZupaniji(zup)
                                                    .zarazenSa(bol).uKontaktuSa(kon).build());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spremanje podataka");
        alert.setHeaderText(null);
        alert.setContentText("Podatci o osobi uspješno spremljeni");

        alert.showAndWait();
    }
}
