package main.java.sample;

import hr.java.covidportal.model.Bolest;
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

public class PretragaBolestiController implements Initializable {

    @FXML
    private TextField nazivBolesti;

    @FXML
    private TableView<Bolest> tablicaBolesti;
    @FXML
    private TableColumn<Bolest, String> stupacNazivaBolesti;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Main.ucitajBolesti();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stupacNazivaBolesti.setCellValueFactory(new PropertyValueFactory<Bolest, String>("naziv"));

        tablicaBolesti.setItems(FXCollections.observableList(Main.getObservableListBolesti()));
    }

    public void pretragaBolesti() {
        String naziv = nazivBolesti.getText();

        if(naziv == null || naziv.equals("") || naziv.equals(" ")) {
            tablicaBolesti.setItems(Main.getObservableListBolesti());
            return;
        }

        ObservableList<Bolest> bol = Main.getObservableListBolesti();
        List<Bolest> filtriraneBolesti = new ArrayList<>();

        for(Bolest b : bol) {
            if(b.getNaziv().toLowerCase().contains((naziv.toLowerCase()))) {
                filtriraneBolesti.add(b);
            }
        }

        tablicaBolesti.setItems(FXCollections.observableList(filtriraneBolesti));
    }
}
