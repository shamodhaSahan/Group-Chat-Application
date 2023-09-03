package lk.ijse.groupchatapplication.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Created By shamodha_s_rathnamalala
 * Date : 6/8/2023
 * Time : 4:03 PM
 */

public class ChatFormController implements Initializable {
    @FXML
    public AnchorPane ancPane;
    @FXML
    public ScrollPane scPane;
    @FXML
    public VBox vBox;
    @FXML
    public Label lblClientName;
    @FXML
    public TextField txtTextMessage;
    public static String userName;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            lblClientName.setText(userName);
            Socket socket = new Socket("localhost", 1234);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println(userName + " Connected to server : " + socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread messageReceiver = new Thread(()->{
            try {

                while (true) {
                    Label lblName = new Label();
                    lblName.setStyle("-fx-background-color: #82CCDD;-fx-background-radius:15;-fx-font-size: 13;-fx-text-fill: #000000;-fx-alignment: center;-fx-padding: 5px;");

                    Label lblMessage = new Label();
                    lblMessage.setStyle("-fx-background-color: #82CCDD;-fx-background-radius:15;-fx-font-size: 16;-fx-text-fill: #000000;-fx-alignment: center;-fx-padding: 5px;");

                    ImageView imgReserved = new ImageView();
                    imgReserved.setFitWidth(200);
                    imgReserved.setPreserveRatio(true);

                    String[] data = bufferedReader.readLine().split("/#sendingClientName#/");
                    String clientName = data[0];
                    String message = data[1];
                    if (message != null) {
                        Platform.runLater(() -> {
                            HBox hBoxMessage = new HBox();
                            hBoxMessage.setSpacing(10);
                            hBoxMessage.setStyle("-fx-alignment: center-left;-fx-pref-width: 584;-fx-padding: 10px 0 0 0");

                            lblName.setText(clientName);
                            if (message.startsWith("Image:")) {
                                byte[] imageData = Base64.getDecoder().decode(message.substring(6));
                                imgReserved.setImage(new Image(new ByteArrayInputStream(imageData)));

                                HBox hBoxImage = new HBox(imgReserved);
                                hBoxImage.setStyle("-fx-background-color: #82CCDD;-fx-background-radius:15;-fx-alignment: center;-fx-padding: 20px 5px;");

                                hBoxMessage.getChildren().addAll(lblName, hBoxImage);
                            } else {
                                lblMessage.setText(message);
                                hBoxMessage.getChildren().addAll(lblName, lblMessage);
                            }
                            vBox.getChildren().add(hBoxMessage);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        messageReceiver.setDaemon(true);
        messageReceiver.start();
    }

    private void sendMessage(String message, HBox hBox) throws IOException {
        hBox.setStyle("-fx-alignment: center-right;-fx-pref-width: 584;-fx-padding: 10px 0 0 0");
        vBox.getChildren().add(hBox);
        message = userName+"/#sendingClientName#/"+message;
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    @FXML
    public void textSendOnAction(ActionEvent actionEvent) {
        String message = txtTextMessage.getText();
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-background-color: #78E08F;-fx-background-radius:15;-fx-font-size: 16;-fx-alignment: center;-fx-padding: 5px;");

        if (!message.isEmpty()) {
            try {
                sendMessage(message, new HBox(messageLabel));
                txtTextMessage.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void imageSendOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] imageData = Files.readAllBytes(file.toPath());
                String encodedImage = Base64.getEncoder().encodeToString(imageData);
                String message = "Image:" + encodedImage;
                ImageView imageView = new ImageView(new Image(file.getPath()));
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);

                HBox imageHbox = new HBox(imageView);
                imageHbox.setStyle("-fx-background-color: #78E08F;-fx-background-radius:15;-fx-alignment: center;-fx-padding: 20px 5px;");

                sendMessage(message, new HBox(imageHbox));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void emojiSendOnAction(ActionEvent actionEvent) {
        String selectedEmoji = "🙂";
        txtTextMessage.appendText(selectedEmoji);
        try {
            sendMessage(selectedEmoji, new HBox());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void minimizeOnAction(MouseEvent mouseEvent) {
    }

    @FXML
    public void closeOnAction(MouseEvent mouseEvent) {
        Platform.exit();
    }
}