package main.java.sample;

import hr.java.covidportal.model.Simptom;
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

public class PretragaSimptomaController implements Initializable {

    @FXML
    private TextField nazivSimptoma;

    @FXML
    private TableView<Simptom> tablicaSimptoma;
    @FXML
    private TableColumn<Simptom, String> stupacNazivaSimptoma;
    @FXML
    private TableColumn<Simptom, String> stupacVrijednostiSimptoma;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Main.ucitajSimptome();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stupacNazivaSimptoma.setCellValueFactory(new PropertyValueFactory<Simptom, String>("naziv"));
        stupacVrijednostiSimptoma.setCellValueFactory(new PropertyValueFactory<Simptom, String>("vrijednost"));

        tablicaSimptoma.setItems(FXCollections.observableList(Main.getObservableListSimptomi()));
    }

    public void pretragaSimptoma() {
        String naziv = nazivSimptoma.getText();

        if(naziv == null || naziv.equals("") || naziv.equals(" ")) {
            tablicaSimptoma.setItems(Main.getObservableListSimptomi());
            return;
        }

        ObservableList<Simptom> simp = Main.getObservableListSimptomi();
        List<Simptom> filtriraniSimptomi = new ArrayList<>();

        for(Simptom s : simp) {
            if(s.getNaziv().toLowerCase().contains((naziv.toLowerCase()))) {
                filtriraniSimptomi.add(s);
            }
        }

        tablicaSimptoma.setItems(FXCollections.observableList(filtriraniSimptomi));
    }
}
