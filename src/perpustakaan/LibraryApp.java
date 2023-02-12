package perpustakaan;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class LibraryApp {

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel authorLabel;
    private JTextField authorTextField;
    private JLabel publisherLabel;
    private JTextField publisherTextField;
    private JButton createButton;
    private JButton readButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/perpustakaan";
    static final String USER = "postgres";
    static final String PASS = "root";

    public LibraryApp() {
        prepareGUI();
    }

    public static void main(String[] args) {
        LibraryApp libraryApp = new LibraryApp();
        libraryApp.showEventDemo();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Library Application");
        mainFrame.setSize(650, 400);
        mainFrame.setLayout(new BorderLayout());  
        
        headerLabel = new JLabel("Add Books in Library", JLabel.CENTER);
        
        nameLabel = new JLabel("Name: ");
        nameTextField = new JTextField();
        authorLabel = new JLabel("Author: ");
        authorTextField = new JTextField();
        publisherLabel = new JLabel("Publisher: ");
        publisherTextField = new JTextField();
        createButton = new JButton("Add");
        readButton = new JButton("Refresh");
        bookTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Author", "Publisher"}, 0);
        bookTable.setModel(tableModel);
        
    }

    private void showEventDemo() {
        JPanel controlPanel = new JPanel();
        JPanel tablePanel = new JPanel();
        
        controlPanel.setLayout(new GridLayout(0, 1));

        mainFrame.add(headerLabel, BorderLayout.NORTH);
        mainFrame.add(controlPanel, BorderLayout.WEST);
        mainFrame.add(tablePanel, BorderLayout.CENTER);

        controlPanel.add(nameLabel);
        controlPanel.add(nameTextField);
        controlPanel.add(authorLabel);
        controlPanel.add(authorTextField);
        controlPanel.add(publisherLabel);
        controlPanel.add(publisherTextField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(readButton);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        tablePanel.add(tableScrollPane);

        mainFrame.setVisible(true);

        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Add button click event
                // Example: insert data into database
                String name = nameTextField.getText();
                String author = authorTextField.getText();
                String publisher = publisherTextField.getText();

                Connection connection = null;
                Statement statement = null;
                try {
                    // Connect to database and insert data
                    connection = DriverManager.getConnection(DB_URL, USER, PASS);
                    statement = connection.createStatement();
                    String sql = "INSERT INTO book (name, author, publisher) VALUES('" + name + "', '" + author + "', '" + publisher + "')";
                    statement.executeUpdate(sql);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        statement.close();
                        connection.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                nameTextField.setText("");
                authorTextField.setText("");
                publisherTextField.setText("");
                
                readButton.doClick();        
            }
        });

        readButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Refresh button click event
                // Example: retrieve data from database and display in table
                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;
                try {
                    // Connect to database and retrieve data
                    connection = DriverManager.getConnection(DB_URL, USER, PASS);
                    statement = connection.createStatement();
                    String sql = "SELECT * FROM book";
                    resultSet = statement.executeQuery(sql);

                    // Clear previous data in table
                    tableModel.setRowCount(0);

                    // Populate data in table
                    List<Book> books = new ArrayList<>();
                    while (resultSet.next()) {
                        Book book = new Book();
                        book.setId(resultSet.getString("id"));
                        book.setName(resultSet.getString("name"));
                        book.setAuthor(resultSet.getString("author"));
                        book.setPublisher(resultSet.getString("publisher"));
                        books.add(book);
                    }
                    for (Book book : books) {
                        Object[] rowData = new Object[]{book.getId(), book.getName(), book.getAuthor(), book.getPublisher()};
                        tableModel.addRow(rowData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        resultSet.close();
                        statement.close();
                        connection.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
