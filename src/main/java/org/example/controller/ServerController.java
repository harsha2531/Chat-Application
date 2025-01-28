package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {
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

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";

    @FXML
    public void initialize() {
        try {
            messageVBox.setSpacing(10);
            scrollPane.setContent(messageVBox);
            scrollPane.setFitToWidth(true);

            messageVBox.heightProperty().addListener((observable, oldValue, newValue) ->
                    scrollPane.setVvalue(1.0));

            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(3000);
                    Platform.runLater(() -> addTextMessage("Server Started...."));

                    socket = serverSocket.accept();
                    Platform.runLater(() -> addTextMessage("Client 1 Connected!"));
                    Platform.runLater(() -> addTextMessage("Client 2 Connected!"));

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    while (!message.equals("Exit")) {
                        message = dataInputStream.readUTF();

                        if (message.startsWith("[IMAGE]")) {
                            String imagePath = message.substring(7);
                            Platform.runLater(() -> {
                                displayImage(imagePath);
                                addTextMessage("Client: [Image Received]");
                            });
                        } else {
                            Platform.runLater(() -> addTextMessage("Client: " + message));
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> addTextMessage("Error: Connection is disabled..."));
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addTextMessage(String text) {
        Label label = new Label(text);
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
            addTextMessage("Error : Image is not available");
            e.printStackTrace();
        }

    }

    public void txtMessageOnAction(ActionEvent actionEvent) {
        btnSendOnAction(actionEvent);
    }

    public void btnSendOnAction(ActionEvent actionEvent) {
        try {
            String message = txtMessage.getText().trim();
            if (!message.isEmpty()) {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                System.out.println("error :" + dataOutputStream);
                addTextMessage("Server: " + message);
                txtMessage.clear();
            }
        } catch (IOException e) {
            addTextMessage("Error: Message is sending failed");
            e.printStackTrace();
        }

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
                addTextMessage("Server: [Image Sent]");
            } catch (IOException e) {
                addTextMessage("Error: Image sending failed");
                e.printStackTrace();
            }
        }
    }
}
