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


public class SecondWindow extends Stage {

    public SecondWindow(Group root, int m, int n, InputData inputData) {
        setScene(new Scene(root));

        Label machineWorkingTimeLabel [] = new Label[m];
        for (int i = 0; i < machineWorkingTimeLabel.length; i++) {
             machineWorkingTimeLabel[i] = new Label("Machine " + (i+1) + " working time:");
        }
        Label numberOfDetailsLabel [] = new Label[n];
        for (int i = 0; i< numberOfDetailsLabel.length; i++) {
            numberOfDetailsLabel[i] = new Label("Number of " + (i+1) + " type details:");
        }

        TextField machineWorkingTime [] = new TextField[m];
        for (int i = 0; i < machineWorkingTime.length; i++) {
            machineWorkingTime[i] = new TextField();
        }
        TextField numberOfDetails[] = new TextField[n];
        for (int i = 0; i < numberOfDetails.length; i++) {
            numberOfDetails[i] = new TextField();
        }

        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent event) {
                int workingTime[] = new int[m];
                for (int i = 0; i < m; i++) {
                    workingTime[i] = Integer.parseInt(machineWorkingTime[i].getText());
                }
                inputData.setMachineWorkingTime(workingTime);
                int details[] = new int[n];
                for (int i = 0; i < n; i++) {
                    details[i] = Integer.parseInt(numberOfDetails[i].getText());
                }
                inputData.setNumberOfDetailsByType(details);
                close();
            }
        });

        HBox hBox = new HBox();
        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();

        vBox1.getChildren().addAll(machineWorkingTimeLabel);
        vBox1.setSpacing(13);
        vBox1.getChildren().addAll(numberOfDetailsLabel);
        vBox1.getChildren().add(okButton);

        vBox2.getChildren().addAll(machineWorkingTime);
        vBox2.setSpacing(5);
        vBox2.getChildren().addAll(numberOfDetails);

        hBox.getChildren().addAll(vBox1, vBox2);

        root.getChildren().add(hBox);
    }
}
