//Final Project - Nir Hazan 
package LZW;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * GUI class for LZW Compressor.
 *
 * This class provides a graphical user interface for compressing and decompressing files
 * using the upgraded  LZW compression algorithm.
 * It includes buttons for compression and decompression, check boxes to choose dictionary sizes,and text box for compression ratio.
 * and displays progress and error messages to the user.
 */
public class GUI {

    private JFrame frame;
    private JButton compressButton;
    private JButton decompressButton;
    public static JProgressBar progressBar;
    private JCheckBox initialSize256Checkbox;
    private JCheckBox initialSize512Checkbox;
    private JCheckBox maxSize4096Checkbox;
    private JCheckBox maxSize8192Checkbox;
    private JCheckBox maxSize16384Checkbox;
    private JTextField numberTextField; 
    
    public interface ProgressListener {
        void updateProgress(int progress);
    }
    
    /**
     * Constructor that initializes the GUI components.
     */
    public GUI() {
        initializeComponents();
    }

    /**
     * Initializes the GUI components.
     */
    private void initializeComponents() {
        // Set the look and feel to the system's default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame with a compact size
        frame = new JFrame("Upgraded LZW Compressor - By Nir Hazan & May Setter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setResizable(true);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Create a layered pane to hold background and components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setSize(frame.getSize());

        // Load background image from the classpath
        try {
            ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/LZW/background.jpg"));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and position label for initial dictionary size
        JLabel initialSizeLabel = new JLabel("Select Initial Dictionary Size:");
        initialSizeLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        initialSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        initialSizeLabel.setBounds(100, 220, 300, 20);
        layeredPane.add(initialSizeLabel, JLayeredPane.PALETTE_LAYER);

        // Create a panel for initial dictionary size checkboxes
        JPanel initialSizePanel = new JPanel();
        initialSizePanel.setBounds(225, 240, 53, 70); // Adjusted height for two options
        initialSizePanel.setOpaque(false);
        initialSizePanel.setLayout(new GridLayout(2, 1)); // Only for checkboxes

        // Create checkboxes for initial dictionary size
        initialSize256Checkbox = new JCheckBox("256");
        initialSize512Checkbox = new JCheckBox("512");

        // Center the checkboxes
        initialSize256Checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        initialSize512Checkbox.setHorizontalAlignment(SwingConstants.CENTER);

        initialSize256Checkbox.addActionListener(e -> {
            initialSize512Checkbox.setSelected(false);
            if (initialSize256Checkbox.isSelected()) {
                UpgradedLZWCompression.setInitialDictionarySize(256);
            }
        });                                                                 //Set the check boxes

        initialSize512Checkbox.addActionListener(e -> {
            initialSize256Checkbox.setSelected(false);
            if (initialSize512Checkbox.isSelected()) {
                UpgradedLZWCompression.setInitialDictionarySize(512);
            }
        });

        initialSizePanel.add(initialSize256Checkbox);
        initialSizePanel.add(initialSize512Checkbox);
        layeredPane.add(initialSizePanel, JLayeredPane.PALETTE_LAYER);

        // Create and position label for max dictionary size
        JLabel maxSizeLabel = new JLabel("Select Maximum Dictionary Size:");
        maxSizeLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        maxSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        maxSizeLabel.setBounds(100, 350, 300, 20);
        layeredPane.add(maxSizeLabel, JLayeredPane.PALETTE_LAYER);

        // Create a panel for max dictionary size checkboxes
        JPanel maxSizePanel = new JPanel();
        maxSizePanel.setBounds(225, 370, 53, 100); // Smaller width for compact layout
        maxSizePanel.setOpaque(false);
        maxSizePanel.setLayout(new GridLayout(3, 1)); // Only for checkboxes

        // Create checkboxes for max dictionary size
        maxSize4096Checkbox = new JCheckBox("4096");
        maxSize8192Checkbox = new JCheckBox("8192");
        maxSize16384Checkbox = new JCheckBox("16384");

        // Center the checkboxes
        maxSize4096Checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        maxSize8192Checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        maxSize16384Checkbox.setHorizontalAlignment(SwingConstants.CENTER);

        maxSize4096Checkbox.addActionListener(e -> {
            maxSize8192Checkbox.setSelected(false);
            maxSize16384Checkbox.setSelected(false);
            if (maxSize4096Checkbox.isSelected()) {
                UpgradedLZWCompression.setMaxDictionarySize(4096);
            }
        });

        maxSize8192Checkbox.addActionListener(e -> {
            maxSize4096Checkbox.setSelected(false);                         // Set the checkboxes 
            maxSize16384Checkbox.setSelected(false);
            if (maxSize8192Checkbox.isSelected()) {
                UpgradedLZWCompression.setMaxDictionarySize(8192);
            }
        });

        maxSize16384Checkbox.addActionListener(e -> {
            maxSize4096Checkbox.setSelected(false);
            maxSize8192Checkbox.setSelected(false);
            if (maxSize16384Checkbox.isSelected()) {
                UpgradedLZWCompression.setMaxDictionarySize(16384);
            }
        });

        maxSizePanel.add(maxSize4096Checkbox);
        maxSizePanel.add(maxSize8192Checkbox);
        maxSizePanel.add(maxSize16384Checkbox);
        layeredPane.add(maxSizePanel, JLayeredPane.PALETTE_LAYER);

        // Add JTextField for compression ratio input
        JLabel numberInputLabel = new JLabel("Enter valid compressiion ratio( 0< ratio <1 ):");
        numberInputLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        numberInputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberInputLabel.setBounds(20, 300, 450, 30);
        layeredPane.add(numberInputLabel, JLayeredPane.PALETTE_LAYER);

        numberTextField = new JTextField();
        numberTextField.setHorizontalAlignment(SwingConstants.CENTER);
        numberTextField.setBounds(200, 330, 100, 25);
        layeredPane.add(numberTextField, JLayeredPane.PALETTE_LAYER);

        // Add an ActionListener to the JTextField to enable/disable buttons based on input validity
        numberTextField.addActionListener(e -> checkRatioAndToggleButtons());
        numberTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkRatioAndToggleButtons();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkRatioAndToggleButtons();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkRatioAndToggleButtons();
            }
        });

