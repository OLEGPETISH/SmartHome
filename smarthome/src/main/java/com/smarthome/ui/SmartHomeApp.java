package com.smarthome.ui;

import com.smarthome.abstractfactory.SmartHomeFactory;
import com.smarthome.abstractfactory.SmartHomeFactoryProvider;
import com.smarthome.model.*;
import com.smarthome.singleton.DeviceManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class SmartHomeApp extends JFrame {

    // ── Theme ────────────────────────────────────────────────────────────────
    private static final Color BG = new Color(0x0F1226);
    private static final Color BG_2 = new Color(0x151935);
    private static final Color PANEL = new Color(0x1D2345);
    private static final Color PANEL_2 = new Color(0x242B52);
    private static final Color PANEL_HOVER = new Color(0x2B3360);

    private static final Color TEXT = new Color(0xF7F9FF);
    private static final Color MUTED = new Color(0xA5AED3);
    private static final Color CYAN = new Color(0x67E8F9);
    private static final Color BLUE = new Color(0x60A5FA);
    private static final Color PURPLE = new Color(0xA78BFA);
    private static final Color PINK = new Color(0xF472B6);
    private static final Color GREEN = new Color(0x4ADE80);
    private static final Color ORANGE = new Color(0xFDBA74);
    private static final Color RED = new Color(0xFB7185);
    private static final Color YELLOW = new Color(0xFACC15);
    private static final Color BORDER = new Color(255, 255, 255, 28);

    private final DeviceManager dm = DeviceManager.getInstance();

    // ── Navigation ────────────────────────────────────────────────────────────
    private final CardLayout mainCards = new CardLayout();
    private final JPanel contentPanel = new JPanel(mainCards);

    private JPanel dashboardPage;
    private JPanel roomPage;

    private String currentRoom = null;

    // ── Dynamic UI refs ───────────────────────────────────────────────────────
    private JPanel roomsGrid;
    private JPanel roomDevicesGrid;
    private JTextArea activityArea;

    private JLabel overviewLabel;
    private JLabel roomTitleLabel;
    private JLabel roomMetaLabel;

    public SmartHomeApp() {
        setupDemoDataIfNeeded();
        buildUI();
        refreshAll();
    }

    // ── Demo data ─────────────────────────────────────────────────────────────

    private void setupDemoDataIfNeeded() {
        if (!dm.getAllDevices().isEmpty()) return;

        dm.setHomeOwner("Sarah");
        dm.addRoom("Living Room");
        dm.addRoom("Bedroom");
        dm.addRoom("Kitchen");

        SmartHomeFactory xiaomi = SmartHomeFactoryProvider.getFactory("Xiaomi");
        SmartHomeFactory philips = SmartHomeFactoryProvider.getFactory("Philips");
        SmartHomeFactory samsung = SmartHomeFactoryProvider.getFactory("Samsung");

        Lamp livingLamp = philips.createLamp("Main Light", "Living Room");
        AirConditioner livingAC = xiaomi.createAirConditioner("AC Unit", "Living Room");
        TV livingTV = samsung.createTV("Smart TV", "Living Room");
        Camera livingCam = samsung.createCamera("Door Camera", "Living Room");

        Lamp bedLamp = philips.createLamp("Bedside Lamp", "Bedroom");
        Thermostat bedThermo = xiaomi.createThermostat("Thermostat", "Bedroom");
        Alarm bedAlarm = samsung.createAlarm("Alarm System", "Bedroom");

        Lamp kitchenLamp = philips.createLamp("Kitchen Light", "Kitchen");
        Camera kitchenCam = xiaomi.createCamera("Kitchen Cam", "Kitchen");

        for (Device d : new Device[]{
                livingLamp, livingAC, livingTV, livingCam,
                bedLamp, bedThermo, bedAlarm, kitchenLamp, kitchenCam
        }) {
            dm.addDevice(d);
        }

        livingLamp.turnOn();
        livingAC.turnOn();
        livingTV.turnOn();
        livingCam.turnOn();
        bedLamp.turnOn();
        kitchenLamp.turnOn();

        dm.logEvent("Premium dashboard initialized");
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void buildUI() {
        setTitle("Smart Home");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1380, 880);
        setMinimumSize(new Dimension(1120, 760));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        root.add(buildActivityPanel(), BorderLayout.EAST);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(BG_2);
        side.setPreferredSize(new Dimension(235, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel appTitle = new JLabel("Smart Home");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        appTitle.setForeground(TEXT);

        JLabel sub = new JLabel("Premium Control Panel");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MUTED);

        side.add(appTitle);
        side.add(Box.createVerticalStrut(6));
        side.add(sub);
        side.add(Box.createVerticalStrut(28));

        side.add(sectionLabel("NAVIGATION"));
        side.add(sideButton("🏠 Dashboard", e -> showDashboard()));
        side.add(sideButton("➕ Add Room", e -> showAddRoomDialog()));
        side.add(sideButton("📟 Add Device", e -> {
            String room = currentRoom != null ? currentRoom : firstRoomOrNull();
            showAddDeviceDialog(room);
        }));

        side.add(Box.createVerticalStrut(20));
        side.add(sectionLabel("QUICK ACTIONS"));
        side.add(sideButton("⚡ Turn All On", e -> {
            for (Device d : dm.getAllDevices()) d.turnOn();
            dm.logEvent("All devices turned ON");
            refreshAll();
        }));
        side.add(sideButton("🌙 Turn All Off", e -> {
            dm.turnOffAll();
            refreshAll();
        }));

        side.add(Box.createVerticalStrut(20));
        side.add(sectionLabel("ROOMS"));

        JPanel roomLinks = new JPanel();
        roomLinks.setOpaque(false);
        roomLinks.setLayout(new BoxLayout(roomLinks, BoxLayout.Y_AXIS));

        for (String room : dm.getRooms()) {
            roomLinks.add(sideButton("• " + room, e -> openRoom(room)));
        }

        side.add(roomLinks);
        side.add(Box.createVerticalGlue());

        JLabel owner = new JLabel("Owner: " + dm.getHomeOwner());
        owner.setForeground(MUTED);
        owner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        side.add(owner);

        return side;
    }

    private JPanel buildMainArea() {
        contentPanel.setOpaque(false);

        dashboardPage = buildDashboardPage();
        roomPage = buildRoomPage();

        contentPanel.add(dashboardPage, "dashboard");
        contentPanel.add(roomPage, "room");

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrap.add(contentPanel, BorderLayout.CENTER);

        mainCards.show(contentPanel, "dashboard");
        return wrap;
    }

    private JPanel buildDashboardPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);

        page.add(buildDashboardHeader(), BorderLayout.NORTH);

        roomsGrid = new JPanel(new GridLayout(0, 2, 18, 18));
        roomsGrid.setOpaque(false);

        JScrollPane scroll = styledScroll(roomsGrid);
        page.add(scroll, BorderLayout.CENTER);

        return page;
    }

    private JPanel buildDashboardHeader() {
        GlassPanel header = new GlassPanel(28);
        header.setLayout(new BorderLayout(18, 0));
        header.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel hello = new JLabel("Welcome back, " + dm.getHomeOwner());
        hello.setForeground(MUTED);
        hello.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel title = new JLabel("Home Overview");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 34));

        overviewLabel = new JLabel();
        overviewLabel.setForeground(CYAN);
        overviewLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        left.add(hello);
        left.add(Box.createVerticalStrut(6));
        left.add(title);
        left.add(Box.createVerticalStrut(10));
        left.add(overviewLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        right.add(actionButton("＋ Add Room", CYAN, e -> showAddRoomDialog()));
        right.add(actionButton("＋ Add Device", PURPLE, e -> {
            String room = firstRoomOrNull();
            showAddDeviceDialog(room);
        }));

        header.add(left, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel buildRoomPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);

        page.add(buildRoomHeader(), BorderLayout.NORTH);

        roomDevicesGrid = new JPanel(new GridLayout(0, 3, 16, 16));
        roomDevicesGrid.setOpaque(false);

        JScrollPane scroll = styledScroll(roomDevicesGrid);
        page.add(scroll, BorderLayout.CENTER);

        return page;
    }

    private JPanel buildRoomHeader() {
        GlassPanel header = new GlassPanel(28);
        header.setLayout(new BorderLayout(18, 0));
        header.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JButton back = pillButton("← Back to Dashboard", MUTED);
        back.addActionListener(e -> showDashboard());

        roomTitleLabel = new JLabel("Room");
        roomTitleLabel.setForeground(TEXT);
        roomTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

        roomMetaLabel = new JLabel();
        roomMetaLabel.setForeground(CYAN);
        roomMetaLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        left.add(back);
        left.add(Box.createVerticalStrut(12));
        left.add(roomTitleLabel);
        left.add(Box.createVerticalStrut(6));
        left.add(roomMetaLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        right.add(actionButton("＋ Add Device", CYAN, e -> showAddDeviceDialog(currentRoom)));
        right.add(actionButton("All On", GREEN, e -> {
            if (currentRoom != null) {
                dm.turnOnAllInRoom(currentRoom);
                refreshAll();
            }
        }));
        right.add(actionButton("All Off", RED, e -> {
            if (currentRoom != null) {
                dm.turnOffAllInRoom(currentRoom);
                refreshAll();
            }
        }));
        right.add(actionButton("Rename Room", ORANGE, e -> showRenameRoomDialog()));
        right.add(actionButton("Delete Room", PINK, e -> deleteCurrentRoom()));

        header.add(left, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel buildActivityPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_2);
        wrap.setPreferredSize(new Dimension(290, 0));
        wrap.setBorder(new EmptyBorder(20, 0, 20, 20));

        GlassPanel panel = new GlassPanel(28);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Recent Activity");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel sub = new JLabel("System events and interactions");
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(4));
        head.add(sub);

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        activityArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        activityArea.setForeground(TEXT);
        activityArea.setBackground(PANEL);
        activityArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(PANEL);
        scroll.setBackground(PANEL);

        JButton clear = pillButton("Clear Log", MUTED);
        clear.addActionListener(e -> {
            dm.logEvent("Activity panel refreshed");
            refreshActivity();
        });

        panel.add(head, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(clear, BorderLayout.SOUTH);

        wrap.add(panel, BorderLayout.CENTER);
        return wrap;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    private void refreshAll() {
        refreshDashboard();
        refreshRoomView();
        refreshActivity();
        revalidate();
        repaint();
    }

    private void refreshDashboard() {
        if (overviewLabel != null) {
            overviewLabel.setText(dm.getRooms().size() + " rooms • "
                    + dm.getTotalDevices() + " devices • "
                    + dm.getActiveCount() + " active now");
        }

        if (roomsGrid == null) return;

        roomsGrid.removeAll();

        List<String> rooms = dm.getRooms();
        if (rooms.isEmpty()) {
            roomsGrid.add(emptyStateCard(
                    "No rooms yet",
                    "Create your first room to start building the smart home dashboard.",
                    "＋ Add Room",
                    this::showAddRoomDialog
            ));
        } else {
            for (String room : rooms) {
                roomsGrid.add(buildRoomCard(room));
            }
        }

        roomsGrid.revalidate();
        roomsGrid.repaint();
    }

    private void refreshRoomView() {
        if (roomDevicesGrid == null) return;

        roomDevicesGrid.removeAll();

        if (currentRoom == null) {
            roomTitleLabel.setText("Room");
            roomMetaLabel.setText("");
            roomDevicesGrid.revalidate();
            roomDevicesGrid.repaint();
            return;
        }

        List<Device> devices = dm.getDevicesByRoom(currentRoom);

        roomTitleLabel.setText(currentRoom);
        roomMetaLabel.setText(devices.size() + " devices • "
                + dm.getActiveCountByRoom(currentRoom) + " active");

        if (devices.isEmpty()) {
            roomDevicesGrid.add(emptyStateCard(
                    "No devices in " + currentRoom,
                    "Add your first smart device to this room.",
                    "＋ Add Device",
                    () -> showAddDeviceDialog(currentRoom)
            ));
        } else {
            for (Device d : devices) {
                roomDevicesGrid.add(buildDeviceCard(d));
            }
        }

        roomDevicesGrid.revalidate();
        roomDevicesGrid.repaint();
    }

    private void refreshActivity() {
        if (activityArea == null) return;

        List<String> events = dm.getRecentEvents(40);
        StringBuilder sb = new StringBuilder();

        for (int i = events.size() - 1; i >= 0; i--) {
            sb.append(events.get(i)).append("\n\n");
        }

        activityArea.setText(sb.toString());
        activityArea.setCaretPosition(0);
    }

    // ── Cards ─────────────────────────────────────────────────────────────────

    private JComponent buildRoomCard(String room) {
        GlassPanel card = new GlassPanel(30);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        List<Device> devices = dm.getDevicesByRoom(room);
        long active = devices.stream().filter(Device::isOn).count();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel icon = new JLabel(roomIcon(room));
        icon.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JLabel name = new JLabel(room);
        name.setForeground(TEXT);
        name.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.add(icon);
        left.add(Box.createHorizontalStrut(10));
        left.add(name);

        JLabel badge = statusBadge(active + " active", active > 0 ? GREEN : MUTED);

        top.add(left, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);

        JLabel meta = new JLabel(devices.size() + " devices • "
                + deviceTypeSummary(devices));
        meta.setForeground(MUTED);
        meta.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel preview = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        preview.setOpaque(false);

        if (devices.isEmpty()) {
            JLabel none = new JLabel("No devices yet");
            none.setForeground(MUTED);
            preview.add(none);
        } else {
            for (int i = 0; i < Math.min(devices.size(), 4); i++) {
                Device d = devices.get(i);
                preview.add(typeChip(d.getType()));
            }
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton open = actionButton("Open Room", CYAN, e -> openRoom(room));
        JButton add = actionButton("Add Device", PURPLE, e -> showAddDeviceDialog(room));
        bottom.add(add);
        bottom.add(open);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(meta);
        center.add(Box.createVerticalStrut(14));
        center.add(preview);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openRoom(room); }
        });

        return card;
    }

    private JComponent buildDeviceCard(Device device) {
        GlassPanel card = new GlassPanel(26);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        Color accent = colorForType(device.getType());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel type = new JLabel(device.getType().getIcon() + "  " + device.getType().getLabel());
        type.setForeground(accent);
        type.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel power = statusBadge(device.isOn() ? "ON" : "OFF", device.isOn() ? GREEN : MUTED);

        top.add(type, BorderLayout.WEST);
        top.add(power, BorderLayout.EAST);

        JLabel name = new JLabel(device.getName());
        name.setForeground(TEXT);
        name.setFont(new Font("SansSerif", Font.BOLD, 22));

        String brand = device instanceof AbstractDevice ad ? ad.getBrand() : "Unknown";
        JLabel meta = new JLabel(brand + " • " + device.getRoom());
        meta.setForeground(MUTED);
        meta.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTextArea status = new JTextArea(device.getStatus());
        status.setEditable(false);
        status.setLineWrap(true);
        status.setWrapStyleWord(true);
        status.setOpaque(false);
        status.setForeground(device.isOn() ? accent : MUTED);
        status.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(name);
        center.add(Box.createVerticalStrut(4));
        center.add(meta);
        center.add(Box.createVerticalStrut(10));
        center.add(status);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);

        JToggleButton toggle = new JToggleButton(device.isOn() ? "ON" : "OFF");
        toggle.setSelected(device.isOn());
        styleToggle(toggle, accent);
        toggle.addActionListener(e -> {
            if (toggle.isSelected()) {
                device.turnOn();
                dm.logEvent("Turned ON " + device.getName());
            } else {
                device.turnOff();
                dm.logEvent("Turned OFF " + device.getName());
            }
            refreshAll();
        });

        JButton edit = pillButton("Edit", CYAN);
        edit.addActionListener(e -> showEditDeviceDialog(device));

        JButton delete = pillButton("Delete", RED);
        delete.addActionListener(e -> deleteDevice(device));

        bottom.add(toggle);
        bottom.add(edit);
        bottom.add(delete);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JComponent emptyStateCard(String title, String text, String actionText, Runnable action) {
        GlassPanel card = new GlassPanel(28);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT);
        t.setFont(new Font("SansSerif", Font.BOLD, 24));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(text);
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setOpaque(false);
        desc.setForeground(MUTED);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton actionBtn = actionButton(actionText, CYAN, e -> action.run());
        actionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(desc);
        card.add(Box.createVerticalStrut(16));
        card.add(actionBtn);

        return card;
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private void showAddRoomDialog() {
        JTextField roomField = styledField();
        JPanel panel = formPanel(
                "Create New Room",
                "Add a new room to the smart home dashboard.",
                "Room name", roomField
        );

        int ok = JOptionPane.showConfirmDialog(
                this, panel, "Add Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (ok == JOptionPane.OK_OPTION) {
            String room = roomField.getText().trim();
            if (room.isBlank()) {
                showMessage("Room name cannot be empty.");
                return;
            }
            dm.addRoom(room);
            refreshAll();
        }
    }

    private void showRenameRoomDialog() {
        if (currentRoom == null) return;

        JTextField roomField = styledField();
        roomField.setText(currentRoom);

        JPanel panel = formPanel(
                "Rename Room",
                "Update the room title across the dashboard.",
                "New name", roomField
        );

        int ok = JOptionPane.showConfirmDialog(
                this, panel, "Rename Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (ok == JOptionPane.OK_OPTION) {
            String newName = roomField.getText().trim();
            if (newName.isBlank()) {
                showMessage("Room name cannot be empty.");
                return;
            }
            dm.renameRoom(currentRoom, newName);
            currentRoom = newName;
            refreshAll();
            openRoom(newName);
        }
    }

    private void showAddDeviceDialog(String preselectedRoom) {
        if (dm.getRooms().isEmpty()) {
            showMessage("Create a room first.");
            return;
        }

        JTextField nameField = styledField();
        JComboBox<DeviceType> typeBox = styledCombo(DeviceType.values());
        JComboBox<String> brandBox = styledCombo(SmartHomeFactoryProvider.availableBrands());
        JComboBox<String> roomBox = styledCombo(dm.getRooms().toArray(new String[0]));

        if (preselectedRoom != null) roomBox.setSelectedItem(preselectedRoom);

        JPanel panel = deviceFormPanel("Add Device", "Create a new smart device.",
                nameField, typeBox, brandBox, roomBox);

        int ok = JOptionPane.showConfirmDialog(
                this, panel, "Add Device",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (ok == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            DeviceType type = (DeviceType) typeBox.getSelectedItem();
            String brand = (String) brandBox.getSelectedItem();
            String room = (String) roomBox.getSelectedItem();

            if (name.isBlank() || type == null || brand == null || room == null) {
                showMessage("Please fill all fields.");
                return;
            }

            SmartHomeFactory factory = SmartHomeFactoryProvider.getFactory(brand);
            Device newDevice = createDevice(factory, type, name, room);

            if (newDevice == null) {
                showMessage("This device type is not supported by the current factory method.");
                return;
            }

            dm.addDevice(newDevice);
            if (currentRoom != null && currentRoom.equals(room)) {
                openRoom(room);
            }
            refreshAll();
        }
    }

    private void showEditDeviceDialog(Device device) {
        JTextField nameField = styledField();
        nameField.setText(device.getName());

        JComboBox<String> roomBox = styledCombo(dm.getRooms().toArray(new String[0]));
        roomBox.setSelectedItem(device.getRoom());

        JPanel panel = formPanel(
                "Edit Device",
                "Change the device name or move it to another room.",
                "Device name", nameField,
                "Room", roomBox
        );

        int ok = JOptionPane.showConfirmDialog(
                this, panel, "Edit Device",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (ok == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newRoom = (String) roomBox.getSelectedItem();

            if (newName.isBlank() || newRoom == null || newRoom.isBlank()) {
                showMessage("Please fill all fields.");
                return;
            }

            dm.updateDevice(device.getId(), newName, newRoom);
            if (currentRoom != null && !currentRoom.equals(newRoom)
                    && dm.getDevicesByRoom(currentRoom).isEmpty()) {
                refreshAll();
            } else {
                refreshAll();
            }

            if (currentRoom != null && dm.getRooms().contains(currentRoom)) {
                openRoom(currentRoom);
            }
        }
    }

    private void deleteDevice(Device device) {
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Delete device '" + device.getName() + "'?",
                "Delete Device",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (ok == JOptionPane.YES_OPTION) {
            dm.removeDevice(device.getId());
            refreshAll();
            if (currentRoom != null) openRoom(currentRoom);
        }
    }

    private void deleteCurrentRoom() {
        if (currentRoom == null) return;

        List<Device> devices = dm.getDevicesByRoom(currentRoom);
        if (!devices.isEmpty()) {
            showMessage("You can delete only an empty room.\nRemove or move all devices first.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Delete room '" + currentRoom + "'?",
                "Delete Room",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (ok == JOptionPane.YES_OPTION) {
            String roomToDelete = currentRoom;
            dm.removeRoom(roomToDelete);
            currentRoom = null;
            refreshAll();
            showDashboard();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Device createDevice(SmartHomeFactory factory, DeviceType type, String name, String room) {
        return switch (type) {
            case LAMP -> factory.createLamp(name, room);
            case THERMOSTAT -> factory.createThermostat(name, room);
            case CAMERA -> factory.createCamera(name, room);
            case ALARM -> factory.createAlarm(name, room);
            case AIR_CONDITIONER -> factory.createAirConditioner(name, room);
            case TV -> factory.createTV(name, room);
            default -> null;
        };
    }

    private void showDashboard() {
        currentRoom = null;
        refreshDashboard();
        mainCards.show(contentPanel, "dashboard");
    }

    private void openRoom(String room) {
        currentRoom = room;
        refreshRoomView();
        mainCards.show(contentPanel, "room");
    }

    private String firstRoomOrNull() {
        List<String> rooms = dm.getRooms();
        return rooms.isEmpty() ? null : rooms.get(0);
    }

    private JPanel formPanel(String title, String desc, Object... labelFieldPairs) {
        JPanel wrap = new JPanel(new BorderLayout(0, 14));
        wrap.setBackground(PANEL);

        JPanel head = new JPanel();
        head.setBackground(PANEL);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT);
        t.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel d = new JLabel(desc);
        d.setForeground(MUTED);
        d.setFont(new Font("SansSerif", Font.PLAIN, 12));

        head.add(t);
        head.add(Box.createVerticalStrut(4));
        head.add(d);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(PANEL);
        form.setBorder(new EmptyBorder(8, 0, 0, 0));

        for (int i = 0; i < labelFieldPairs.length; i += 2) {
            String label = (String) labelFieldPairs[i];
            JComponent field = (JComponent) labelFieldPairs[i + 1];

            JLabel l = new JLabel(label);
            l.setForeground(MUTED);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));

            JPanel block = new JPanel(new BorderLayout(0, 6));
            block.setBackground(PANEL);
            block.add(l, BorderLayout.NORTH);
            block.add(field, BorderLayout.CENTER);

            form.add(block);
        }

        wrap.add(head, BorderLayout.NORTH);
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel deviceFormPanel(String title, String desc,
                                   JTextField nameField,
                                   JComboBox<DeviceType> typeBox,
                                   JComboBox<String> brandBox,
                                   JComboBox<String> roomBox) {
        return formPanel(
                title, desc,
                "Device name", nameField,
                "Type", typeBox,
                "Brand", brandBox,
                "Room", roomBox
        );
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setBackground(BG_2);
        f.setForeground(TEXT);
        f.setCaretColor(CYAN);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
        return f;
    }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setBackground(BG_2);
        box.setForeground(TEXT);
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));
        box.setBorder(BorderFactory.createLineBorder(BORDER));
        return box;
    }

    private JScrollPane styledScroll(JComponent view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JButton sideButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBackground(PANEL);
        btn.setForeground(TEXT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(PANEL_HOVER); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(PANEL); }
        });
        return btn;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setBorder(new EmptyBorder(0, 2, 10, 2));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton actionButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 36));
        btn.setForeground(color);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 110), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private JButton pillButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(PANEL_2);
        btn.setForeground(color);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel statusBadge(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        l.setForeground(color);
        l.setBorder(new EmptyBorder(6, 10, 6, 10));
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    private JLabel typeChip(DeviceType type) {
        JLabel chip = new JLabel(type.getIcon() + " " + type.getLabel());
        chip.setOpaque(true);
        chip.setBackground(new Color(255, 255, 255, 18));
        chip.setForeground(colorForType(type));
        chip.setBorder(new EmptyBorder(7, 10, 7, 10));
        chip.setFont(new Font("SansSerif", Font.BOLD, 11));
        return chip;
    }

    private void styleToggle(JToggleButton btn, Color accent) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(btn.isSelected() ? BG : TEXT);
        btn.setBackground(btn.isSelected() ? accent : PANEL_2);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String roomIcon(String room) {
        String r = room.toLowerCase();
        if (r.contains("bed")) return "🛏";
        if (r.contains("kitchen")) return "🍽";
        if (r.contains("living")) return "🛋";
        if (r.contains("bath")) return "🛁";
        if (r.contains("office")) return "💼";
        return "🏠";
    }

    private String deviceTypeSummary(List<Device> devices) {
        if (devices.isEmpty()) return "empty room";

        Map<DeviceType, Long> counts = devices.stream()
                .collect(Collectors.groupingBy(Device::getType, LinkedHashMap::new, Collectors.counting()));

        return counts.entrySet().stream()
                .limit(3)
                .map(e -> e.getKey().getLabel() + " ×" + e.getValue())
                .collect(Collectors.joining(" • "));
    }

    private Color colorForType(DeviceType type) {
        return switch (type) {
            case LAMP -> YELLOW;
            case THERMOSTAT -> ORANGE;
            case AIR_CONDITIONER -> CYAN;
            case CAMERA -> PURPLE;
            case ALARM -> RED;
            case TV -> BLUE;
            default -> GREEN;
        };
    }

    private void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text, "Smart Home", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Glass panel ───────────────────────────────────────────────────────────

    private static class GlassPanel extends JPanel {
        private final int arc;

        GlassPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(new Color(29, 35, 69, 220));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.background", PANEL);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("TextField.background", BG_2);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("ComboBox.background", BG_2);
        UIManager.put("ComboBox.foreground", TEXT);

        SwingUtilities.invokeLater(() -> {
            SmartHomeApp app = new SmartHomeApp();
            app.setVisible(true);
        });
    }
}