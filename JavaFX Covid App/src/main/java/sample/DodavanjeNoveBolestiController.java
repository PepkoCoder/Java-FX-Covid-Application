package main.java.sample;

import hr.java.covidportal.model.Bolest;
import hr.java.covidportal.model.Simptom;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DodavanjeNoveBolestiController implements Initializable {

    @FXML
    private TextField nazivBolesti;

    @FXML
    private ListView simptomi;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        simptomi.setItems(Main.getObservableListSimptomi());
        simptomi.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void dodajNovuBolest() {
        String naziv = nazivBolesti.getText();
        ObservableList<Simptom> simp = simptomi.getSelectionModel().getSelectedItems();

        ObservableList<Bolest> bol = Main.getObservableListBolesti();
        bol.addAll(Main.getObservableListVirusi());
        long id = (bol != null && bol.size() > 0) ? bol.size() + 1 : 1;

        try {
            Main.getDatabase().spremiBolest(new Bolest(id, naziv, simp), false);
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
        alert.setContentText("Podatci o bolesti uspješno spremljeni");

        alert.showAndWait();
    }
}
