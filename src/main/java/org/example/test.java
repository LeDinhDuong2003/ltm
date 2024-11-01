package org.example;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String x = sc.next();
        System.out.println(x);
    }
}

//    private void uploadJavaFile() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
//        int returnValue = fileChooser.showOpenDialog(frame);
//        if (returnValue == JFileChooser.APPROVE_OPTION) {
//            selectedFile = fileChooser.getSelectedFile();
//            textArea.append("\nFile uploaded: " + selectedFile.getName());
//            File input = new File("C:\\Users\\ASUS\\Desktop\\test.txt");
//            System.out.println(input.getName());
//
//            compileAndRunFile(selectedFile, input); // Compile and run after upload
//        }
//    }

//    private void compileAndRunFile(File file, File inputFile) {
//        try {
//            // Compile the Java file
//            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", file.getAbsolutePath());
//            compileProcessBuilder.redirectErrorStream(true); // Merge stdout and stderr
//            Process compileProcess = compileProcessBuilder.start();
//            int compileExitCode = compileProcess.waitFor();
//
//            if (compileExitCode == 0) {
//                compilationOutputArea.append("\nCompilation successful.\n");
//                runCompiledFile(file, inputFile); // Run the compiled file
//            } else {
//                handleCompileError(compileProcess);
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            compilationOutputArea.append("\nError during compilation or execution.\n");
//        }
//    }

//    private void runCompiledFile(File file, File inputFile) {
//        try {
//            // Lấy thư mục chứa file đã biên dịch
//            File parentDir = file.getParentFile();
//
//            // Lấy tên lớp từ file Java
//            String className = file.getName().replace(".java", "");
//
//            // ProcessBuilder để chạy lớp Java
//            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", className);
//            runProcessBuilder.directory(parentDir); // Đặt thư mục làm việc là thư mục chứa file
//
//            // Chuyển hướng đầu vào từ file đầu vào
//            runProcessBuilder.redirectInput(inputFile);
//
//            // Bắt đầu quá trình
//            Process runProcess = runProcessBuilder.start();
//
//            // Đọc đầu ra từ quá trình (stdout)
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
//                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()))) {
//
//                String line;
//                compilationOutputArea.append("\n"); // Add a blank line before the output
//                while ((line = reader.readLine()) != null) {
//                    compilationOutputArea.append(line + "\n");
//                }
//
//                // Đọc bất kỳ đầu ra lỗi nào từ quá trình
//                while ((line = errorReader.readLine()) != null) {
//                    compilationOutputArea.append("ERROR: " + line + "\n"); // Thêm tiền tố "ERROR:" cho các lỗi
//                }
//            }
//
//            int exitCode = runProcess.waitFor();
//            if (exitCode == 0) {
//                compilationOutputArea.append("\nChương trình thực thi thành công.\n");
//            } else {
//                compilationOutputArea.append("\nChương trình kết thúc với mã: " + exitCode + "\n");
//            }
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            compilationOutputArea.append("\nLỗi trong quá trình thực thi chương trình.\n");
//        }
//    }

//    private void handleCompileError(Process compileProcess) throws IOException {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
//            String line;
//            compilationOutputArea.append("\n"); // Add a blank line before the output
//            while ((line = reader.readLine()) != null) {
//                compilationOutputArea.append(line + "\n");
//            }
//        }
//        compilationOutputArea.append("\nCompilation failed.\n");
//    }
