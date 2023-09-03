package lk.ijse.groupchatapplication.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created By shamodha_s_rathnamalala
 * Date : 5/21/2023
 * Time :10:51 PM
 */

public class LoginFormController {
    @FXML
    public TextField txtUserName;
    private static double xOffset = 0;
    private static double yOffset = 0;

    @FXML
    protected void loginOnAction() throws IOException {
        Stage stage = (Stage) txtUserName.getScene().getWindow();
        stage.setTitle("Chat Room");
        ChatFormController.userName = txtUserName.getText();
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/groupchatapplication/view/ChatForm.fxml"));
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void closeOnAction(MouseEvent mouseEvent) {
        Platform.exit();
    }

    @FXML
    public void minimizeOnAction(MouseEvent mouseEvent) {

    }
}
