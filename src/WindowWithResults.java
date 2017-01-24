import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WindowWithResults extends Stage{

    public WindowWithResults(Group root, OutputData outputData, int m, int n) {
        setScene(new Scene(root));

        Label labels[] = new Label[m];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label("Machine " + (i+1) + ": ");
        }

        TextField textFields[][] = new TextField[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                textFields[i][j] = new TextField();
                textFields[i][j].setEditable(false);
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                textFields[i][j].setText(String.valueOf(outputData.getSolution()[i][j]));
            }
        }

        TextArea textArea = new TextArea();
        textArea.setScrollLeft(30);
        textArea.setVisible(false);


        Button solutionButton = new Button("Full solution");
        solutionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.setVisible(true);
            }
        });

        HBox boxRows [] = new HBox[m];
        for (int i = 0; i < m; i++) {
            boxRows[i] = new HBox();
        }
        for (int i = 0; i < m; i++) {
            boxRows[i].getChildren().add(labels[i]);
            for (int j = 0; j < n; j++) {
                boxRows[i].getChildren().add(textFields[i][j]);
            }
        }

        VBox vBox = new VBox();
        vBox.getChildren().addAll(boxRows);
        vBox.getChildren().add(solutionButton);
        vBox.getChildren().add(textArea);

        root.getChildren().add(vBox);
    }

    void drawWindow (Table table){
        final TableView tableView = new TableView();
        String columns[] = table.castToReadableView(table.indexOfVariablesCorrespondingColumns);
        String rows[] = table.castToReadableView(table.indexOfVariablesCorrespondingRows);

        TableColumn tableColumns[] = new TableColumn[table.indexOfVariablesCorrespondingColumns.length];
        TableRow tableRows[] = new TableRow[table.indexOfVariablesCorrespondingRows.length];

        for (int i = 0; i < table.indexOfVariablesCorrespondingColumns.length; i++) {
            tableColumns[i].setText(columns[i]);
        }

        for (int i = 0; i < table.indexOfVariablesCorrespondingRows.length; i++) {
            tableRows[i].setText(rows[i]);
        }

        tableView.getColumns().addAll(tableColumns);

    }
}
