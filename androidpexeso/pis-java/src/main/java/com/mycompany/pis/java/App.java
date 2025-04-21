package com.mycompany.pis.java;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class App extends Application {
    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private ObservableList<InventoryItem> inventory = FXCollections.observableArrayList();
    private ObservableList<Project> projects = FXCollections.observableArrayList();

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Podnikový informační systém");
        TabPane tabs = new TabPane(createEmployeeTab(), createInventoryTab(), createProjectTab());
        initializeSampleData();
        stage.setScene(new Scene(tabs, 800, 600));
        stage.show();
    }

    private void initializeSampleData() {
        employees.addAll(new Employee("Jan", "Novák", "Vývojář", "jan.novak@firma.cz", "777123456"),
                         new Employee("Marie", "Svobodová", "Manažerka", "marie.svobodova@firma.cz", "777654321"));
        inventory.addAll(new InventoryItem("Notebook", "IT-001", 15, "Sklad A", 25000),
                         new InventoryItem("Monitor 24\"", "IT-002", 8, "Sklad B", 5000));
        projects.addAll(new Project("Webová aplikace", "Vývoj", LocalDate.now().plusMonths(3)),
                        new Project("Mobilní aplikace", "Analýza", LocalDate.now().plusMonths(1)));
    }

    private Tab createEmployeeTab() {
        TableView<Employee> table = new TableView<>(employees);
        table.getColumns().addAll(column("Jméno", "firstName"), column("Příjmení", "lastName"), column("Pozice", "position"));

        TextField fn = new TextField(), ln = new TextField(), pos = new TextField(), em = new TextField(), ph = new TextField();
        GridPane form = form(new String[]{"Jméno", "Příjmení", "Pozice", "Email", "Telefon"}, fn, ln, pos, em, ph);

        Button add = new Button("Přidat"), del = new Button("Smazat"), att = new Button("Evidovat docházku");
        add.setOnAction(e -> { employees.add(new Employee(fn.getText(), ln.getText(), pos.getText(), em.getText(), ph.getText())); clearFields(fn, ln, pos, em, ph); });
        del.setOnAction(e -> employees.remove(table.getSelectionModel().getSelectedItem()));
        att.setOnAction(e -> showSelected("Docházka", table.getSelectionModel().getSelectedItem()));

        return tab("Zaměstnanci", new VBox(10, table, form, new HBox(10, add, del), att));
    }

    private Tab createInventoryTab() {
        TableView<InventoryItem> table = new TableView<>(inventory);
        table.getColumns().addAll(column("Název", "name"), column("Kód", "code"), column("Množství", "quantity"));

        TextField n = new TextField(), c = new TextField(), q = new TextField(), l = new TextField(), p = new TextField();
        GridPane form = form(new String[]{"Název", "Kód", "Množství", "Umístění", "Cena"}, n, c, q, l, p);

        Button add = new Button("Přidat"), del = new Button("Smazat"), check = new Button("Kontrola zásob");
        add.setOnAction(e -> {
            try {
                inventory.add(new InventoryItem(n.getText(), c.getText(), Integer.parseInt(q.getText()), l.getText(), Double.parseDouble(p.getText())));
                clearFields(n, c, q, l, p);
            } catch (NumberFormatException ex) { showAlert("Chyba", "Zadejte platné číslo"); }
        });
        del.setOnAction(e -> inventory.remove(table.getSelectionModel().getSelectedItem()));
        check.setOnAction(e -> showAlert("Stav zásob", "Nízké zásoby (" + inventory.stream().filter(i -> i.getQuantity() < 10).count() + " položek)"));

        return tab("Zásoby", new VBox(10, table, form, new HBox(10, add, del, check)));
    }

    private Tab createProjectTab() {
        TableView<Project> table = new TableView<>(projects);
        table.getColumns().addAll(column("Název", "name"), column("Stav", "status"), column("Termín", "deadline"));

        TextField n = new TextField();
        ComboBox<String> st = new ComboBox<>(FXCollections.observableArrayList("Plánování", "Probíhá", "Pozastaveno", "Dokončeno"));
        DatePicker dp = new DatePicker();
        GridPane form = form(new String[]{"Název", "Stav", "Termín"}, n, st, dp);

        Button add = new Button("Přidat"), del = new Button("Smazat"), assign = new Button("Přiřadit zaměstnance");
        add.setOnAction(e -> { projects.add(new Project(n.getText(), st.getValue(), dp.getValue())); clearFields(n); st.getSelectionModel().clearSelection(); dp.setValue(null); });
        del.setOnAction(e -> projects.remove(table.getSelectionModel().getSelectedItem()));
        assign.setOnAction(e -> showSelected("Přiřazení", table.getSelectionModel().getSelectedItem()));

        return tab("Projekty", new VBox(10, table, form, new HBox(10, add, del, assign)));
    }

    private <T> TableColumn<T, ?> column(String name, String prop) {
        TableColumn<T, Object> col = new TableColumn<>(name);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        return col;
    }

    private Tab tab(String name, VBox content) {
        content.setPadding(new Insets(10));
        Tab tab = new Tab(name, content);
        tab.setClosable(false);
        return tab;
    }

    private GridPane form(String[] labels, Control... fields) {
        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10); grid.setPadding(new Insets(10));
        for (int i = 0; i < labels.length; i++) grid.addRow(i, new Label(labels[i] + ":"), fields[i]);
        return grid;
    }

    private void clearFields(TextField... fields) { for (TextField f : fields) f.clear(); }
    private void clearFields(TextField f) { f.clear(); }

    private void showSelected(String title, Object obj) {
        if (obj != null) showAlert(title, obj instanceof Employee ? ((Employee) obj).getFullName() : ((Project) obj).getName());
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }

    public static class Employee {
        private String firstName, lastName, position, email, phone;
        public Employee(String fn, String ln, String p, String e, String ph) { firstName = fn; lastName = ln; position = p; email = e; phone = ph; }
        public String getFullName() { return firstName + " " + lastName; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPosition() { return position; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
    }

    public static class InventoryItem {
        private String name, code, location; private int quantity; private double price;
        public InventoryItem(String n, String c, int q, String l, double p) { name = n; code = c; quantity = q; location = l; price = p; }
        public String getName() { return name; }
        public String getCode() { return code; }
        public int getQuantity() { return quantity; }
        public String getLocation() { return location; }
        public double getPrice() { return price; }
    }

    public static class Project {
        private String name, status; private LocalDate deadline;
        public Project(String n, String s, LocalDate d) { name = n; status = s; deadline = d; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public LocalDate getDeadline() { return deadline; }
    }
}
