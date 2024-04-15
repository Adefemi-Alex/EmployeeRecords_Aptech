import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class EmployeeRecords extends JFrame {
    String filePath = "src/Database.txt";
    Date currentDate = new Date();

    JButton Add, Delete, Edit, Save, Count, Search;
    JTextField empNumField, firstnameField, DepartmentField;
    String[] columns;
    String[][] data;
    ArrayList <String[][]> dataArr = new ArrayList<>();
    int fontSize;

    Boolean isEditable = false;

    DefaultTableModel model;
    private JTable dataTable;
    private JScrollPane scrollPane;

    public EmployeeRecords() {
        setTitle("Admin Page");
        setSize(1350, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        fetch();

        Font serifFont = new Font("Dialog", Font.BOLD, 16);

        JPanel navbar = new JPanel();
        JLabel Greetings = new JLabel("Hello Admin");
        JLabel Title = new JLabel("Employee Records");
        JLabel dateTime = new JLabel(getDate() + " " + getTime());
        Title.setFont(serifFont);
        Greetings.setFont(serifFont);
        dateTime.setFont(serifFont);
        Title.setForeground(Color.WHITE);
        Greetings.setForeground(Color.WHITE);
        dateTime.setForeground(Color.WHITE);


        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the time label
                dateTime.setText(getDate() + " " + getTime());
            }
        });

        // Start the timer
        timer.start();



        Title.setForeground(Color.WHITE);
        Greetings.setForeground(Color.WHITE);
        dateTime.setForeground(Color.WHITE);
        Title.setHorizontalAlignment(JLabel.CENTER);

        navbar.setBackground(new Color(0x071559));
        navbar.setLayout(new BorderLayout());
        navbar.setBorder(new EmptyBorder(30, 20, 30, 20));
        navbar.add(Greetings, BorderLayout.WEST);
        navbar.add(Title, BorderLayout.CENTER);
        navbar.add(dateTime, BorderLayout.EAST);
        add(navbar, BorderLayout.NORTH);

        JPanel Body = new JPanel();
        Body.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel Sidebar = new JPanel();
        Sidebar.setLayout(null);
        Sidebar.setPreferredSize(new Dimension(200, 600));
        Sidebar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

//        Sidebar.setBorder();
        Save = new JButton("Save");
        Add = new JButton("Add");
        Delete = new JButton("Delete");
        Edit = new JButton("Edit Mode");
        Count = new JButton("Sum of Records");

        Save.setBounds(30, 80, 130, 40);
        Add.setBounds(30, 160, 130, 40);
        Delete.setBounds(30, 240, 130, 40);
        Edit.setBounds(30, 320, 130, 40);
        Count.setBounds(30, 400, 130, 40);

        Save.setBackground(new Color(8, 32, 107));
        Add.setBackground(new Color(8, 32, 107));
        Delete.setBackground(new Color(8, 32, 107));
        Edit.setBackground(new Color(8, 32, 107));
        Count.setBackground(new Color(8, 32, 107));

        Save.setForeground(Color.WHITE);
        Add.setForeground(Color.WHITE);
        Delete.setForeground(Color.WHITE);
        Edit.setForeground(Color.WHITE);
        Count.setForeground(Color.WHITE);

        Save.setFocusPainted(false);
        Add.setFocusPainted(false);
        Delete.setFocusPainted(false);
        Edit.setFocusPainted(false);
        Count.setFocusPainted(false);

//        Save.setFont(serifFont);
//        Add.setFont(serifFont);
//        Delete.setFont(serifFont);
//        Edit.setFont(serifFont);
//        Count.setFont(serifFont);


        Edit.addActionListener(e->{
            editData();
        });
        Delete.addActionListener(e->{
            deleteData();
        });

        Sidebar.add(Save);
        Sidebar.add(Add);
        Sidebar.add(Delete);
        Sidebar.add(Edit);
        Sidebar.add(Count);

        JPanel MainSection = new JPanel();
        MainSection.setLayout(new BorderLayout());

        JPanel SearchPanel = new JPanel();
        SearchPanel.setLayout(new FlowLayout());

        empNumField = new JTextField("Employee Id", 15);
        firstnameField = new JTextField("Firstname and Lastname", 20);
        DepartmentField = new JTextField("Department", 15);
        Search = new JButton("Search");
        Search.setBackground(new Color(8, 32, 107));
        Search.setForeground(Color.WHITE);

        SearchPanel.add(empNumField);
        SearchPanel.add(firstnameField);
        SearchPanel.add(DepartmentField);
        SearchPanel.add(Search);



        MainSection.add(SearchPanel, BorderLayout.NORTH);


        JPanel tablePanel = new JPanel(new BorderLayout());
