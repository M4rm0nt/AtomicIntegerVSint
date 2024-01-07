import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Bibliotheksverwaltung {
    private static final String FRAME_TITLE = "Bibliotheksverwaltungs-System";
    private static final String[] COLUMN_NAMES = {"Titel", "Autor", "Ausgeliehen"};
    private JFrame frame;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private List<Book> booksList;
    private static final AtomicInteger bookIdGenerator = new AtomicInteger(0);

    private static class Book {
        int id;
        String title;
        String author;
        boolean isBorrowed;

        Book(int id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.isBorrowed = false;
        }
    }

    public Bibliotheksverwaltung() {
        booksList = Collections.synchronizedList(new ArrayList<>());
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        frame.add(createBooksPanel(), BorderLayout.CENTER);
        frame.add(createButtonPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addBookButton = new JButton("Buch hinzufügen");
        addBookButton.addActionListener(this::addBook);

        JButton borrowBookButton = new JButton("Buch ausleihen");
        borrowBookButton.addActionListener(this::borrowBook);

        JButton deleteBookButton = new JButton("Buch löschen");
        deleteBookButton.addActionListener(this::deleteBook);

        buttonPanel.add(addBookButton);
        buttonPanel.add(borrowBookButton);
        buttonPanel.add(deleteBookButton);

        return buttonPanel;
    }

    private JScrollPane createBooksPanel() {
        booksTableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(booksTableModel);
        return new JScrollPane(booksTable);
    }

    private void addBook(ActionEvent event) {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Titel:"));
        panel.add(titleField);
        panel.add(new JLabel("Autor:"));
        panel.add(authorField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Neues Buch hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String author = authorField.getText();
            if (!title.isEmpty() && !author.isEmpty()) {
                int bookId = bookIdGenerator.incrementAndGet();
                Book newBook = new Book(bookId, title, author);
                synchronized (booksList) {
                    booksList.add(newBook);
                }
                booksTableModel.addRow(new Object[] { title, author, false });
            } else {
                JOptionPane.showMessageDialog(frame, "Bitte füllen Sie alle Felder aus.", "Eingabefehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void borrowBook(ActionEvent event) {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            synchronized (booksList) {
                Book selectedBook = booksList.get(selectedRow);
                if (!selectedBook.isBorrowed) {
                    selectedBook.isBorrowed = true;
                    booksTableModel.setValueAt(true, selectedRow, 2);
                    JOptionPane.showMessageDialog(frame, "Buch mit ID " + selectedBook.id + " ausgeliehen.", "Buch ausgeliehen", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Dieses Buch ist bereits ausgeliehen.", "Buch nicht verfügbar", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Bitte wählen Sie ein Buch aus.", "Auswahl erforderlich", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteBook(ActionEvent event) {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            synchronized (booksList) {
                booksList.remove(selectedRow);
            }
            booksTableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(frame, "Bitte wählen Sie ein Buch zum Löschen aus.", "Auswahl erforderlich", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Bibliotheksverwaltung::new);
    }
}
