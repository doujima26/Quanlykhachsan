package vn.viettuts.qlsv.view;

import vn.viettuts.qlsv.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HotelBooking extends JFrame {

    private JTextField nameField, idField, depositField, priceField, totalPriceField;
    private JSpinner checkinDate, checkoutDate, quantitySpinner;
    private JComboBox<String> roomTypeComboBox, availableRoomTypeComboBox, roomNumberComboBox;
    private JTable availableRoomsTable, bookedRoomsTable;
    private DefaultTableModel availableRoomsTableModel, bookedRoomsTableModel;

    private static final String ROOMS_FILE_PATH = "rooms.xml";
    private static final String BOOKINGS_FILE_PATH = "bookings.xml";

    public HotelBooking() {
        setTitle("Đặt phòng khách sạn");
        setSize(9000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thông tin khách hàng
        JPanel customerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        customerPanel.add(new JLabel("Họ và tên:"));
        nameField = new JTextField();
        customerPanel.add(nameField);

        customerPanel.add(new JLabel("Ngày đặt:"));
        checkinDate = new JSpinner(new SpinnerDateModel());
        customerPanel.add(checkinDate);

        customerPanel.add(new JLabel("Ngày xuất:"));
        checkoutDate = new JSpinner(new SpinnerDateModel());
        customerPanel.add(checkoutDate);

        customerPanel.add(new JLabel("Đặt cọc:"));
        depositField = new JTextField();
        depositField.setEditable(false);
        customerPanel.add(depositField);

        customerPanel.add(new JLabel("CMTND:"));
        idField = new JTextField();
        customerPanel.add(idField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(customerPanel, gbc);

        // Thông tin phòng
        JPanel roomPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phòng"));

        roomPanel.add(new JLabel("Loại phòng:"));
        String[] roomTypes = {"Phòng đơn", "Phòng đôi", "Phòng VIP"};
        roomTypeComboBox = new JComboBox<>(roomTypes);
        roomTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePriceAndDeposit();
            }
        });
        roomPanel.add(roomTypeComboBox);

        roomPanel.add(new JLabel("Đơn giá:"));
        priceField = new JTextField();
        priceField.setEditable(false);
        roomPanel.add(priceField);

        roomPanel.add(new JLabel("Số lượng:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        quantitySpinner.addChangeListener(e -> updatePriceAndDeposit());
        roomPanel.add(quantitySpinner);

        roomPanel.add(new JLabel("Tổng giá:"));
        totalPriceField = new JTextField();
        totalPriceField.setEditable(false);
        roomPanel.add(totalPriceField);

        roomPanel.add(new JLabel("Số phòng:"));
        roomNumberComboBox = new JComboBox<>();
        roomPanel.add(roomNumberComboBox);

        gbc.gridy = 1;
        panel.add(roomPanel, gbc);

        // Chọn phòng trống
        JPanel availableRoomsPanel = new JPanel(new BorderLayout());
        availableRoomsPanel.setBorder(BorderFactory.createTitledBorder("Chọn phòng trống"));

        String[] availableRoomColumnNames = {"Phòng", "Loại phòng", "Đơn giá", "Trạng thái"};
        availableRoomsTableModel = new DefaultTableModel(availableRoomColumnNames, 0);
        availableRoomsTable = new JTable(availableRoomsTableModel);
        JScrollPane availableRoomsScrollPane = new JScrollPane(availableRoomsTable);
        availableRoomsPanel.add(availableRoomsScrollPane, BorderLayout.CENTER);

        // Thêm phòng trống
        JPanel addRoomPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        addRoomPanel.setBorder(BorderFactory.createTitledBorder("Thêm phòng trống"));

        addRoomPanel.add(new JLabel("Phòng:"));
        JTextField newRoomNumberField = new JTextField();
        addRoomPanel.add(newRoomNumberField);

        addRoomPanel.add(new JLabel("Loại phòng:"));
        availableRoomTypeComboBox = new JComboBox<>(roomTypes);
        addRoomPanel.add(availableRoomTypeComboBox);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(addRoomPanel, gbc);

        // Nút thêm phòng
        JButton addRoomButton = new JButton("Thêm phòng");
        addRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRoom(newRoomNumberField.getText(), (String) availableRoomTypeComboBox.getSelectedItem());
            }
        });
        gbc.gridx = 1;
        panel.add(addRoomButton, gbc);

        // Nút sửa phòng
        JButton editRoomButton = new JButton("Sửa phòng");
        editRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editRoom();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(editRoomButton, gbc);

        // Nút xóa phòng
        JButton deleteRoomButton = new JButton("Xóa phòng");
        deleteRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRoom();
            }
        });
        gbc.gridx = 1;
        panel.add(deleteRoomButton, gbc);

        // Bảng phòng trống
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(availableRoomsPanel, gbc);

        // Nút đặt phòng
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton bookButton = new JButton("Đặt phòng");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookRoom();
            }
        });
        buttonsPanel.add(bookButton);

        // Nút trả phòng
        JButton returnButton = new JButton("Trả phòng");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnRoom();
            }

            private void returnRoom() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
        buttonsPanel.add(returnButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonsPanel, gbc);

        // Bảng phòng đã đặt
        JPanel bookedRoomsPanel = new JPanel(new BorderLayout());
        bookedRoomsPanel.setBorder(BorderFactory.createTitledBorder("Phòng đã đặt"));

        String[] bookedRoomColumnNames = {"Họ và tên", "Ngày đặt", "Ngày xuất", "Loại phòng", "Số lượng", "Đơn giá", "Tổng giá", "Đặt cọc", "Số phòng"};
        bookedRoomsTableModel = new DefaultTableModel(bookedRoomColumnNames, 0);
        bookedRoomsTable = new JTable(bookedRoomsTableModel);
        JScrollPane bookedRoomsScrollPane = new JScrollPane(bookedRoomsTable);
        bookedRoomsPanel.add(bookedRoomsScrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(bookedRoomsPanel, gbc);

        add(panel);
        updatePriceAndDeposit();  // Cập nhật giá và tiền đặt cọc khi khởi tạo giao diện
        loadRoomsFromDatabase();  // Load available rooms from database
        loadBookingsFromDatabase();  // Load bookings from database
    }

    private void updatePriceAndDeposit() {
        String selectedRoomType = (String) roomTypeComboBox.getSelectedItem();
        int price = 0;
        if (selectedRoomType != null) {
            switch (selectedRoomType) {
                case "Phòng đơn":
                    price = 500000;
                    break;
                case "Phòng đôi":
                    price = 800000;
                    break;
                case "Phòng VIP":
                    price = 1500000;
                    break;
            }
        }
        priceField.setText(String.valueOf(price));

        int quantity = (int) quantitySpinner.getValue();
        int totalPrice = price * quantity;
        totalPriceField.setText(String.valueOf(totalPrice));

        int deposit = totalPrice / 2;  // Giả sử tiền đặt cọc là 50% tổng giá
        depositField.setText(String.valueOf(deposit));
    }

    private void loadRoomsFromDatabase() {
        Document doc = XMLUtil.readXMLFile(ROOMS_FILE_PATH);
        if (doc != null) {
            Element rootElement = doc.getDocumentElement();
            List<Element> roomElements = XMLUtil.getElementsByTagName(rootElement, "room");

            for (Element roomElement : roomElements) {
                String roomNumber = XMLUtil.getElementValue(roomElement, "number");
                String roomType = XMLUtil.getElementValue(roomElement, "type");
                String roomPrice = XMLUtil.getElementValue(roomElement, "price");
                String roomStatus = XMLUtil.getElementValue(roomElement, "status");

                availableRoomsTableModel.addRow(new Object[]{roomNumber, roomType, roomPrice, roomStatus});
                roomNumberComboBox.addItem(roomNumber);  // Thêm số phòng vào combobox
            }
        }
    }

    private void loadBookingsFromDatabase() {
        Document doc = XMLUtil.readXMLFile(BOOKINGS_FILE_PATH);
        if (doc != null) {
            Element rootElement = doc.getDocumentElement();
            List<Element> bookingElements = XMLUtil.getElementsByTagName(rootElement, "booking");

            for (Element bookingElement : bookingElements) {
                String customerName = XMLUtil.getElementValue(bookingElement, "name");
                String checkinDate = XMLUtil.getElementValue(bookingElement, "checkinDate");
                String checkoutDate = XMLUtil.getElementValue(bookingElement, "checkoutDate");
                String roomType = XMLUtil.getElementValue(bookingElement, "roomType");
                String quantity = XMLUtil.getElementValue(bookingElement, "quantity");
                String price = XMLUtil.getElementValue(bookingElement, "price");
                String totalPrice = XMLUtil.getElementValue(bookingElement, "totalPrice");
                String deposit = XMLUtil.getElementValue(bookingElement, "deposit");
                String roomNumber = XMLUtil.getElementValue(bookingElement, "roomNumber");

                bookedRoomsTableModel.addRow(new Object[]{customerName, checkinDate, checkoutDate, roomType, quantity, price, totalPrice, deposit, roomNumber});
            }
        }
    }

    private void addRoom(String roomNumber, String roomType) {
        int price = 0;
        switch (roomType) {
            case "Phòng đơn":
                price = 500000;
                break;
            case "Phòng đôi":
                price = 800000;
                break;
            case "Phòng VIP":
                price = 1500000;
                break;
        }
        String status = "Trống";

        // Add to table
        availableRoomsTableModel.addRow(new Object[]{roomNumber, roomType, price, status});

        // Save to database
        Document doc = XMLUtil.readXMLFile(ROOMS_FILE_PATH);
        if (doc != null) {
            Element rootElement = doc.getDocumentElement();
            Element roomElement = doc.createElement("room");

            XMLUtil.createChildElement(doc, roomElement, "number", roomNumber);
            XMLUtil.createChildElement(doc, roomElement, "type", roomType);
            XMLUtil.createChildElement(doc, roomElement, "price", String.valueOf(price));
            XMLUtil.createChildElement(doc, roomElement, "status", status);

            rootElement.appendChild(roomElement);
            XMLUtil.writeXMLFile(doc, ROOMS_FILE_PATH);
        }
    }

    private void editRoom() {
        int selectedRow = availableRoomsTable.getSelectedRow();
        if (selectedRow != -1) {
            String roomNumber = (String) availableRoomsTableModel.getValueAt(selectedRow, 0);
            String roomType = (String) availableRoomsTableModel.getValueAt(selectedRow, 1);

            JTextField roomNumberField = new JTextField(roomNumber);
            JComboBox<String> roomTypeComboBox = new JComboBox<>(new String[]{"Phòng đơn", "Phòng đôi", "Phòng VIP"});
            roomTypeComboBox.setSelectedItem(roomType);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Số phòng:"));
            panel.add(roomNumberField);
            panel.add(new JLabel("Loại phòng:"));
            panel.add(roomTypeComboBox);

            int result = JOptionPane.showConfirmDialog(null, panel, "Sửa phòng", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String newRoomNumber = roomNumberField.getText();
                String newRoomType = (String) roomTypeComboBox.getSelectedItem();
                int price = 0;
                switch (newRoomType) {
                    case "Phòng đơn":
                        price = 500000;
                        break;
                    case "Phòng đôi":
                        price = 800000;
                        break;
                    case "Phòng VIP":
                        price = 1500000;
                        break;
                }

                availableRoomsTableModel.setValueAt(newRoomNumber, selectedRow, 0);
                availableRoomsTableModel.setValueAt(newRoomType, selectedRow, 1);
                availableRoomsTableModel.setValueAt(price, selectedRow, 2);

                // Save changes to database
                Document doc = XMLUtil.readXMLFile(ROOMS_FILE_PATH);
                if (doc != null) {
                    Element rootElement = doc.getDocumentElement();
                    List<Element> roomElements = XMLUtil.getElementsByTagName(rootElement, "room");

                    for (Element roomElement : roomElements) {
                        String xmlRoomNumber = XMLUtil.getElementValue(roomElement, "number");
                        if (xmlRoomNumber.equals(roomNumber)) {
                            XMLUtil.setElementValue(roomElement, "number", newRoomNumber);
                            XMLUtil.setElementValue(roomElement, "type", newRoomType);
                            XMLUtil.setElementValue(roomElement, "price", String.valueOf(price));
                            break;
                        }
                    }
                    XMLUtil.writeXMLFile(doc, ROOMS_FILE_PATH);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteRoom() {
        int selectedRow = availableRoomsTable.getSelectedRow();
        if (selectedRow != -1) {
            String roomNumber = (String) availableRoomsTableModel.getValueAt(selectedRow, 0);

            int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng này?", "Xóa phòng", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                availableRoomsTableModel.removeRow(selectedRow);

                // Remove from database
                Document doc = XMLUtil.readXMLFile(ROOMS_FILE_PATH);
                if (doc != null) {
                    Element rootElement = doc.getDocumentElement();
                    List<Element> roomElements = XMLUtil.getElementsByTagName(rootElement, "room");

                    for (Element roomElement : roomElements) {
                        String xmlRoomNumber = XMLUtil.getElementValue(roomElement, "number");
                        if (xmlRoomNumber.equals(roomNumber)) {
                            rootElement.removeChild(roomElement);
                            break;
                        }
                    }
                    XMLUtil.writeXMLFile(doc, ROOMS_FILE_PATH);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void bookRoom() {
        String customerName = nameField.getText();
        Date checkin = (Date) checkinDate.getValue();
        Date checkout = (Date) checkoutDate.getValue();
        String roomType = (String) roomTypeComboBox.getSelectedItem();
        int quantity = (int) quantitySpinner.getValue();
        int price = Integer.parseInt(priceField.getText());
        int totalPrice = Integer.parseInt(totalPriceField.getText());
        int deposit = Integer.parseInt(depositField.getText());
        String id = idField.getText();
        String roomNumber = (String) roomNumberComboBox.getSelectedItem();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String checkinDateStr = sdf.format(checkin);
        String checkoutDateStr = sdf.format(checkout);

        bookedRoomsTableModel.addRow(new Object[]{customerName, checkinDateStr, checkoutDateStr, roomType, quantity, price, totalPrice, deposit, roomNumber});

        // Save to database
        Document doc = XMLUtil.readXMLFile(BOOKINGS_FILE_PATH);
        if (doc != null) {
            Element rootElement = doc.getDocumentElement();
            Element bookingElement = doc.createElement("booking");

            XMLUtil.createChildElement(doc, bookingElement, "name", customerName);
            XMLUtil.createChildElement(doc, bookingElement, "checkinDate", checkinDateStr);
            XMLUtil.createChildElement(doc, bookingElement, "checkoutDate", checkoutDateStr);
            XMLUtil.createChildElement(doc, bookingElement, "roomType", roomType);
            XMLUtil.createChildElement(doc, bookingElement, "quantity", String.valueOf(quantity));
            XMLUtil.createChildElement(doc, bookingElement, "price", String.valueOf(price));
            XMLUtil.createChildElement(doc, bookingElement, "totalPrice", String.valueOf(totalPrice));
            XMLUtil.createChildElement(doc, bookingElement, "deposit", String.valueOf(deposit));
            XMLUtil.createChildElement(doc, bookingElement, "roomNumber", roomNumber);

            rootElement.appendChild(bookingElement);
            XMLUtil.writeXMLFile(doc, BOOKINGS_FILE_PATH);

            // Cập nhật trạng thái phòng
            updateRoomStatus(roomNumber, "Đã đặt");
        }
    }

    private void updateRoomStatus(String roomNumber, String status) {
        // Update status in table
        for (int i = 0; i < availableRoomsTableModel.getRowCount(); i++) {
            if (availableRoomsTableModel.getValueAt(i, 0).equals(roomNumber)) {
                availableRoomsTableModel.setValueAt(status, i, 3);
                break;
            }
        }

        // Update status in database
        Document doc = XMLUtil.readXMLFile(ROOMS_FILE_PATH);
        if (doc != null) {
            Element rootElement = doc.getDocumentElement();
            List<Element> roomElements = XMLUtil.getElementsByTagName(rootElement, "room");

            for (Element roomElement : roomElements) {
                if (XMLUtil.getElementValue(roomElement, "number").equals(roomNumber)) {
                    XMLUtil.setElementValue(roomElement, "status", status);
                    break;
                }
            }
            XMLUtil.writeXMLFile(doc, ROOMS_FILE_PATH);
        }
    }

    private void cancelBooking() {
        int selectedRow = bookedRoomsTable.getSelectedRow();
        if (selectedRow != -1) {
            String roomNumber = (String) bookedRoomsTableModel.getValueAt(selectedRow, 8);

            bookedRoomsTableModel.removeRow(selectedRow);

            // Remove from database
            Document doc = XMLUtil.readXMLFile(BOOKINGS_FILE_PATH);
            if (doc != null) {
                Element rootElement = doc.getDocumentElement();
                List<Element> bookingElements = XMLUtil.getElementsByTagName(rootElement, "booking");

                for (Element bookingElement : bookingElements) {
                    String xmlRoomNumber = XMLUtil.getElementValue(bookingElement, "roomNumber");
                    if (xmlRoomNumber.equals(roomNumber)) {
                        rootElement.removeChild(bookingElement);
                        break;
                    }
                }
                XMLUtil.writeXMLFile(doc, BOOKINGS_FILE_PATH);

                // Cập nhật trạng thái phòng
                updateRoomStatus(roomNumber, "Trống");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt phòng để hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           HotelBooking app= new HotelBooking();
            app.setVisible(true);
        });
    }
}