//        tablePanel.setVisible(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tablePanel.setPreferredSize(new Dimension(1100, 500));
        columns = new String[]{
                "Employee number",
                "First Name",
                "Last Name",
                "Age",
                "Basic Salary",
                "Department",
                "Date of Joining",
                "Address / City",
                "Phone Number"
        };
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditable;
            }
        };
        dataTable = new JTable(model);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataTable.setFillsViewportHeight(true);
        JLabel noItems =new JLabel("No items found");
        noItems.setVisible(false);

        // Customizing the table header background color
        JTableHeader header = dataTable.getTableHeader();
        header.setBackground(new Color(8, 32, 107));
        header.setBorder(new EmptyBorder(30, 0, 30, 0));
        header.setForeground(Color.WHITE);

        header.setFont(serifFont);

        // Creating a JScrollPane to display the table
        scrollPane = new JScrollPane(dataTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        MainSection.add(tablePanel, BorderLayout.CENTER);
//        MainSection.add(noItems, BorderLayout.CENTER);

        Body.add(Sidebar);
        Body.add(MainSection);


        add(Body, BorderLayout.CENTER);

        setVisible(true);
    }

    public String getDate() {
        // Format for date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return dateFormat.format(currentDate);
    }

    public String getTime() {
        // Get the current time
        Date currentTime = new Date();

        // Format for time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(currentTime);
    }


    public static void main(String[] args) {
        new EmployeeRecords();
    }

    public ArrayList<String[][]> fetch() {
        String filePath = "src/Database.txt";
        ArrayList<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line of data here
                String[] record = line.split(",");
                rows.add(record);
            }

            // Initialize the data array based on the number of rows and columns
            int rowCount = rows.size();
            int columnCount = rows.isEmpty() ? 0 : rows.get(0).length;
            data = new String[rowCount][columnCount];

            // Populate the data array with values from the ArrayList
            for (int row = 0; row < rowCount; row++) {
                String[] record = rows.get(row);
                for (int col = 0; col < columnCount; col++) {
                    data[row][col] = record[col];
                }
            }

            // Add data array to dataArr
            dataArr.add(data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    public void editData() {
        isEditable = !isEditable;
        if (isEditable) {
            Edit.setText("Readonly");
        } else {
            Edit.setText("Edit Mode");
        }
//                valid = true;
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                model.fireTableCellUpdated(row, col); // Refresh the table cells
            }
        }
    }

    public void deleteData(){
        int[] selectedRows = dataTable.getSelectedRows();

        if (selectedRows.length > 0) {
            int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete selected rows?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                deleteRowsFromDatabase(selectedRows);
                // Refresh the table after deletion
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select rows to delete");
        }
    }

    private void deleteRowsFromDatabase(int[] selectedRows) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            try (FileWriter fileWriter = new FileWriter(filePath);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Process each line of data here
                    String[] record = line.split(",");
                    for (int i : selectedRows) {
                        if(!dataArr.isEmpty()){
                        String[][] arrLoop =dataArr.get(0);
                        String employeeNumber = arrLoop[i][0];
                        if (Objects.equals(employeeNumber, record[0])) {
                            System.out.println("hi");
//                            dataArr.remove(i);
//                            System.out.println(dataArr[f]);
                        }

                        }
                    }
                }
//                if (found) {
//                    System.out.println("Employee deleted successfully.");
//                } else {
//                    System.out.println("Employee with Employee Number " + employeeNumberToDelete + " not found.");
//                }
            } catch (IOException e) {
                System.out.println("Error occurred while deleting employee: " + e.getMessage());
            }

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
            JOptionPane.showMessageDialog(null, "Selected rows deleted successfully");

    }

    private void refreshTable() {
        //model.setRowCount(0); // Clear the table model
        dataArr = fetch(); // Fetch data again
       // String[][] arrLoop = dataArr.get(0);
       // for (String[] row : arrLoop) {
        //    model.addRow(row); // Add the updated data to the table model
        //}
    }

}
