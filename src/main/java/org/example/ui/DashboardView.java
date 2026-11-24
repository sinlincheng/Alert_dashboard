package org.example.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class DashboardView extends BorderPane {

    private final Label iocLabel = new Label("IOC: -");
    private final Label vtLabel = new Label("VT score: -");

    public DashboardView() {
        getStyleClass().add("app-root");
        setPadding(new Insets(24));

        TableView<AlertRow> table = buildTable();
        VBox details = buildDetailsPanel();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            iocLabel.setText(newRow == null ? "IOC: -" : "IOC: " + newRow.sourceIp);
            vtLabel.setText(newRow == null ? "VT score: -" : "VT score: " + newRow.vt());
        });
        table.getSelectionModel().selectFirst();

        HBox body = new HBox(12, table, details);
        HBox.setHgrow(table, Priority.ALWAYS);

        VBox content = new VBox(12, buildCards(), buildFilters(), body);
        VBox.setVgrow(body, Priority.ALWAYS);

        HBox panel = new HBox(14, buildSidebar(), content);
        panel.getStyleClass().add("main-panel");
        panel.setPadding(new Insets(14));
        HBox.setHgrow(content, Priority.ALWAYS);

        setCenter(panel);
    }

    // ---------- 1. Sidebar ----------
    private VBox buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(130);
        sidebar.setMinWidth(130);
        sidebar.setPadding(new Insets(4, 0, 0, 0));

        Button dashboard = navButton("Dashboard");
        dashboard.getStyleClass().add("nav-active");

        sidebar.getChildren().addAll(dashboard, navButton("Import logs"), navButton("Alerts"),
                navButton("IOC lookup"), navButton("Export"));
        return sidebar;
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("nav-button");
        return button;
    }

    // ---------- 2. Summary cards ----------
    private HBox buildCards() {
        return new HBox(12,
                card("Total alerts", 24, "card-total"),
                card("High", 5, "card-high"),
                card("Medium", 9, "card-medium"),
                card("Low", 10, "card-low"));
    }

    private VBox card(String title, int count, String styleClass) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        Label countLabel = new Label(String.valueOf(count));
        countLabel.getStyleClass().add("card-count");

        VBox card = new VBox(4, titleLabel, countLabel);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(70);
        card.getStyleClass().addAll("card", styleClass);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // ---------- 3. Search and filters ----------
    private HBox buildFilters() {
        TextField search = new TextField();
        search.setPromptText("Search IP or IOC");
        search.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(search, Priority.ALWAYS);

        ComboBox<String> priority = new ComboBox<>();
        priority.setPromptText("Priority");
        priority.getItems().addAll("All", "High", "Medium", "Low");
        priority.setPrefWidth(160);

        ComboBox<String> status = new ComboBox<>();
        status.setPromptText("Status");
        status.getItems().addAll("All", "New", "In review", "Closed", "Escalated");
        status.setPrefWidth(160);

        return new HBox(12, search, priority, status);
    }

    // ---------- 4 and 5. Alert table ----------
    private TableView<AlertRow> buildTable() {
        TableView<AlertRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(40);
        table.setPlaceholder(new Label("No alerts imported"));

        TableColumn<AlertRow, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().time));

        TableColumn<AlertRow, String> ipCol = new TableColumn<>("Source IP");
        ipCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().sourceIp));

        TableColumn<AlertRow, String> typeCol = new TableColumn<>("Alert type");
        typeCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().type));

        TableColumn<AlertRow, String> vtCol = new TableColumn<>("VT");
        vtCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().vt()));

        TableColumn<AlertRow, Level> levelCol = new TableColumn<>("Priority");
        levelCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().level));
        levelCol.setCellFactory(col -> new LevelCell());

        table.getColumns().addAll(timeCol, ipCol, typeCol, vtCol, levelCol);

        return table;
    }

    // ---------- 6 and 7. Details panel and action buttons ----------
    private VBox buildDetailsPanel() {
        VBox panel = new VBox(8);
        panel.setPrefWidth(230);
        panel.setMinWidth(230);
        panel.setPadding(new Insets(12));
        panel.getStyleClass().add("details-panel");

        Label title = new Label("Alert details");
        title.getStyleClass().add("details-title");

        StackPane rawLog = new StackPane(new Label("Raw log excerpt"));
        rawLog.setPrefHeight(80);
        rawLog.getStyleClass().add("raw-log");

        Button truePositive = actionButton("True positive");
        Button falsePositive = actionButton("False positive");
        Button escalate = actionButton("Escalate to L2");
        escalate.getStyleClass().add("escalate-button");

        panel.getChildren().addAll(title, iocLabel, vtLabel, rawLog, truePositive, falsePositive, escalate);
        return panel;
    }

    private Button actionButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("action-button");
        return button;
    }

    // ---------- Temporary helpers (swap for your Alert and Severity classes) ----------
    private enum Level {
        HIGH("High", "dot-red"),
        MEDIUM("Medium", "dot-yellow"),
        LOW("Low", "dot-green");

        final String label;
        final String dotClass;

        Level(String label, String dotClass) {
            this.label = label;
            this.dotClass = dotClass;
        }
    }

    private static class AlertRow {
        final String time, sourceIp, type;
        final int vtScore, vtTotal;
        final Level level;

        AlertRow(String time, String sourceIp, String type, int vtScore, int vtTotal, Level level) {
            this.time = time;
            this.sourceIp = sourceIp;
            this.type = type;
            this.vtScore = vtScore;
            this.vtTotal = vtTotal;
            this.level = level;
        }

        String vt() {
            return vtScore + "/" + vtTotal;
        }
    }

    /** Coloured dot plus text label, so colour is never the only signal. */
    private static class LevelCell extends TableCell<AlertRow, Level> {
        @Override
        protected void updateItem(Level item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            Circle dot = new Circle(5);
            dot.getStyleClass().addAll("priority-dot", item.dotClass);
            HBox box = new HBox(8, dot, new Label(item.label));
            box.setAlignment(Pos.CENTER_LEFT);
            setGraphic(box);
        }
    }
}