import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class textEditor extends JFrame {
    private JTextPane textArea;
    private JComboBox<String> fontSelector;
    private JComboBox<Integer> fontSizeSelector;
    private Color currentColor;
    private int lastSearchIndex = 0;


    private ImageIcon createScaledImageIcon(String imagePath, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void createParagraphAlignmentButtons(JPanel menuPanel) {
        String leftAlignImagePath = "left-align.png";
        String centerAlignImagePath = "center-align.png";
        String rightAlignImagePath = "right-align.png";
        int iconWidth = 20;
        int iconHeight = 20;
    
        JButton leftAlignButton = new JButton(createScaledImageIcon(leftAlignImagePath, iconWidth, iconHeight));
        leftAlignButton.setPreferredSize(new Dimension(iconWidth, iconHeight));
        leftAlignButton.addActionListener(e -> leftAlign());
        menuPanel.add(leftAlignButton);
    
        JButton centerAlignButton = new JButton(createScaledImageIcon(centerAlignImagePath, iconWidth, iconHeight));
        centerAlignButton.setPreferredSize(new Dimension(iconWidth, iconHeight));
        centerAlignButton.addActionListener(e -> centerAlign());
        menuPanel.add(centerAlignButton);
    
        JButton rightAlignButton = new JButton(createScaledImageIcon(rightAlignImagePath, iconWidth, iconHeight));
        rightAlignButton.setPreferredSize(new Dimension(iconWidth, iconHeight));
        rightAlignButton.addActionListener(e -> rightAlign());
        menuPanel.add(rightAlignButton);
    }
    

    public textEditor() {
        setTitle("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile());
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
        cutItem.setText("Cut");
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyItem.setText("Copy");
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteItem.setText("Paste");
        editMenu.add(pasteItem);

        JMenuItem findItem = new JMenuItem("Find");
        findItem.addActionListener(e -> find());
        editMenu.add(findItem);

        JMenuItem findNextItem = new JMenuItem("Find Next");
        findNextItem.addActionListener(e -> findNext());
        editMenu.add(findNextItem);


        JMenuItem replaceItem = new JMenuItem("Replace");
        replaceItem.addActionListener(e -> replace());
        editMenu.add(replaceItem);
        menuBar.add(editMenu);

        // JMenu replaceAllItem = new JMenu("Replace All");
        // replaceAllItem.addActionListener(e -> replaceAll());
        // editMenu.add(replaceAllItem);
        // menuBar.add(editMenu);


        // Selection Menu
        JMenu selectionMenu = new JMenu("Selection");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        selectAllItem.addActionListener(e -> textArea.selectAll());
        selectionMenu.add(selectAllItem);
        
        // Format Menu
        // JMenu formatMenu = new JMenu("Format");
        // JMenuItem boldItem = new JMenuItem("Bold");
        // boldItem.addActionListener(e -> toggleBold());
        // formatMenu.add(boldItem);

        // JMenuItem italicItem = new JMenuItem("Italic");
        // italicItem.addActionListener(e -> toggleItalic());
        // formatMenu.add(italicItem);
   

        // Font Selector
        fontSelector = new JComboBox<>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        for (Font font : allFonts) {
            fontSelector.addItem(font.getFontName());
        }
        fontSelector.addActionListener(e -> {
            String selectedFont = fontSelector.getSelectedItem().toString();
            textArea.setFont(new Font(selectedFont, Font.PLAIN, (int) fontSizeSelector.getSelectedItem()));
        });

        // Font Size Selector
        fontSizeSelector = new JComboBox<>();
        for (int i = 8; i <= 72; i += 2) {
            fontSizeSelector.addItem(i);
        }
        fontSizeSelector.addActionListener(e -> {
            String selectedFont = fontSelector.getSelectedItem().toString();
            textArea.setFont(new Font(selectedFont, Font.PLAIN, (int) fontSizeSelector.getSelectedItem()));
        });



        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenu appearanceMenu = new JMenu("Appearance");
      
        JMenuItem lightThemeItem = new JMenuItem("Light Theme");
        lightThemeItem.addActionListener(e -> setLightTheme());
        appearanceMenu.add(lightThemeItem);
      
        JMenuItem darkThemeItem = new JMenuItem("Dark Theme");
        darkThemeItem.addActionListener(e -> setDarkTheme());
        appearanceMenu.add(darkThemeItem);
      
        JMenuItem purpleThemeItem = new JMenuItem("Purple Theme");
        purpleThemeItem.addActionListener(e -> setPurpleTheme());
        appearanceMenu.add(purpleThemeItem);
      
        viewMenu.add(appearanceMenu);
        menuBar.add(viewMenu);

        // Font Color Menu
        JMenu fontColorMenu = new JMenu("Font Color");
        JMenuItem colorChooserItem = new JMenuItem("Choose Color");
        colorChooserItem.addActionListener(e -> chooseFontColor());
        fontColorMenu.add(colorChooserItem);
        menuBar.add(fontColorMenu);


        // Menu Bar
        menuBar.add(selectionMenu);
        //menuBar.add(formatMenu);
        
        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        menuPanel.add(menuBar);
        menuPanel.add(fontSelector);
        menuPanel.add(fontSizeSelector);
        createParagraphAlignmentButtons(menuPanel); 
        createTextFormatButtons(menuPanel);


        // Text Area
        textArea = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.NORTH);

        // Sketchpad Button
        JButton sketchpadButton = new JButton("Open Sketchpad");
        sketchpadButton.addActionListener(e -> openSketchpad());
        JPanel sketchpadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sketchpadPanel.add(sketchpadButton);
        add(sketchpadPanel, BorderLayout.SOUTH);
    }

    private void openSketchpad() {
        JFrame sketchpadFrame = new JFrame("Sketchpad");
        SketchpadPanel sketchpadPanel = new SketchpadPanel();
        sketchpadFrame.add(sketchpadPanel);
        sketchpadFrame.setSize(800, 600);
        sketchpadFrame.setVisible(true);
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showOpenDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                textArea.setText(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showSaveDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        // private void toggleBold() {
        //     Font currentFont = textArea.getFont();
        //     Font newFont;
        //     if (currentFont.isBold()) {
        //         newFont = currentFont.deriveFont(Font.PLAIN);
        //     } else {
        //         newFont = currentFont.deriveFont(Font.BOLD);
        //     }
        //     textArea.setFont(newFont);
        // }
    
        // // Method to toggle italic style
        // private void toggleItalic() {
        //     Font currentFont = textArea.getFont();
        //     Font newFont;
        //     if (currentFont.isItalic()) {
        //         newFont = currentFont.deriveFont(Font.PLAIN);
        //     } else {
        //         newFont = currentFont.deriveFont(Font.ITALIC);
        //     }
        //     textArea.setFont(newFont);
        // }

        private void setLightTheme() {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            getContentPane().setBackground(Color.WHITE);
            textArea.getCaret().setBlinkRate(500);
        }
    
        private void setDarkTheme() {
            textArea.setBackground(Color.DARK_GRAY);
            textArea.setForeground(Color.WHITE);
            getContentPane().setBackground(Color.DARK_GRAY);
            textArea.getCaret().setBlinkRate(500);
        }
    
        private void setPurpleTheme() {
            textArea.setBackground(new Color(150, 111, 214));
            textArea.setForeground(Color.WHITE);
            getContentPane().setBackground(new Color(150, 111, 214));
            textArea.getCaret().setBlinkRate(500);
        }

        private void chooseFontColor() {
            currentColor = JColorChooser.showDialog(this, "Choose Font Color", currentColor);
            if (currentColor != null) {
                textArea.setForeground(currentColor);
            }
        }

        private void find() {
            String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
            if (searchText != null && !searchText.isEmpty()) {
                String document = textArea.getText();
                int index = document.indexOf(searchText);
                if (index >= 0) {
                    textArea.setCaretPosition(index);
                    textArea.moveCaretPosition(index + searchText.length());
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found");
                }
            }
        }
        
        private void findNext() {
            String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
            if (searchText != null && !searchText.isEmpty()) {
                String document = textArea.getText();
                lastSearchIndex = document.indexOf(searchText, lastSearchIndex + searchText.length());
                if (lastSearchIndex >= 0) {
                    textArea.setCaretPosition(lastSearchIndex);
                    textArea.moveCaretPosition(lastSearchIndex + searchText.length());
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found");
                    lastSearchIndex = 0; // Reset the search index if no more matches are found
                }
            }
            
        }

        private void replace() {
            String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
            if (searchText != null && !searchText.isEmpty()) {
                String replaceText = JOptionPane.showInputDialog(this, "Enter replacement text:");
                if (replaceText != null) {
                    String document = textArea.getText();
                    String newDocument = document.replace(searchText, replaceText);
                    textArea.setText(newDocument);
                }
            }
        }

        // private void replaceAll() {
        //     String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
        //     if (searchText != null && !searchText.isEmpty()) {
        //         String replaceText = JOptionPane.showInputDialog(this, "Enter replacement text:");
        //         if (replaceText != null) {
        //             String document = textArea.getText();
        //             String newDocument = document.replace(searchText, replaceText);
        //             textArea.setText(newDocument);
        //         }
        //     }
        // }
        
            
        private void leftAlign() {
            StyledDocument doc = textArea.getStyledDocument();
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
            doc.setParagraphAttributes(0, doc.getLength(), attributes, false);
        }
    
        private void centerAlign() {
            StyledDocument doc = textArea.getStyledDocument();
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), attributes, false);
        }
    
        private void rightAlign() {
            StyledDocument doc = textArea.getStyledDocument();
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);
            doc.setParagraphAttributes(0, doc.getLength(), attributes, false);
        }


private void createTextFormatButtons(JPanel menuPanel) {
    JButton boldButton = new JButton(createScaledImageIcon("bold.png", 20, 20));
    boldButton.setPreferredSize(new Dimension(20, 20));
    boldButton.addActionListener(e -> toggleBold());
    menuPanel.add(boldButton);

    JButton italicButton = new JButton(createScaledImageIcon("italic.png", 20, 20));
    italicButton.setPreferredSize(new Dimension(20, 20));
    italicButton.addActionListener(e -> toggleItalic());
    menuPanel.add(italicButton);

    JButton underlineButton = new JButton(createScaledImageIcon("underline.png", 20, 20));
    underlineButton.setPreferredSize(new Dimension(20, 20));
    underlineButton.addActionListener(e -> toggleUnderline());
    menuPanel.add(underlineButton);

    JButton strikethroughButton = new JButton(createScaledImageIcon("strikethrough.png", 20, 20));
    strikethroughButton.setPreferredSize(new Dimension(20, 20));
    strikethroughButton.addActionListener(e -> toggleStrikethrough());
    menuPanel.add(strikethroughButton);
}


private void toggleBold() {
    Font currentFont = textArea.getFont();
    Font newFont;
    if (currentFont.isBold()) {
        newFont = currentFont.deriveFont(Font.PLAIN);
    } else {
        newFont = currentFont.deriveFont(Font.BOLD);
    }
    textArea.setFont(newFont);
}

private void toggleItalic() {
    Font currentFont = textArea.getFont();
    Font newFont;
    if (currentFont.isItalic()) {
        newFont = currentFont.deriveFont(Font.PLAIN);
    } else {
        newFont = currentFont.deriveFont(Font.ITALIC);
    }
    textArea.setFont(newFont);
}

private void toggleUnderline() {
    StyledDocument doc = textArea.getStyledDocument();
    SimpleAttributeSet underline = new SimpleAttributeSet();
    StyleConstants.setUnderline(underline, !StyleConstants.isUnderline(underline));
    doc.setCharacterAttributes(textArea.getSelectionStart(), textArea.getSelectionEnd() - textArea.getSelectionStart(), underline, false);
}

private void toggleStrikethrough() {
    StyledDocument doc = textArea.getStyledDocument();
    SimpleAttributeSet strikethrough = new SimpleAttributeSet();
    StyleConstants.setStrikeThrough(strikethrough, !StyleConstants.isStrikeThrough(strikethrough));
    doc.setCharacterAttributes(textArea.getSelectionStart(), textArea.getSelectionEnd() - textArea.getSelectionStart(), strikethrough, false);
}
    
        
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new textEditor().setVisible(true));
    }
}



