package main.java.sample;

import hr.java.covidportal.model.Zupanija;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

public class PretragaZupanijaController implements Initializable {

    @FXML
    private TextField nazivZupanije;

    @FXML
    private TableView<Zupanija> tablicaZupanija;
    @FXML
    private TableColumn<Zupanija, String> stupacNazivaZupanija;
    @FXML
    private TableColumn<Zupanija, String> stupacBrojaStanovnikaZupanija;
    @FXML
    private TableColumn<Zupanija, String> stupacBrojaZarazenihStanovnikaZupanija;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Main.ucitajZupanije();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stupacNazivaZupanija.setCellValueFactory(new PropertyValueFactory<Zupanija, String>("naziv"));
        stupacBrojaStanovnikaZupanija.setCellValueFactory(new PropertyValueFactory<Zupanija, String>("brojStanovnika"));
        stupacBrojaZarazenihStanovnikaZupanija.setCellValueFactory(new PropertyValueFactory<Zupanija, String>("zarazeneOsobe"));

        tablicaZupanija.setItems(FXCollections.observableList(Main.getObservableListZupanije()));
    }

    public void pretragaZupanija() {
        String naziv = nazivZupanije.getText();

        if(naziv == null || naziv.equals("") || naziv.equals(" ")) {
            tablicaZupanija.setItems(Main.getObservableListZupanije());
            return;
        }

        ObservableList<Zupanija> zup = Main.getObservableListZupanije();
        List<Zupanija> filtriraneZupanije = new ArrayList<>();

        for(Zupanija z : zup) {
            if(z.getNaziv().toLowerCase().contains((naziv.toLowerCase()))) {
                filtriraneZupanije.add(z);
            }
        }

        tablicaZupanija.setItems(FXCollections.observableList(filtriraneZupanije));
    }
}
