import javax.swing.*;
import java.awt.*;

public class SimpleMadlibs extends JFrame {
    
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
    
    private JTextField[] inputFields;
    private JTextArea storyDisplay;
    private Template template;
    
    public SimpleMadlibs() {
        setTitle("Simple Madlibs");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create our story template
        template = new Template(
            "The Wild West",
            new String[]{"Person's Name", "Adjective", "Verb (past tense)"},
            "Sheriff %s rode into town. Everything looked %s. Then he %s!"
        );
        
        // Create input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        inputFields = new JTextField[template.prompts.length];
        for (int i = 0; i < template.prompts.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel(template.prompts[i] + ":");
            inputFields[i] = new JTextField(15);
            fieldPanel.add(label);
            fieldPanel.add(inputFields[i]);
            inputPanel.add(fieldPanel);
        }
        
        // Create button
        JButton generateButton = new JButton("Generate Story");
        generateButton.addActionListener(e -> generateStory());
        inputPanel.add(generateButton);
        
        // Create display area
        storyDisplay = new JTextArea(10, 30);
        storyDisplay.setLineWrap(true);
        storyDisplay.setWrapStyleWord(true);
        storyDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(storyDisplay);
        
        add(inputPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void generateStory() {
        Object[] inputs = new Object[inputFields.length];
        for (int i = 0; i < inputFields.length; i++) {
            inputs[i] = inputFields[i].getText().trim();
        }
        
        String story = String.format(template.storyTemplate, inputs);
        storyDisplay.setText(story);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleMadlibs());
    }
}