package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientController {
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public VBox messageVBox;
    @FXML
    public TextField txtMessage;
    @FXML
    public Button btnSend;
    @FXML
    public Button btnFile;
    public Button btnUsernameSend;
    public Button btnDisconnect;
    public TextField txtUsername;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";
    private String username;


    @FXML
    public void initialize() {
        try {
            if (messageVBox != null) {
                messageVBox.setSpacing(10);
                scrollPane.setContent(messageVBox);
                scrollPane.setFitToWidth(true);

                messageVBox.heightProperty().addListener((observable, oldValue, newValue) ->
                        scrollPane.setVvalue(1.0));
            } else {
                System.err.println("Error: messageVBox is null. Check FXML file for proper fx:id");
                return;
            }

            new Thread(() -> {
                try {
                    socket = new Socket("localhost", 3000);
                    Platform.runLater(() -> addTextMessage("Client Connected"));
                    Platform.runLater(() -> addTextMessage("Username : " + username));


                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    while (!message.equals("Exit")) {
                        message = dataInputStream.readUTF();

                        if (message.startsWith("[IMAGE]")) {
                            String imagePath = message.substring(7);
                            Platform.runLater(() -> {
                                displayImage(imagePath);
                                addTextMessage("Server: [File Received]");
                            });
                        } else {
                            Platform.runLater(() -> addTextMessage("Server: " + message));
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> addTextMessage("Error: Server not found..Please check the connection"));
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTextMessage(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px;");
        label.setWrapText(true);
        messageVBox.getChildren().add(label);
    }

    private void displayImage(String imagePath) {
        try {
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            messageVBox.getChildren().add(imageView);
        } catch (Exception e) {
            addTextMessage("Error : Image is not found...");
            e.printStackTrace();
        }
    }

    public void btnSendOnAction(ActionEvent actionEvent) {
        try {
            String message = txtMessage.getText().trim();
            if (!message.isEmpty()) {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                addTextMessage("Client: " + message);
                txtMessage.clear();
            }
        } catch (IOException e) {
            addTextMessage("Error: Failed to send message");
            e.printStackTrace();
        }
    }

    public void txtMessageOnAction(ActionEvent actionEvent) {
        btnSendOnAction(actionEvent);

    }

    public void btnFileOnAction(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                displayImage(selectedFile.getPath());
                dataOutputStream.writeUTF("[IMAGE]" + selectedFile.getPath());
                dataOutputStream.flush();
                addTextMessage("Client: [Image Sent]");
            } catch (IOException e) {
                addTextMessage("Error: Failed to send image..! Try again..");
                e.printStackTrace();
            }
        }
    }

    public void btnUsernameSendOnAction(ActionEvent actionEvent) {
        try {
            username = txtUsername.getText().trim();
            if (!username.isEmpty()) {
                dataOutputStream.writeUTF(username);
                dataOutputStream.flush();
                addTextMessage("Client: " + username);
                txtUsername.clear();
            }
        } catch (IOException e) {
            addTextMessage("Error: Failed to send message");
            e.printStackTrace();
        }

    }

    public void btnDisconnectOnAction(ActionEvent actionEvent) throws IOException {
        socket.close();
    }

    public void txtUsernameOnAction(ActionEvent actionEvent) {
        btnUsernameSendOnAction(actionEvent);
    }
}
