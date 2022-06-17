package main.java.sample;

import hr.java.covidportal.model.Bolest;
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
import java.util.List;
import java.util.ResourceBundle;

public class PretragaVirusaController implements Initializable {

    @FXML
    private TextField nazivVirusa;

    @FXML
    private TableView<Bolest> tablicaVirusa;
    @FXML
    private TableColumn<Virus, String> stupacNazivaVirusa;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Main.ucitajViruse();
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stupacNazivaVirusa.setCellValueFactory(new PropertyValueFactory<Virus, String>("naziv"));

        tablicaVirusa.setItems(FXCollections.observableList(Main.getObservableListVirusi()));
    }

    public void pretragaVirusa() {
        String naziv = nazivVirusa.getText();

        if(naziv == null || naziv.equals("") || naziv.equals(" ")) {
            tablicaVirusa.setItems(Main.getObservableListVirusi());
            return;
        }

        ObservableList<Bolest> vir = Main.getObservableListVirusi();
        List<Bolest> filtriraniVirusi = new ArrayList<>();

        for(Bolest v : vir) {
            if(v.getNaziv().toLowerCase().contains((naziv.toLowerCase()))) {
                filtriraniVirusi.add(v);
            }
        }

        tablicaVirusa.setItems(FXCollections.observableList(filtriraniVirusi));
    }
}
