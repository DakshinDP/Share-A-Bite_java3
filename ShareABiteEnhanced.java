import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ShareABiteEnhanced {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel, registerPanel, postPanel, browsePanel, historyPanel, notificationsPanel;
    private JTextField foodNameField, locationField, contactField, servingsField;
    private JTextField loginUsernameField, loginPasswordField;
    private JTextField registerUsernameField, registerPasswordField, registerEmailField;
    private JTextArea descriptionArea;
    private JList<FoodItem> foodList;
    private JList<HistoryItem> historyList;
    private JList<NotificationItem> notificationList;
    private DefaultListModel<FoodItem> foodListModel;
    private DefaultListModel<HistoryItem> historyListModel;
    private DefaultListModel<NotificationItem> notificationListModel;
    private ArrayList<FoodItem> foodItems = new ArrayList<>();
    private JLabel imagePreview;
    private File selectedImageFile;

    // Current user information
    private int currentUserId = -1;
    private String currentUsername = "";

    // Database credentials
    private final String DB_URL = "jdbc:mysql://localhost:3306/shareabite_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "0000";

    public ShareABiteEnhanced() {
        frame = new JFrame("Share a Bite - Food Sharing Community");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        createLoginPanel();
        frame.add(loginPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void showMainApplication() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 87, 34));
        header.setPreferredSize(new Dimension(1000, 80));

        JLabel title = new JLabel("Share a Bite", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("Logged in as: " + currentUsername, SwingConstants.RIGHT);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        header.add(userLabel, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        createPostPanel();
        createBrowsePanel();
        createHistoryPanel();
        createNotificationsPanel();

        tabbedPane.addTab("Donate Food", postPanel);
        tabbedPane.addTab("Browse Food", browsePanel);
        tabbedPane.addTab("My History", historyPanel);
        tabbedPane.addTab("Notifications", notificationsPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        loadFoodItemsFromDB();
        loadUserHistory();
        loadNotifications();
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginTitle = new JLabel("Login to ShareABite", SwingConstants.CENTER);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(loginTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        loginUsernameField = new JTextField(20);
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(20);
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.GREEN);
        loginButton.addActionListener(e -> attemptLogin());
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 4;
        JButton registerButton = new JButton("Don't have an account? Register");
        registerButton.addActionListener(e -> showRegistrationPanel());
        loginPanel.add(registerButton, gbc);
    }

    private void showRegistrationPanel() {
        frame.getContentPane().removeAll();

        registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel registerTitle = new JLabel("Register for ShareABite", SwingConstants.CENTER);
        registerTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerPanel.add(registerTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        registerPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        registerUsernameField = new JTextField(20);
        registerPanel.add(registerUsernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        registerPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        registerPasswordField = new JPasswordField(20);
        registerPanel.add(registerPasswordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        registerPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        registerEmailField = new JTextField(20);
        registerPanel.add(registerEmailField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton registerSubmitButton = new JButton("Register");
        registerSubmitButton.setBackground(new Color(76, 175, 80));
        registerSubmitButton.setForeground(Color.GREEN);
        registerSubmitButton.addActionListener(e -> attemptRegistration());
        registerPanel.add(registerSubmitButton, gbc);

        gbc.gridy = 5;
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(loginPanel, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
        registerPanel.add(backButton, gbc);

        frame.add(registerPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void attemptLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // In a real app, you should hash passwords
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUsername = rs.getString("username");
                showMainApplication();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error during login", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptRegistration() {
        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText().trim();
        String email = registerEmailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Check if username already exists
            String checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, username);
            checkPs.setString(2, email);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Username or email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user
            String insertSql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertPs.setString(1, username);
            insertPs.setString(2, password); // In a real app, hash this password
            insertPs.setString(3, email);
            insertPs.executeUpdate();

            ResultSet generatedKeys = insertPs.getGeneratedKeys();
            if (generatedKeys.next()) {
                currentUserId = generatedKeys.getInt(1);
                currentUsername = username;
                showMainApplication();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error during registration", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createPostPanel() {
        postPanel = new JPanel(new BorderLayout(10, 10));
        postPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("Food Name:"));
        foodNameField = new JTextField();
        formPanel.add(foodNameField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane);

        formPanel.add(new JLabel("Number of Servings:"));
        servingsField = new JTextField();
        formPanel.add(servingsField);

        formPanel.add(new JLabel("Pickup Location:"));
        locationField = new JTextField();
        formPanel.add(locationField);

        formPanel.add(new JLabel("Contact Info:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        formPanel.add(new JLabel("Food Image:"));
        JButton uploadButton = new JButton("Upload Image");
        uploadButton.addActionListener(e -> uploadImage());
        formPanel.add(uploadButton);

        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(200, 200));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel imagePanel = new JPanel();
        imagePanel.add(imagePreview);
        formPanel.add(new JLabel(""));
        formPanel.add(imagePanel);

        postPanel.add(formPanel, BorderLayout.CENTER);

        JButton submitButton = new JButton("Share This Food");
        submitButton.setBackground(new Color(255, 87, 34));
        submitButton.setForeground(Color.ORANGE);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.addActionListener(e -> postFoodItem());
        postPanel.add(submitButton, BorderLayout.SOUTH);
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedImageFile);
                imagePreview.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void postFoodItem() {
        String name = foodNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String servings = servingsField.getText().trim();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || description.isEmpty() || servings.isEmpty() || location.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;
        ImageIcon icon = null;
        try {
            if (selectedImageFile != null) {
                BufferedImage img = ImageIO.read(selectedImageFile);
                icon = new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            }
        } catch (IOException ignored) {}

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO food_items (name, description, servings, location, contact, image_path, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, servings);
            ps.setString(4, location);
            ps.setString(5, contact);
            ps.setString(6, imagePath);
            ps.setInt(7, currentUserId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int foodId = rs.getInt(1);
                logUserHistory(conn, foodId, "Shared");
                FoodItem newItem = new FoodItem(foodId, name, description, servings, location, contact, icon, currentUserId, currentUsername);
                foodItems.add(newItem);
                foodListModel.addElement(newItem);
            }

            JOptionPane.showMessageDialog(frame, "Food item posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            tabbedPane.setSelectedIndex(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        foodNameField.setText("");
        descriptionArea.setText("");
        servingsField.setText("");
        locationField.setText("");
        contactField.setText("");
        imagePreview.setIcon(null);
        selectedImageFile = null;
    }

    private void createBrowsePanel() {
        browsePanel = new JPanel(new BorderLayout(10, 10));
        browsePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        foodListModel = new DefaultListModel<>();
        foodList = new JList<>(foodListModel);
        foodList.setCellRenderer(new FoodItemRenderer());
        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(foodList);
        browsePanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Food Details"));
        JPanel detailContent = new JPanel(new BorderLayout());
        JLabel detailImage = new JLabel();
        detailImage.setPreferredSize(new Dimension(200, 200));
        detailImage.setHorizontalAlignment(JLabel.CENTER);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailContent.add(detailImage, BorderLayout.WEST);
        detailContent.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        detailsPanel.add(detailContent, BorderLayout.CENTER);

        JButton reserveButton = new JButton("Reserve Food & Get Directions");
        reserveButton.setBackground(new Color(76, 175, 80));
        reserveButton.setForeground(Color.GREEN);
        reserveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        reserveButton.addActionListener(e -> {
            int index = foodList.getSelectedIndex();
            if (index != -1) {
                FoodItem item = foodItems.get(index);
                reserveFoodItem(item);
            }
        });
        detailsPanel.add(reserveButton, BorderLayout.SOUTH);
        browsePanel.add(detailsPanel, BorderLayout.SOUTH);

        foodList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = foodList.getSelectedIndex();
                if (index != -1) {
                    FoodItem item = foodItems.get(index);
                    detailsArea.setText("Food: " + item.getName() + "\n\n" +
                            "Description: " + item.getDescription() + "\n\n" +
                            "Servings: " + item.getServings() + "\n\n" +
                            "Location: " + item.getLocation() + "\n\n" +
                            "Contact: " + item.getContact() + "\n\n" +
                            "Posted by: " + item.getDonorUsername());
                    detailImage.setIcon(item.getImage());
                }
            }
        });

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> loadFoodItemsFromDB());
        browsePanel.add(refreshButton, BorderLayout.NORTH);
    }

    private void reserveFoodItem(FoodItem item) {
        if (item.getDonorId() == currentUserId) {
            JOptionPane.showMessageDialog(frame, "You cannot reserve your own food item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to reserve this food item?\n" + item.getName(),
                "Confirm Reservation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                // Create reservation
                String reserveSql = "INSERT INTO reservations (food_id, donor_id, receiver_id, status) VALUES (?, ?, ?, 'reserved')";
                PreparedStatement reservePs = conn.prepareStatement(reserveSql);
                reservePs.setInt(1, item.getId());
                reservePs.setInt(2, item.getDonorId());
                reservePs.setInt(3, currentUserId);
                reservePs.executeUpdate();

                // Create notification for donor
                String notificationMsg = currentUsername + " has reserved your food item: " + item.getName();
                String notifySql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                PreparedStatement notifyPs = conn.prepareStatement(notifySql);
                notifyPs.setInt(1, item.getDonorId());
                notifyPs.setString(2, notificationMsg);
                notifyPs.executeUpdate();

                // Log history for both users
                logUserHistory(conn, item.getId(), "Reserved");

                // Remove from available food list
                foodItems.remove(item);
                foodListModel.removeElement(item);

                JOptionPane.showMessageDialog(frame, "Food item reserved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                openGoogleMaps(item.getLocation());

                // Refresh notifications for donor if they're viewing the app
                loadNotifications();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error reserving food item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyList.setCellRenderer(new HistoryItemRenderer());
        JScrollPane scrollPane = new JScrollPane(historyList);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh History");
        refreshButton.addActionListener(e -> loadUserHistory());
        historyPanel.add(refreshButton, BorderLayout.NORTH);
    }

    private void createNotificationsPanel() {
        notificationsPanel = new JPanel(new BorderLayout());
        notificationsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setCellRenderer(new NotificationItemRenderer());
        JScrollPane scrollPane = new JScrollPane(notificationList);
        notificationsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton markReadButton = new JButton("Mark as Read");
        markReadButton.addActionListener(e -> markNotificationsAsRead());
        buttonPanel.add(markReadButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadNotifications());
        buttonPanel.add(refreshButton);

        notificationsPanel.add(buttonPanel, BorderLayout.NORTH);
    }

    private void markNotificationsAsRead() {
        int[] selectedIndices = notificationList.getSelectedIndices();
        if (selectedIndices.length == 0) {
            JOptionPane.showMessageDialog(frame, "Please select notifications to mark as read", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            for (int index : selectedIndices) {
                NotificationItem item = notificationListModel.getElementAt(index);
                String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, item.getId());
                ps.executeUpdate();
            }
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating notifications", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openGoogleMaps(String location) {
        try {
            String url = "https://www.google.com/maps/dir/?api=1&destination=" +
                    java.net.URLEncoder.encode(location, "UTF-8");
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error opening Google Maps", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFoodItemsFromDB() {
        foodItems.clear();
        if (foodListModel != null) foodListModel.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Only load food items that aren't reserved
            String sql = "SELECT f.*, u.username AS donor_username FROM food_items f " +
                    "JOIN users u ON f.user_id = u.id " +
                    "WHERE f.id NOT IN (SELECT food_id FROM reservations WHERE status = 'reserved')";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String servings = rs.getString("servings");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String imagePath = rs.getString("image_path");
                int donorId = rs.getInt("user_id");
                String donorUsername = rs.getString("donor_username");

                ImageIcon icon = null;
                if (imagePath != null) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        BufferedImage img = ImageIO.read(imgFile);
                        icon = new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    }
                }

                FoodItem item = new FoodItem(id, name, description, servings, location, contact, icon, donorId, donorUsername);
                foodItems.add(item);
                if (foodListModel != null) foodListModel.addElement(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserHistory() {
        if (historyListModel == null) return;
        historyListModel.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT h.id, h.action, h.timestamp, f.name AS food_name, " +
                    "CASE WHEN h.user_id = r.donor_id THEN 'Donated to ' ELSE 'Received from ' END || " +
                    "u.username AS other_party " +
                    "FROM user_history h " +
                    "JOIN food_items f ON h.food_id = f.id " +
                    "LEFT JOIN reservations r ON h.food_id = r.food_id AND h.user_id IN (r.donor_id, r.receiver_id) " +
                    "JOIN users u ON (h.user_id = r.donor_id AND u.id = r.receiver_id) OR " +
                    "(h.user_id = r.receiver_id AND u.id = r.donor_id) " +
                    "WHERE h.user_id = ? " +
                    "ORDER BY h.timestamp DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String action = rs.getString("action");
                String timestamp = rs.getString("timestamp");
                String foodName = rs.getString("food_name");
                String otherParty = rs.getString("other_party");

                historyListModel.addElement(new HistoryItem(id, action, timestamp, foodName, otherParty));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        if (notificationListModel == null) return;
        notificationListModel.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT id, message, is_read, created_at FROM notifications " +
                    "WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String message = rs.getString("message");
                boolean isRead = rs.getBoolean("is_read");
                String timestamp = rs.getString("created_at");

                notificationListModel.addElement(new NotificationItem(id, message, isRead, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logUserHistory(Connection conn, int foodId, String action) throws SQLException {
        String sql = "INSERT INTO user_history (user_id, food_id, action) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, currentUserId);
        ps.setInt(2, foodId);
        ps.setString(3, action);
        ps.executeUpdate();
    }

    // Custom renderer classes
    class FoodItemRenderer extends JPanel implements ListCellRenderer<FoodItem> {
        private final JLabel nameLabel = new JLabel();
        private final JLabel detailsLabel = new JLabel();
        private final JLabel imageLabel = new JLabel();
        private final JLabel donorLabel = new JLabel();

        public FoodItemRenderer() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JPanel textPanel = new JPanel(new GridLayout(3, 1));
            textPanel.add(nameLabel);
            textPanel.add(detailsLabel);
            textPanel.add(donorLabel);
            imageLabel.setPreferredSize(new Dimension(50, 50));
            add(imageLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        public Component getListCellRendererComponent(JList<? extends FoodItem> list, FoodItem value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(value.getName());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            detailsLabel.setText("Servings: " + value.getServings() + " | Location: " + value.getLocation());
            detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            donorLabel.setText("Posted by: " + value.getDonorUsername());
            donorLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
            imageLabel.setIcon(value.getImage());
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }

    class HistoryItemRenderer extends JPanel implements ListCellRenderer<HistoryItem> {
        private final JLabel actionLabel = new JLabel();
        private final JLabel detailsLabel = new JLabel();
        private final JLabel timeLabel = new JLabel();

        public HistoryItemRenderer() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.add(actionLabel);
            textPanel.add(detailsLabel);
            add(textPanel, BorderLayout.CENTER);
            add(timeLabel, BorderLayout.EAST);
        }

        public Component getListCellRendererComponent(JList<? extends HistoryItem> list, HistoryItem value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            actionLabel.setText(value.getAction() + ": " + value.getFoodName());
            actionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            detailsLabel.setText("With: " + value.getOtherParty());
            detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            timeLabel.setText(value.getTimestamp());
            timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }

    class NotificationItemRenderer extends JPanel implements ListCellRenderer<NotificationItem> {
        private final JLabel messageLabel = new JLabel();
        private final JLabel timeLabel = new JLabel();

        public NotificationItemRenderer() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(messageLabel, BorderLayout.CENTER);
            add(timeLabel, BorderLayout.EAST);
        }

        public Component getListCellRendererComponent(JList<? extends NotificationItem> list, NotificationItem value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            messageLabel.setText(value.getMessage());
            messageLabel.setFont(new Font("SansSerif", value.isRead() ? Font.PLAIN : Font.BOLD, 14));
            timeLabel.setText(value.getTimestamp());
            timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }

    // Data model classes
    private static class FoodItem {
        private final int id;
        private final String name, description, servings, location, contact;
        private final ImageIcon image;
        private final int donorId;
        private final String donorUsername;

        public FoodItem(int id, String name, String description, String servings, String location,
                        String contact, ImageIcon image, int donorId, String donorUsername) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.servings = servings;
            this.location = location;
            this.contact = contact;
            this.image = image;
            this.donorId = donorId;
            this.donorUsername = donorUsername;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getServings() { return servings; }
        public String getLocation() { return location; }
        public String getContact() { return contact; }
        public ImageIcon getImage() { return image; }
        public int getDonorId() { return donorId; }
        public String getDonorUsername() { return donorUsername; }
    }

    private static class HistoryItem {
        private final int id;
        private final String action, timestamp, foodName, otherParty;

        public HistoryItem(int id, String action, String timestamp, String foodName, String otherParty) {
            this.id = id;
            this.action = action;
            this.timestamp = timestamp;
            this.foodName = foodName;
            this.otherParty = otherParty;
        }

        public int getId() { return id; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }
        public String getFoodName() { return foodName; }
        public String getOtherParty() { return otherParty; }
    }

    private static class NotificationItem {
        private final int id;
        private final String message, timestamp;
        private final boolean isRead;

        public NotificationItem(int id, String message, boolean isRead, String timestamp) {
            this.id = id;
            this.message = message;
            this.isRead = isRead;
            this.timestamp = timestamp;
        }

        public int getId() { return id; }
        public String getMessage() { return message; }
        public boolean isRead() { return isRead; }
        public String getTimestamp() { return timestamp; }
    }

    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ShareABiteEnhanced app = new ShareABiteEnhanced();

            // Load MySQL JDBC driver
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}