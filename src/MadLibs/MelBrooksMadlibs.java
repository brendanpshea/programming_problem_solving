import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class MelBrooksMadlibs extends JFrame {
    
    // Template class to hold story templates
    private static class Template {
        String name;
        String[] prompts;
        String storyTemplate;
        
        Template(String name, String[] prompts, String storyTemplate) {
            this.name = name;
            this.prompts = prompts;
            this.storyTemplate = storyTemplate;
        }
    }
    
    // GUI Components
    private JComboBox<String> templateSelector;
    private JPanel inputPanel;
    private JTextField[] inputFields;
    private JTextArea storyDisplay;
    private JButton generateButton;
    private JButton saveButton;
    private Template currentTemplate;
    
    // Templates array
    private Template[] templates;
    
    public MelBrooksMadlibs() {
        setTitle("Mel Brooks Madlibs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        
        initializeTemplates();
        initializeGUI();
    }
    
    private void initializeTemplates() {
        templates = new Template[3];
        
        // Template 1: Blazing Saddles Western Theme
        templates[0] = new Template(
            "The Wild West Adventure",
            new String[]{"Person's Name", "Adjective", "Adjective", "Verb (past tense)", "Body Part", "Animal", "Food"},
            "Sheriff %s rode into town on his trusty %s horse. The townspeople looked %s as he %s " +
            "into the saloon. 'Listen up!' he shouted, scratching his %s. 'There's a gang of " +
            "wild %ss coming, and they're hungry for %s!'"
        );
        
        // Template 2: Young Frankenstein Horror Theme
        templates[1] = new Template(
            "The Mad Scientist's Lab",
            new String[]{"Adjective", "Noun", "Exclamation", "Verb", "Body Part", "Silly Sound"},
            "In the %s castle, Dr. Frankenstein worked on his greatest creation. 'Hand me that %s!' " +
            "he demanded. Igor replied, '%s!' Lightning struck as the doctor began to %s the lever. " +
            "The monster's %s twitched, and suddenly it made a loud '%s' sound. 'It's alive!' they shouted."
        );
        
        // Template 3: Spaceballs Sci-Fi Theme
        templates[2] = new Template(
            "Space Opera Spectacular",
            new String[]{"Adjective", "Adjective", "Verb", "Color", "Number", "Adverb"},
            "Captain Lone Starr piloted his %s spaceship through the %s nebula. 'We need to %s " +
            "the hyperdrive!' he yelled. The radar showed %s dots approaching. Dark Helmet had " +
            "sent %s enemy fighters, and they were moving %s!"
        );
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top Panel: Template Selection
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Template:"));
        
        String[] templateNames = new String[templates.length];
        for (int i = 0; i < templates.length; i++) {
            templateNames[i] = templates[i].name;
        }
        
        templateSelector = new JComboBox<>(templateNames);
        templateSelector.addActionListener(e -> loadTemplate());
        topPanel.add(templateSelector);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel: Input Fields
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane inputScrollPane = new JScrollPane(inputPanel);
        add(inputScrollPane, BorderLayout.CENTER);
        
        // Right Panel: Story Display and Buttons
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        storyDisplay = new JTextArea(15, 30);
        storyDisplay.setLineWrap(true);
        storyDisplay.setWrapStyleWord(true);
        storyDisplay.setEditable(false);
        storyDisplay.setFont(new Font("Serif", Font.PLAIN, 14));
        JScrollPane storyScrollPane = new JScrollPane(storyDisplay);
        rightPanel.add(storyScrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        generateButton = new JButton("Generate Story");
        generateButton.addActionListener(e -> generateStory());
        buttonPanel.add(generateButton);
        
        saveButton = new JButton("Save to File");
        saveButton.addActionListener(e -> saveStory());
        saveButton.setEnabled(false);
        buttonPanel.add(saveButton);
        
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(rightPanel, BorderLayout.EAST);
        
        // Load first template by default
        loadTemplate();
    }
    
    private void loadTemplate() {
        int selectedIndex = templateSelector.getSelectedIndex();
        currentTemplate = templates[selectedIndex];
        
        // Clear previous inputs
        inputPanel.removeAll();
        
        // Create new input fields for current template
        inputFields = new JTextField[currentTemplate.prompts.length];
        
        for (int i = 0; i < currentTemplate.prompts.length; i++) {
            JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
            JLabel label = new JLabel(currentTemplate.prompts[i] + ":");
            label.setPreferredSize(new Dimension(150, 25));
            inputFields[i] = new JTextField(20);
            
            fieldPanel.add(label, BorderLayout.WEST);
            fieldPanel.add(inputFields[i], BorderLayout.CENTER);
            fieldPanel.setMaximumSize(new Dimension(400, 35));
            
            inputPanel.add(fieldPanel);
            inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Clear story display
        storyDisplay.setText("");
        saveButton.setEnabled(false);
        
        inputPanel.revalidate();
        inputPanel.repaint();
    }
    
    private void generateStory() {
        // Check if all fields are filled
        for (int i = 0; i < inputFields.length; i++) {
            if (inputFields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in all fields!",
                    "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
                inputFields[i].requestFocus();
                return;
            }
        }
        
        // Collect all input values
        Object[] inputs = new Object[inputFields.length];
        for (int i = 0; i < inputFields.length; i++) {
            inputs[i] = inputFields[i].getText().trim();
        }
        
        // Generate the story using String.format
        String story = String.format(currentTemplate.storyTemplate, inputs);
        
        storyDisplay.setText(story);
        saveButton.setEnabled(true);
    }
    
    private void saveStory() {
        String story = storyDisplay.getText();
        
        if (story.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No story to save!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("my_madlib_story.txt"));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("=== Mel Brooks Madlibs ===");
                writer.println("Template: " + currentTemplate.name);
                writer.println("Created: " + new Date());
                writer.println();
                writer.println(story);
                writer.println();
                writer.println("--- Input Words ---");
                for (int i = 0; i < currentTemplate.prompts.length; i++) {
                    writer.println(currentTemplate.prompts[i] + ": " + inputFields[i].getText());
                }
                
                JOptionPane.showMessageDialog(this,
                    "Story saved successfully to:\n" + file.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MelBrooksMadlibs app = new MelBrooksMadlibs();
            app.setVisible(true);
        });
    }
}