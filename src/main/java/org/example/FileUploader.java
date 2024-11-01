package org.example;

import javax.swing.*;
import java.io.*;

public class FileUploader {
    private String destinationFolder;

    public FileUploader(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File destinationFile = new File(destinationFolder, selectedFile.getName());
            try (InputStream in = new FileInputStream(selectedFile);
                 FileOutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                JOptionPane.showMessageDialog(null, "File uploaded successfully to: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to upload file: " + e.getMessage());
            }
        }
    }
}
