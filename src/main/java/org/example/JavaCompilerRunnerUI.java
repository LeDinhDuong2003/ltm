package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class JavaCompilerRunnerUI extends JFrame {
    private JTextField textField;
    private JButton uploadButton;
    private JButton compileButton;
    private JTextArea outputArea;
    private File selectedFile;

    public JavaCompilerRunnerUI() {
        createUI();
    }

    private void createUI() {
        setTitle("Java File Uploader and Compiler");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        textField = new JTextField();
        uploadButton = new JButton("Upload Java File");
        compileButton = new JButton("Compile and Run Java File");
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile(); // Gọi phương thức uploadFile
            }
        });

        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileAndRun(); // Gọi phương thức compileAndRun
            }
        });

        panel.add(textField);
        panel.add(uploadButton);
        panel.add(compileButton);
        panel.add(scrollPane);

        add(panel);
    }

    public void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            textField.setText(selectedFile.getAbsolutePath()); // Hiển thị đường dẫn tệp
            outputArea.setText("File uploaded: " + selectedFile.getName()); // Hiển thị tên tệp
        }
    }

    public void compileAndRun() {
        if (selectedFile != null) {
            compileAndRunFile(selectedFile);
        } else {
            JOptionPane.showMessageDialog(null, "Please upload a file first", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void compileAndRunFile(File file) {
        outputArea.setText(""); // Xóa dữ liệu trước khi hiển thị mới
        try {
            // Compile the Java file
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", file.getAbsolutePath());
            compileProcessBuilder.redirectErrorStream(true);
            Process compileProcess = compileProcessBuilder.start();
            int compileExitCode = compileProcess.waitFor();

            if (compileExitCode == 0) {
                outputArea.append("Compilation successful.\n");

                // Run the compiled Java file
                String fileName = file.getName();
                String className = fileName.substring(0, fileName.lastIndexOf('.'));
                ProcessBuilder runProcessBuilder = new ProcessBuilder("java", className);
                runProcessBuilder.directory(file.getParentFile());
                runProcessBuilder.redirectErrorStream(true);
                Process runProcess = runProcessBuilder.start();

                // Read the output of the running process
                BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    outputArea.append(line + "\n"); // Hiển thị dữ liệu đầu ra
                }
                int runExitCode = runProcess.waitFor();

                if (runExitCode != 0) {
                    outputArea.append("Error during execution.");
                }
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    outputArea.append(line + "\n"); // Hiển thị lỗi biên dịch
                }
                outputArea.append("Compilation failed.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            outputArea.setText("Error during compilation or execution.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JavaCompilerRunnerUI javaCompilerRunnerUI = new JavaCompilerRunnerUI();
                javaCompilerRunnerUI.setVisible(true);
            }
        });
    }
}
