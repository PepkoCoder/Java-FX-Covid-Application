package main.java.sample;

import hr.java.covidportal.model.Bolest;
import hr.java.covidportal.model.Osoba;
import hr.java.covidportal.model.Virus;
import hr.java.covidportal.model.Zupanija;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PretragaOsobaController implements Initializable {

    @FXML
    private TextField imeOsobe, prezimeOsobe;

    @FXML
    private TableView<Osoba> tablicaOsoba;
    @FXML
    private TableColumn<Osoba, String> stupacImenaOsobe;
    @FXML
    private TableColumn<Osoba, String> stupacPrezimenaOsobe;
    @FXML
    private TableColumn<Osoba, String> stupacStarostiOsobe;
    @FXML
    private TableColumn<Osoba, String> stupacZupanijeOsobe;
    @FXML
    private TableColumn<Osoba, String> stupacBolestiOsobe;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Main.ucitajOsobe();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stupacImenaOsobe.setCellValueFactory(new PropertyValueFactory<Osoba, String>("ime"));
        stupacPrezimenaOsobe.setCellValueFactory(new PropertyValueFactory<Osoba, String>("prezime"));
        stupacStarostiOsobe.setCellValueFactory(new PropertyValueFactory<Osoba, String>("starost"));
        stupacZupanijeOsobe.setCellValueFactory(new PropertyValueFactory<Osoba, String>("zupanija"));
        stupacBolestiOsobe.setCellValueFactory(new PropertyValueFactory<Osoba, String>("zarazenBolescu"));

        tablicaOsoba.setItems(FXCollections.observableList(Main.getObservableListOsobe()));
    }

    public void pretragaOsoba() {
        ObservableList<Osoba> osobe = Main.getObservableListOsobe();

        List<Osoba> filtriranaListaOsoba = osobe.stream().filter(o -> o.getIme().toLowerCase().contains(imeOsobe.getText().toLowerCase()))
                .filter(o -> o.getPrezime().toLowerCase().contains(prezimeOsobe.getText().toLowerCase()))
                .collect(Collectors.toList());

        tablicaOsoba.setItems(FXCollections.observableList(filtriranaListaOsoba));
    }
}