        // Create a compress button with enhanced style
        compressButton = new JButton("Compress File");
        compressButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        compressButton.setBackground(new Color(70, 130, 180));
        compressButton.setForeground(new Color(0, 100, 0));
        compressButton.setFocusPainted(false);
        compressButton.setBounds(100, 50, 300, 50); // Position and size
        compressButton.setEnabled(false); // Initially disabled
        layeredPane.add(compressButton, JLayeredPane.PALETTE_LAYER);

        // Create a decompress button with enhanced style
        decompressButton = new JButton("Decompress File");
        decompressButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        decompressButton.setBackground(new Color(60, 179, 113));
        decompressButton.setForeground(Color.RED);
        decompressButton.setFocusPainted(false);
        decompressButton.setBounds(100, 120, 300, 50); // Position and size
        decompressButton.setEnabled(false); // Initially disabled
        layeredPane.add(decompressButton, JLayeredPane.PALETTE_LAYER);

        // Create a shared progress bar for both compression and decompression
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.GREEN);
        progressBar.setBounds(100, 190, 300, 30); // Position and size
        layeredPane.add(progressBar, JLayeredPane.PALETTE_LAYER);

        // Add layered pane to the frame
        frame.setContentPane(layeredPane);
    }

    /**
     * Checks if the compression ratio is valid and enables/disables buttons accordingly.
     */
    private void checkRatioAndToggleButtons() {
        boolean isValid = isNumberValid();
        compressButton.setEnabled(isValid);
        decompressButton.setEnabled(isValid);
    }

    /**
     * Starts the GUI by setting up button actions and showing the frame.
     * 
     * @param gui The GUI instance to start.
     */
    public void startGUI(GUI gui) {
        // Initialize the GUI components
        JFrame frame = gui.getFrame();
        JButton compressButton = gui.getCompressButton();
        JButton decompressButton = gui.getDecompressButton();
        JProgressBar progressBar = gui.getProgressBar();

        // Set up the compress button action
        compressButton.addActionListener(e -> {
            // Check if both checkboxes are selected and number is valid
            if (!isInitialSizeSelected() || !isMaxSizeSelected() || !isNumberValid()) {
                JOptionPane.showMessageDialog(frame, "Please select both initial and maximum dictionary sizes and enter a valid compression ratio between 0 and 1.");
                return;
            }

            // Set the minimum compression ratio using the input from the text field
            double minCompressionRatio = Double.parseDouble(numberTextField.getText());
            try {
                UpgradedLZWCompression.setMinCompressionRatio(minCompressionRatio);
            } catch (IllegalArgumentException E) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid compression ratio between 0 and 1.");
            }

            JFileChooser fileChooser = new JFileChooser();
            JOptionPane.showMessageDialog(frame, "Please choose file to compress...");
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                progressBar.setValue(0); // Reset progress bar
                new Thread(() -> {
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        byte[] inputBytes = inputStream.readAllBytes();
                        byte[] compressedBytes = UpgradedLZWCompression.compress(inputBytes);
                        // Open a save dialog to choose where to save the compressed file
                        JFileChooser saveFileChooser = new JFileChooser();
                        JOptionPane.showMessageDialog(frame, "Please choose where to save the compressed file...");
                        saveFileChooser.setSelectedFile(new File(file.getParent(), file.getName() + ".ulzw"));
                        int saveOption = saveFileChooser.showSaveDialog(frame);
                        if (saveOption == JFileChooser.APPROVE_OPTION) {
                            File outputFile = saveFileChooser.getSelectedFile();
                            // Save the compressed file
                            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                                outputStream.write(compressedBytes);
                                JOptionPane.showMessageDialog(frame, "File compressed successfully.");
                            }
                        }

                        // Reset progress bar after saving the file
                        progressBar.setValue(0);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "An error occurred during compression.");
                    }
                }).start();
            }
        });

        // Set up the decompress button action
        decompressButton.addActionListener(e -> {
            // Check if both checkboxes are selected and number is valid
            if (!isInitialSizeSelected() || !isMaxSizeSelected() || !isNumberValid()) {
                JOptionPane.showMessageDialog(frame, "Please select both initial and maximum dictionary sizes and enter a valid compression ratio between 0 and 1.");
                return;
            }

            // Set the minimum compression ratio using the input from the text field
            double minCompressionRatio = Double.parseDouble(numberTextField.getText());
            try {
                UpgradedLZWCompression.setMinCompressionRatio(minCompressionRatio);
            } catch (IllegalArgumentException E) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid compression ratio between 0 and 1.");
            }

            JFileChooser fileChooser = new JFileChooser();
            JOptionPane.showMessageDialog(frame, "Please choose file to decompress...");
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                progressBar.setValue(0); // Reset progress bar
                new Thread(() -> {
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        byte[] compressedBytes = inputStream.readAllBytes();
                        byte[] decompressedBytes = UpgradedLZWCompression.decompress(compressedBytes);
                        // Check if decompressed data is not empty
                        if (decompressedBytes.length > 0) {
                            String originalFileName = file.getName().replace(".lzw", "");
                            int lastDotIndex = originalFileName.lastIndexOf(".");
                            String baseName = (lastDotIndex == -1) ? originalFileName : originalFileName.substring(0, lastDotIndex);
                            String fileExtension = (lastDotIndex == -1) ? "" : originalFileName.substring(lastDotIndex);

                            // Open a save dialog to choose where to save the decompressed file
                            JFileChooser saveFileChooser = new JFileChooser();
                            JOptionPane.showMessageDialog(frame, "Please choose where to save the decompressed file...");
                            saveFileChooser.setSelectedFile(new File(file.getParent(), baseName + "_de" + fileExtension));
                            int saveOption = saveFileChooser.showSaveDialog(frame);
                            if (saveOption == JFileChooser.APPROVE_OPTION) {
                                File outputFile = saveFileChooser.getSelectedFile();

                                // Save the decompressed file
                                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                                    outputStream.write(decompressedBytes);
                                    JOptionPane.showMessageDialog(frame, "File decompressed successfully.");
                                }
                            }

                            // Reset progress bar after saving the file
                            progressBar.setValue(0);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Decompressed data is empty. Decompression failed.");
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "An error occurred during decompression.");
                    }
                }).start();
            }
        });

        // Show the GUI
        gui.showGUI();
    }

    /**
     * Checks if an initial dictionary size is selected.
     * 
     * @return true if any initial dictionary size checkbox is selected, false otherwise.
     */
    private boolean isInitialSizeSelected() {
        return initialSize256Checkbox.isSelected() || initialSize512Checkbox.isSelected();
    }

    /**
     * Checks if a maximum dictionary size is selected.
     * 
     * @return true if any maximum dictionary size checkbox is selected, false otherwise.
     */
    private boolean isMaxSizeSelected() {
        return maxSize4096Checkbox.isSelected() || maxSize8192Checkbox.isSelected() || maxSize16384Checkbox.isSelected();
    }

    /**
     * Checks if the input number is valid (between 0 and 1).
     * 
     * @return true if the number is valid, false otherwise.
     */
    private boolean isNumberValid() {
        try {
            double number = Double.parseDouble(numberTextField.getText());
            return number > 0 && number <1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns the main frame of the GUI.
     *
     * @return the JFrame representing the main window of the GUI.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Returns the compress button.
     *
     * @return the JButton used for compressing files.
     */
    public JButton getCompressButton() {
        return compressButton;
    }

    /**
     * Returns the decompress button.
     *
     * @return the JButton used for decompressing files.
     */
    public JButton getDecompressButton() {
        return decompressButton;
    }

    /**
     * Returns the progress bar.
     *
     * @return the JProgressBar showing the progress of compression or decompression.
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }
    

    /**
     * Makes the GUI visible to the user.
     * This method should be called after all components are initialized and ready for display.
     */
    public void showGUI() {
        frame.setVisible(true);
        JOptionPane.showMessageDialog(frame, "Please select both initial and maximum dictionary sizes and enter a valid compression ratio between 0 and 1.");
    }

}
