import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartWindow extends Application {

    Stage stage;
    int m;
    int n;

    @Override
    public void start(Stage primaryStage) throws Exception {
        InputData inputData = new InputData();
        OutputData outputData = new OutputData();
        init (primaryStage, inputData, outputData);
        primaryStage.show();
    }

    private void init(Stage primaryStage, InputData inputData, OutputData outputData) {
        Group root = new Group();
        primaryStage.setTitle("Simplex");
        primaryStage.setScene(new Scene(root));

        VBox vBox = new VBox();
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();

        Label labelMachine = new Label("Number of machines:");
        Label labelDetails = new Label("Number of details:");

        TextField numberOfMachines = new TextField();
        numberOfMachines.setMinWidth(7);
        TextField numberOfDetails = new TextField();
        numberOfDetails.setMinWidth(71);

        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent event) {
                Group root2 = new Group();
                Group root3 = new Group();
                Group root4 = new Group();

                m = Integer.parseInt(numberOfMachines.getText());
                n = Integer.parseInt(numberOfDetails.getText());
                inputData.initInputData(m, n);
                primaryStage.hide();

                stage = new SecondWindow(root2, m, n, inputData);
                stage.setTitle("Input data");
                stage.showAndWait();

                stage = new ThirdWindow(root3, m, n, inputData, outputData);
                stage.setTitle("Input data");
                stage.showAndWait();

                stage = new WindowWithResults(root4, outputData, m, n);
                stage.setTitle("Results");
                stage.showAndWait();

                primaryStage.show();
            }
        });

        hBox1.getChildren().add(labelMachine);
        hBox1.setSpacing(5);
        hBox1.getChildren().add(numberOfMachines);

        hBox2.getChildren().add(labelDetails);
        hBox2.setSpacing(20);
        hBox2.getChildren().add(numberOfDetails);

        vBox.getChildren().add(hBox1);
        vBox.setSpacing(10);
        vBox.getChildren().add(hBox2);
        vBox.setSpacing(10);
        vBox.getChildren().add(okButton);
        root.getChildren().add(vBox);
    }
}
