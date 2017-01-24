import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ThirdWindow extends Stage{

    public ThirdWindow(Group root, int m, int n, InputData inputData, OutputData outputData) {
        setScene(new Scene(root));
        ScrollPane scrollPane = new ScrollPane();

        Label costLabel = new Label("Processing cost:");
        TextField cost [] = new TextField[m*n];
        for (int i = 0; i < cost.length; i++) {
            cost[i] = new TextField();
        }
        Button generateCost = new Button("Generate");
        generateCost.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent event) {
                inputData.setProcessingCost();
                for (int i = 0; i < m; i++){
                    for(int j = 0; j < n; j++) {
                        cost[i * n + j].setText(String.valueOf(inputData.getProcessingCost()[i][j]));
                    }
                }
            }
        });

        HBox costRows [] = new HBox[m];
        for (int i = 0; i < m; i++) {
            costRows[i] = new HBox();
            for (int j = 0; j < n; j++) {
                costRows[i].getChildren().add(cost[ i * n + j ]);
            }
        }
        VBox costTable = new VBox();
        costTable.setSpacing(5);
        costTable.getChildren().addAll(costRows);
        costTable.getChildren().add(generateCost);


        Label prepTimeLabel = new Label("Preparation time:");
        TextField preparationTime [] = new TextField[m*n];
        for (int i = 0; i < preparationTime.length; i++) {
            preparationTime[i] = new TextField();
        }
        Button generatePrepTime = new Button("Generate");
        generatePrepTime.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                inputData.setPreparationTime();
                for (int i = 0; i < m; i++){
                    for(int j = 0; j < n; j++) {
                        preparationTime[i * n +j].setText(String.valueOf(inputData.getPreparationTime()[i][j]));
                    }
                }
            }
        });

        HBox prepTimeRows [] = new HBox[m];
        for (int i = 0; i < m; i++) {
            prepTimeRows[i] = new HBox();
            for (int j = 0; j < n; j++) {
                prepTimeRows[i].getChildren().add(preparationTime[ i * n + j ]);
            }
        }
        VBox prepTimeTable = new VBox();
        prepTimeTable.setSpacing(5);
        prepTimeTable.getChildren().addAll(prepTimeRows);
        prepTimeTable.getChildren().add(generatePrepTime);


        Label procTimeLabel = new Label("Processing time:");
        TextField processingTime [] = new TextField[m*n];
        for (int i = 0; i < processingTime.length; i++) {
            processingTime[i] = new TextField();
        }
        Button generateProcTime = new Button("Generate");
        generateProcTime.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                inputData.setProcessingTime();
                for (int i = 0; i < m; i++){
                    for(int j = 0; j < n; j++) {
                        processingTime[i * n + j].setText(String.valueOf(inputData.getProcessingTime()[i][j]));
                    }
                }
                inputData.setTotalTime();
            }
        });


        HBox procTimeRows [] = new HBox[m];
        for (int i = 0; i < m; i++) {
            procTimeRows[i] = new HBox();
            for (int j = 0; j < n; j++) {
                procTimeRows[i].getChildren().add(processingTime[ i * n + j ]);
            }
        }
        VBox procTimeTable = new VBox();
        procTimeTable.setSpacing(5);
        procTimeTable.getChildren().addAll(procTimeRows);
        procTimeTable.getChildren().add(generateProcTime);


        Button okButton = new Button("Solve");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Main().main(inputData, outputData, m, n);
                close();
            }
        });

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.getChildren().addAll(costLabel, costTable, generateCost);
        vBox.getChildren().addAll(prepTimeLabel, prepTimeTable, generatePrepTime);
        vBox.getChildren().addAll(procTimeLabel, procTimeTable, generateProcTime);
        vBox.getChildren().add(okButton);

        scrollPane.setVmax(650);
        scrollPane.setContent(vBox);
        root.getChildren().add(scrollPane);
    }
}