class SketchpadPanel extends JPanel {
    private int lastX, lastY;
    private int startX, startY;
    private String currentShape;
    private boolean isDrawing;
    private boolean freehandMode;
    private boolean eraserMode;
    private ArrayList<Shape> shapes; 

    SketchpadPanel() {
        setPreferredSize(new Dimension(800, 600));
        currentShape = "freehand";
        isDrawing = false;
        shapes = new ArrayList<>();

        JButton freehandButton = new JButton("Freehand");
        JButton rectangleButton = new JButton("Rectangle");
        JButton squareButton = new JButton("Square");
        JButton circleButton = new JButton("Circle");
        JButton ovalButton = new JButton("Oval");
        JButton lineButton = new JButton("Line");
        JButton freehandOffButton = new JButton("Freehand Off");
        freehandOffButton.addActionListener(e -> {
            freehandMode = false;
            repaint();
        });
        add(freehandOffButton);

        JButton eraserButton = new JButton("Eraser");
        eraserButton.addActionListener(e -> {
            eraserMode = true;
            repaint();
        });
        add(eraserButton);

        



        freehandButton.addActionListener(e -> setFreehandMode()); 
        freehandButton.addActionListener(e -> setCurrentShape("freehand"));
        rectangleButton.addActionListener(e -> setCurrentShape("rectangle"));
        squareButton.addActionListener(e -> setCurrentShape("square"));
        circleButton.addActionListener(e -> setCurrentShape("circle"));
        ovalButton.addActionListener(e -> setCurrentShape("oval"));
        lineButton.addActionListener(e -> setCurrentShape("line"));


        add(freehandButton);
        add(rectangleButton);
        add(squareButton);
        add(circleButton);
        add(ovalButton);
        add(lineButton);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                startX = e.getX();
                startY = e.getY();
                isDrawing = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDrawing = false;
                // Create and add the shape to the list of shapes
                if (currentShape.equals("rectangle")) {
                    shapes.add(new Rectangle(startX, startY, e.getX(), e.getY()));
                } else if (currentShape.equals("square")) {
                    shapes.add(new Square(startX, startY, e.getX(), e.getY()));
                } else if (currentShape.equals("circle")) {
                    shapes.add(new Circle(startX, startY, e.getX(), e.getY()));
                } else if (currentShape.equals("oval")) {
                    shapes.add(new Oval(startX, startY, e.getX(), e.getY()));
                } else if (currentShape.equals("line")) {
                    shapes.add(new Line(startX, startY, e.getX(), e.getY()));
                }
                repaint();
            }
        });

        class Freehand extends Shape {
            Freehand(int x1, int y1, int x2, int y2) {
                super(x1, y1, x2, y2);
            }
    
            @Override
            void draw(Graphics g) {
                g.drawLine(x1, y1, x2, y2);
            }
        }

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int newX = e.getX();
                int newY = e.getY();
                if (freehandMode && isDrawing) { // Check if Freehand mode is active
                    shapes.add(new Freehand(lastX, lastY, newX, newY));
                    lastX = newX;
                    lastY = newY;
                    repaint();
                }
            }
        });
    }
    
    private void setFreehandMode() {
        freehandMode = true;
        setCurrentShape("freehand");
    }


    

    public void setCurrentShape(String shape) {
        currentShape = shape;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        // Draw the shapes
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    
        if (freehandMode && isDrawing) {
            if (currentShape.equals("freehand")) {
                Graphics2D g2 = (Graphics2D) g;
                g2.drawLine(lastX, lastY, startX, startY);
                startX = lastX;
                startY = lastY;
            } else if (currentShape.equals("rectangle")) {
                g.drawRect(startX, startY, lastX - startX, lastY - startY);
            } else if (currentShape.equals("square")) {
                int size = Math.min(lastX - startX, lastY - startY);
                g.drawRect(startX, startY, size, size);
            } else if (currentShape.equals("circle")) {
                int width = lastX - startX;
                int height = lastY - startY;
                int diameter = Math.min(width, height);
                g.drawOval(startX, startY, diameter, diameter);
            } else if (currentShape.equals("oval")) {
                g.drawOval(startX, startY, lastX - startX, lastY - startY);
            } else if (currentShape.equals("line")) {
                g.drawLine(startX, startY, lastX, lastY);
            }
        }
    }
    
    private static class Shape {
        int x1, y1, x2, y2;

        Shape(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        void draw(Graphics g) {
            // Implement drawing logic in the subtypes
        }
    }

    private static class Rectangle extends Shape {
        Rectangle(int x1, int y1, int x2, int y2) {
            super(x1, y1, x2, y2);
        }

        @Override
        void draw(Graphics g) {
            g.drawRect(x1, y1, x2 - x1, y2 - y1);
        }
    }

    private static class Square extends Shape {
        Square(int x1, int y1, int x2, int y2) {
            super(x1, y1, x2, y2);
        }

        @Override
        void draw(Graphics g) {
            int size = Math.min(x2 - x1, y2 - y1);
            g.drawRect(x1, y1, size, size);
        }
    }

    private static class Circle extends Shape {
        Circle(int x1, int y1, int x2, int y2) {
            super(x1, y1, x2, y2);
        }

        @Override
        void draw(Graphics g) {
            int width = x2 - x1;
            int height = y2 - y1;
            int diameter = Math.min(width, height);
            g.drawOval(x1, y1, diameter, diameter);
        }
    }

    private static class Oval extends Shape {
        Oval(int x1, int y1, int x2, int y2) {
            super(x1, y1, x2, y2);
        }

        @Override
        void draw(Graphics g) {
            g.drawOval(x1, y1, x2 - x1, y2 - y1);
        }
    }

    private static class Line extends Shape {
        Line(int x1, int y1, int x2, int y2) {
            super(x1, y1, x2, y2);
        }

        @Override
        void draw(Graphics g) {
            g.drawLine(x1, y1, x2, y2);
        }
    }
}

