package org.example;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.opencv.core.Core;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;

public class OnlineExamApp {
    private JFrame frame;
    private JPanel panel;
    private JLabel cameraLabel;
    private JButton uploadFileButton;
    private JTextArea compilationOutputArea;
    private JTextArea textArea;
    private File selectedFile;

    private CameraHandler cameraHandler;

    private static final String SERVER_ADDRESS = "ws://localhost:8887";

    // WebSocket Client
    private final WebSocketClient client = new WebSocketClient(URI.create(SERVER_ADDRESS)) {
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Connected to server");
        }

        @Override
        public void onMessage(String message) {
            // Do nothing for text messages
        }

        @Override
        public void onMessage(ByteBuffer message) {
            // Display video from server
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(message.array()));
                cameraLabel.setIcon(new ImageIcon(img)); // Update image on JLabel
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Disconnected from server: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }
    };

    public OnlineExamApp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        initializeUI();
        cameraHandler = new CameraHandler();
        cameraHandler.startCamera(cameraLabel, client); // Automatically start camera
    }

    private void initializeUI() {
        frame = new JFrame("Online Exam System");
        panel = new JPanel(new BorderLayout());

        // Camera label
        cameraLabel = new JLabel("Camera feed will be here", SwingConstants.CENTER);
        cameraLabel.setPreferredSize(new Dimension(320, 240));
        cameraLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(cameraLabel, BorderLayout.EAST);

        // Text area for displaying exercise questions
        textArea = createTextArea("This is a static text.\n"
                + "You can add information related to the exam here.\n"
                + "This text will scroll if it's too long.\n"
                + "Good luck with the exam!");
        JScrollPane textScrollPane = new JScrollPane(textArea);
        panel.add(textScrollPane, BorderLayout.CENTER);

        // Output area for compilation and execution outputs
        compilationOutputArea = createTextArea("");
        JScrollPane compilationOutputScrollPane = new JScrollPane(compilationOutputArea);
        compilationOutputScrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(compilationOutputScrollPane, BorderLayout.SOUTH);

        // Button panel for upload and submit actions
        JPanel buttonPanel = new JPanel();
        uploadFileButton = new JButton("Upload Java File");
        JButton submitButton = new JButton("Submit Exam");
        buttonPanel.add(uploadFileButton);
        buttonPanel.add(submitButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        // Panel for exercise buttons
        JPanel exercisePanel = new JPanel(new GridLayout(4, 1)); // 4 rows for 4 exercises
        JButton[] exerciseButtons = new JButton[4];

        // Create buttons for exercises
        for (int i = 0; i < 4; i++) {
            int index = i; // Final variable for use in lambda
            exerciseButtons[i] = new JButton("Exercise " + (i + 1));
            exerciseButtons[i].addActionListener(e -> showExercise(index));
            exercisePanel.add(exerciseButtons[i]);
        }

        // JScrollPane for exercise buttons
        JScrollPane exerciseScrollPane = new JScrollPane(exercisePanel);
        panel.add(exerciseScrollPane, BorderLayout.WEST);

        // Final setup for the frame
        frame.add(panel);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Action listeners for buttons
        uploadFileButton.addActionListener(e -> uploadJavaFile());
        submitButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Exam submitted successfully!"));

        // Connect to the WebSocket server
        client.connect();
    }

    // Method to show the exercise based on button clicked
    private void showExercise(int index) {
        String[] questions = {
                "Question for Exercise 1: What is Java?",
                "Question for Exercise 2: Explain OOP concepts.",
                "Question for Exercise 3: What is inheritance?",
                "Question for Exercise 4: What is polymorphism?"
        };

        // Display the corresponding question in the text area
        textArea.setText(questions[index]);
    }

    // Method to create a JTextArea
    private JTextArea createTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false); // Set to false to make it read-only
        return textArea;
    }

    private void uploadJavaFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            textArea.append("\nFile uploaded: " + selectedFile.getName());

            // Read file into byte array and send it through WebSocket
            try {
                byte[] fileData = new byte[(int) selectedFile.length()];
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                bufferedInputStream.read(fileData, 0, fileData.length);
                bufferedInputStream.close();
                String sendFileToServerText = "file";

                // Send the file data to the server
                client.send(ByteBuffer.wrap(fileData));

                // Optionally, you can provide feedback on successful upload
                compilationOutputArea.append("\nFile sent to server successfully.");

            } catch (IOException e) {
                e.printStackTrace();
                compilationOutputArea.append("\nError reading or sending the file.");
            }

            // Example of using a static input file for compilation and running
            File input = new File("C:\\Users\\ASUS\\Desktop\\test.txt");
            compileAndRunFile(selectedFile, input); // Compile and run after upload
        }
    }


    private void compileAndRunFile(File file, File inputFile) {
        try {
            // Compile the Java file
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", file.getAbsolutePath());
            compileProcessBuilder.redirectErrorStream(true); // Merge stdout and stderr
            Process compileProcess = compileProcessBuilder.start();
            int compileExitCode = compileProcess.waitFor();

            if (compileExitCode == 0) {
                compilationOutputArea.append("\nCompilation successful.\n");
                runCompiledFile(file, inputFile); // Run the compiled file
            } else {
                handleCompileError(compileProcess);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            compilationOutputArea.append("\nError during compilation or execution.\n");
        }
    }

    private void runCompiledFile(File file, File inputFile) {
        try {
            // Lấy thư mục chứa file đã biên dịch
            File parentDir = file.getParentFile();

            // Lấy tên lớp từ file Java
            String className = file.getName().replace(".java", "");

            // ProcessBuilder để chạy lớp Java
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", className);
            runProcessBuilder.directory(parentDir); // Đặt thư mục làm việc là thư mục chứa file

            // Chuyển hướng đầu vào từ file đầu vào
            runProcessBuilder.redirectInput(inputFile);

            // Bắt đầu quá trình
            Process runProcess = runProcessBuilder.start();

            // Đọc đầu ra từ quá trình (stdout)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()))) {

                String line;
                compilationOutputArea.append("\n"); // Add a blank line before the output
                while ((line = reader.readLine()) != null) {
                    compilationOutputArea.append(line + "\n");
                }

                // Đọc bất kỳ đầu ra lỗi nào từ quá trình
                while ((line = errorReader.readLine()) != null) {
                    compilationOutputArea.append("ERROR: " + line + "\n"); // Thêm tiền tố "ERROR:" cho các lỗi
                }
            }

            int exitCode = runProcess.waitFor();
            if (exitCode == 0) {
                compilationOutputArea.append("\nChương trình thực thi thành công.\n");
            } else {
                compilationOutputArea.append("\nChương trình kết thúc với mã: " + exitCode + "\n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            compilationOutputArea.append("\nLỗi trong quá trình thực thi chương trình.\n");
        }
    }

    private void handleCompileError(Process compileProcess) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
            String line;
            compilationOutputArea.append("\n"); // Add a blank line before the output
            while ((line = reader.readLine()) != null) {
                compilationOutputArea.append(line + "\n");
            }
        }
        compilationOutputArea.append("\nCompilation failed.\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineExamApp::new);
    }
}
