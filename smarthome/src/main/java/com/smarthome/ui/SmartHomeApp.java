package com.smarthome.ui;

import com.smarthome.abstractfactory.SmartHomeFactory;
import com.smarthome.abstractfactory.SmartHomeFactoryProvider;
import com.smarthome.builder.AutomationScenario;
import com.smarthome.builder.ScenarioDirector;
import com.smarthome.model.*;
import com.smarthome.prototype.ConfigurationRegistry;
import com.smarthome.prototype.RoomConfiguration;
import com.smarthome.singleton.DeviceManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;


public class SmartHomeApp extends JFrame {

    // ── Color Palette (dark theme) ────────────────────────────────────────────
    static final Color BG_DARK      = new Color(0x1A1D2E);
    static final Color BG_CARD      = new Color(0x242740);
    static final Color BG_CARD2     = new Color(0x2D3154);
    static final Color ACCENT_CYAN  = new Color(0x00D4FF);
    static final Color ACCENT_BLUE  = new Color(0x4A9EFF);
    static final Color ACCENT_PURPLE= new Color(0x8B6BF0);
    static final Color TEXT_PRIMARY = new Color(0xF0F4FF);
    static final Color TEXT_MUTED   = new Color(0x8892B0);
    static final Color SUCCESS      = new Color(0x00E5A0);
    static final Color DANGER       = new Color(0xFF5472);
    static final Color WARNING      = new Color(0xFFB347);

    private final DeviceManager dm = DeviceManager.getInstance();
    private ConfigurationRegistry registry;
    private JPanel devicesPanel;
    private JLabel statsLabel;
    private JTextArea logArea;
    private String selectedRoom = "All Rooms";
    private JLabel roomLabel;
    private javax.swing.Timer refreshTimer;

    public SmartHomeApp() {
        setupPatterns();
        buildUI();
        startRefreshTimer();
    }

    // ── Initialize all design patterns ────────────────────────────────────────

    private void setupPatterns() {
        dm.setHomeOwner("Sarah");

        // Abstract Factory: create devices by brand
        SmartHomeFactory xiaomi  = SmartHomeFactoryProvider.getFactory("Xiaomi");
        SmartHomeFactory philips = SmartHomeFactoryProvider.getFactory("Philips");
        SmartHomeFactory samsung = SmartHomeFactoryProvider.getFactory("Samsung");

        Lamp     livingLamp  = philips.createLamp("Main Light",   "Living Room");
        AirConditioner livingAC = xiaomi.createAirConditioner("AC Unit", "Living Room");
        TV       livingTV    = samsung.createTV("Smart TV",        "Living Room");
        Camera   livingCam   = samsung.createCamera("Door Camera","Living Room");

        Lamp     bedLamp     = philips.createLamp("Bedside Lamp", "Bedroom");
        Thermostat bedThermo = xiaomi.createThermostat("Thermostat","Bedroom");
        Alarm    bedAlarm    = samsung.createAlarm("Alarm System","Bedroom");

        Lamp     kitchenLamp = philips.createLamp("Kitchen Light","Kitchen");
        Camera   kitchenCam  = xiaomi.createCamera("Kitchen Cam", "Kitchen");

        // Register all devices
        for (Device d : new Device[]{livingLamp, livingAC, livingTV, livingCam,
                                     bedLamp, bedThermo, bedAlarm, kitchenLamp, kitchenCam}) {
            dm.addDevice(d);
        }

        // Turn some on by default
        livingLamp.turnOn(); livingAC.turnOn(); livingTV.turnOn(); livingCam.turnOn();
        bedLamp.turnOn();
        kitchenLamp.turnOn();

        // Prototype: register room configs
        registry = new ConfigurationRegistry();

        RoomConfiguration nightMode = new RoomConfiguration("Night Mode", "Living Room")
                .setLampSettings(15, "warm")
                .setThermostatSettings(19.0, 1)
                .setACSettings(22.0, 1)
                .setCameraSettings(true);
        registry.register("night_mode", nightMode);

        RoomConfiguration morningMode = new RoomConfiguration("Morning Mode", "Living Room")
                .setLampSettings(100, "daylight")
                .setThermostatSettings(21.0, 2)
                .setACSettings(23.0, 2);
        registry.register("morning_mode", morningMode);

        RoomConfiguration movieMode = new RoomConfiguration("Movie Mode", "Living Room")
                .setLampSettings(20, "warm")
                .setACSettings(23.0, 1);
        registry.register("movie_mode", movieMode);
    }

    // ── Build UI ──────────────────────────────────────────────────────────────

    private void buildUI() {
        setTitle("Smart Home");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 740);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildLogPanel(), BorderLayout.EAST);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_CARD);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_CARD);
        header.setBorder(new EmptyBorder(0, 20, 20, 20));
        header.setAlignmentX(LEFT_ALIGNMENT);

        JLabel homeIcon = new JLabel("🏠");
        homeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        homeIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("SmartHome");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_CYAN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ownerLabel = new JLabel("Hey, " + dm.getHomeOwner() + "!");
        ownerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ownerLabel.setForeground(TEXT_MUTED);
        ownerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(homeIcon);
        header.add(Box.createVerticalStrut(8));
        header.add(titleLabel);
        header.add(ownerLabel);
        sidebar.add(header);

        // Separator
        sidebar.add(createDivider());

        // Room buttons
        JLabel roomsTitle = new JLabel("ROOMS");
        roomsTitle.setFont(new Font("Arial", Font.BOLD, 10));
        roomsTitle.setForeground(TEXT_MUTED);
        roomsTitle.setBorder(new EmptyBorder(10, 20, 8, 20));
        roomsTitle.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(roomsTitle);

        addRoomButton(sidebar, "All Rooms", "🏠");
        for (String room : dm.getRooms()) {
            String icon = switch (room) {
                case "Living Room" -> "🛋️";
                case "Bedroom"     -> "🛏️";
                case "Kitchen"     -> "🍳";
                case "Bathroom"    -> "🚿";
                default            -> "📍";
            };
            addRoomButton(sidebar, room, icon);
        }

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createDivider());

        // Scenarios
        JLabel scenLabel = new JLabel("SCENARIOS");
        scenLabel.setFont(new Font("Arial", Font.BOLD, 10));
        scenLabel.setForeground(TEXT_MUTED);
        scenLabel.setBorder(new EmptyBorder(10, 20, 8, 20));
        scenLabel.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(scenLabel);

        addScenarioButton(sidebar, "🌙  Night Mode",   "night_mode");
        addScenarioButton(sidebar, "☀️  Morning Mode", "morning_mode");
        addScenarioButton(sidebar, "🎬  Movie Mode",   "movie_mode");
        addScenarioButton(sidebar, "🚪  Away Mode",    null);

        sidebar.add(Box.createVerticalGlue());

        // Stats
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statsLabel.setForeground(TEXT_MUTED);
        statsLabel.setBorder(new EmptyBorder(10, 20, 0, 20));
        statsLabel.setAlignmentX(LEFT_ALIGNMENT);
        updateStats();
        sidebar.add(statsLabel);

        return sidebar;
    }

    private void addRoomButton(JPanel parent, String room, String icon) {
        JButton btn = createSidebarButton(icon + "  " + room);
        if (room.equals(selectedRoom)) {
            btn.setBackground(BG_CARD2);
            btn.setForeground(ACCENT_CYAN);
        }
        btn.addActionListener(e -> {
            selectedRoom = room;
            roomLabel.setText(room);
            refreshDevices();
            // Recolor
            for (Component c : parent.getComponents()) {
                if (c instanceof JButton b) {
                    String label = b.getText().trim();
                    boolean active = label.endsWith(room);
                    b.setBackground(active ? BG_CARD2 : BG_CARD);
                    b.setForeground(active ? ACCENT_CYAN : TEXT_PRIMARY);
                }
            }
        });
        parent.add(btn);
    }

    private void addScenarioButton(JPanel parent, String label, String configKey) {
        JButton btn = createSidebarButton(label);
        btn.addActionListener(e -> runScenario(configKey, label));
        parent.add(btn);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_CARD);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(BG_CARD2);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_CARD);
            }
        });
        return btn;
    }

    // ── Main Content ──────────────────────────────────────────────────────────

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BG_DARK);
        content.setBorder(new EmptyBorder(20, 20, 20, 12));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_DARK);

        roomLabel = new JLabel("All Rooms");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 22));
        roomLabel.setForeground(TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BG_DARK);

        JButton addBtn = createActionButton("+ Add Device", ACCENT_CYAN, BG_DARK);
        addBtn.addActionListener(e -> showAddDeviceDialog());
        actions.add(addBtn);

        JButton allOnBtn = createActionButton("All On", SUCCESS, BG_DARK);
        allOnBtn.addActionListener(e -> {
            if (selectedRoom.equals("All Rooms")) dm.getAllDevices().forEach(Device::turnOn);
            else dm.turnOnAllInRoom(selectedRoom);
            refreshDevices(); updateStats();
        });
        actions.add(allOnBtn);

        JButton allOffBtn = createActionButton("All Off", DANGER, BG_DARK);
        allOffBtn.addActionListener(e -> {
            if (selectedRoom.equals("All Rooms")) dm.turnOffAll();
            else dm.turnOffAllInRoom(selectedRoom);
            refreshDevices(); updateStats();
        });
        actions.add(allOffBtn);

        topBar.add(roomLabel, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        content.add(topBar, BorderLayout.NORTH);

        // Devices grid
        devicesPanel = new JPanel(new GridLayout(0, 3, 12, 12));
        devicesPanel.setBackground(BG_DARK);

        JScrollPane scroll = new JScrollPane(devicesPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scroll, BorderLayout.CENTER);

        // Patterns info bar
        content.add(buildPatternBar(), BorderLayout.SOUTH);

        refreshDevices();
        return content;
    }

    private JPanel buildPatternBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bar.setBackground(BG_DARK);
        bar.setBorder(new EmptyBorder(8, 0, 0, 0));

        String[] patterns = {"Factory Method", "Abstract Factory", "Singleton", "Builder", "Prototype"};
        Color[] colors = {ACCENT_CYAN, ACCENT_BLUE, ACCENT_PURPLE, SUCCESS, WARNING};
        for (int i = 0; i < patterns.length; i++) {
            JLabel tag = new JLabel(patterns[i]);
            tag.setFont(new Font("Arial", Font.BOLD, 10));
            tag.setForeground(colors[i]);
            tag.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colors[i], 1, true),
                    new EmptyBorder(3, 8, 3, 8)));
            bar.add(tag);
        }
        return bar;
    }

    // ── Log Panel ─────────────────────────────────────────────────────────────

    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_CARD);
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(new EmptyBorder(20, 12, 20, 16));

        JLabel title = new JLabel("Activity Log");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        logArea.setBackground(new Color(0x1A1D2E));
        logArea.setForeground(TEXT_MUTED);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BG_CARD2));
        panel.add(scroll, BorderLayout.CENTER);

        JButton clearBtn = createActionButton("Clear Log", TEXT_MUTED, BG_CARD);
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        clearBtn.addActionListener(e -> logArea.setText(""));
        panel.add(clearBtn, BorderLayout.SOUTH);

        return panel;
    }

    // ── Device Cards ──────────────────────────────────────────────────────────

    private void refreshDevices() {
        devicesPanel.removeAll();

        List<Device> devices = selectedRoom.equals("All Rooms")
                ? dm.getAllDevices()
                : dm.getDevicesByRoom(selectedRoom);

        if (devices.isEmpty()) {
            JLabel empty = new JLabel("No devices in this room", SwingConstants.CENTER);
            empty.setForeground(TEXT_MUTED);
            empty.setFont(new Font("Arial", Font.ITALIC, 14));
            devicesPanel.add(empty);
        } else {
            for (Device device : devices) {
                devicesPanel.add(buildDeviceCard(device));
            }
        }

        devicesPanel.revalidate();
        devicesPanel.repaint();
        updateStats();
        refreshLog();
    }

    private JPanel buildDeviceCard(Device device) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(device.isOn() ? BG_CARD2 : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                if (device.isOn()) {
                    Color glow = getAccentForType(device.getType());
                    g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 80));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                }
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 130));

        // Top row: icon + toggle
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        Color accent = getAccentForType(device.getType());
        JLabel iconLabel = new JLabel(device.getType().getIcon() + "  " + device.getType().getLabel());
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        iconLabel.setForeground(device.isOn() ? accent : TEXT_MUTED);

        // Toggle switch (custom JToggleButton)
        JToggleButton toggle = new JToggleButton(device.isOn() ? "ON" : "OFF");
        toggle.setSelected(device.isOn());
        styleToggle(toggle, accent);
        toggle.addActionListener(e -> {
            if (toggle.isSelected()) device.turnOn();
            else device.turnOff();
            toggle.setText(toggle.isSelected() ? "ON" : "OFF");
            styleToggle(toggle, accent);
            refreshDevices();
        });

        topRow.add(iconLabel, BorderLayout.WEST);
        topRow.add(toggle, BorderLayout.EAST);

        // Name
        JLabel nameLabel = new JLabel(device.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_PRIMARY);

        // Brand + room
        String brand = device instanceof AbstractDevice ? ((AbstractDevice) device).getBrand() : "";
        JLabel brandLabel = new JLabel(brand + " · " + device.getRoom());
        brandLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        brandLabel.setForeground(TEXT_MUTED);

        // Status
        JLabel statusLabel = new JLabel(device.getStatus());
        statusLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        statusLabel.setForeground(device.isOn() ? accent : TEXT_MUTED);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(2));
        centerPanel.add(brandLabel);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(statusLabel);

        card.add(topRow, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);

        // Hover
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { card.repaint(); }
        });

        return card;
    }

    private Color getAccentForType(DeviceType type) {
        return switch (type) {
            case LAMP              -> WARNING;
            case THERMOSTAT        -> new Color(0xFF7A7A);
            case AIR_CONDITIONER   -> ACCENT_CYAN;
            case CAMERA            -> ACCENT_PURPLE;
            case ALARM             -> DANGER;
            case TV                -> ACCENT_BLUE;
            default                -> SUCCESS;
        };
    }

    private void styleToggle(JToggleButton btn, Color accent) {
        btn.setFont(new Font("Arial", Font.BOLD, 10));
        btn.setForeground(btn.isSelected() ? BG_DARK : TEXT_MUTED);
        btn.setBackground(btn.isSelected() ? accent : new Color(0x3A3D5A));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Scenarios ────────────────────────────────────────────────────────────

    private void runScenario(String configKey, String label) {
        String room = selectedRoom.equals("All Rooms") ? "Living Room" : selectedRoom;

        // Prototype: clone config and apply settings to devices
        if (configKey != null) {
            RoomConfiguration config = registry.getCloneForRoom(configKey, room);
            List<Device> targets = selectedRoom.equals("All Rooms")
                    ? dm.getAllDevices()
                    : dm.getDevicesByRoom(room);
            for (Device d : targets) {
                d.applySettings(config.getSettingsFor(d.getType()));
            }
        }

        // Builder + Director:
        // Клиент создаёт Builder, передаёт его Director.
        // Director сам знает порядок шагов построения каждого сценария —
        // клиент (UI) не управляет деталями, просто просит нужный тип.
        AutomationScenario.Builder builder = new AutomationScenario.Builder(label);
        ScenarioDirector director = new ScenarioDirector(builder);

        AutomationScenario scenario = switch (configKey != null ? configKey : "away") {
            case "night_mode"   -> director.buildNightMode(room);
            case "morning_mode" -> director.buildMorningMode(room);
            case "movie_mode"   -> director.buildMovieMode(room);
            default             -> director.buildAwayMode();
        };

        SwingUtilities.invokeLater(() -> {
            scenario.execute();
            refreshDevices();
            showToast(label + " активирован для: " + room);
        });
    }

    // ── Add Device Dialog ─────────────────────────────────────────────────────

    private void showAddDeviceDialog() {
        JDialog dialog = new JDialog(this, "Add New Device", true);
        dialog.setSize(380, 340);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_CARD);
        dialog.setLayout(new BorderLayout(0, 0));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        String[] typeOptions = Arrays.stream(DeviceType.values())
                .map(t -> t.getIcon() + " " + t.getLabel()).toArray(String[]::new);
        String[] brandOptions = SmartHomeFactoryProvider.availableBrands();
        String[] roomOptions = dm.getRooms().toArray(String[]::new);

        JTextField nameField = styledTextField();
        JComboBox<String> typeCombo = styledCombo(typeOptions);
        JComboBox<String> brandCombo = styledCombo(brandOptions);
        JComboBox<String> roomCombo = styledCombo(roomOptions);

        form.add(styledFormLabel("Device Name:")); form.add(nameField);
        form.add(styledFormLabel("Type:"));        form.add(typeCombo);
        form.add(styledFormLabel("Brand:"));       form.add(brandCombo);
        form.add(styledFormLabel("Room:"));        form.add(roomCombo);

        JButton addBtn = createActionButton("Add Device", ACCENT_CYAN, BG_CARD);
        addBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String brand = brandOptions[brandCombo.getSelectedIndex()];
            String room  = roomOptions[roomCombo.getSelectedIndex()];
            int    typeIdx = typeCombo.getSelectedIndex();
            DeviceType type = DeviceType.values()[typeIdx];

            if (name.isEmpty()) { showToast("Enter device name"); return; }

            SmartHomeFactory factory = SmartHomeFactoryProvider.getFactory(brand);

            // Factory Method usage
            Device newDevice = switch (type) {
                case LAMP            -> factory.createLamp(name, room);
                case THERMOSTAT      -> factory.createThermostat(name, room);
                case CAMERA          -> factory.createCamera(name, room);
                case ALARM           -> factory.createAlarm(name, room);
                case AIR_CONDITIONER -> factory.createAirConditioner(name, room);
                case TV              -> factory.createTV(name, room);
                default              -> factory.createLamp(name, room);
            };

            dm.addDevice(newDevice);
            dialog.dispose();
            refreshDevices();
            showToast("✅ Added " + name + " (" + brand + ") to " + room);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_CARD);
        btnPanel.setBorder(new EmptyBorder(0, 16, 16, 16));
        btnPanel.add(addBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Helper Builders ───────────────────────────────────────────────────────

    private JButton createActionButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg, 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 40)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JLabel styledFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_DARK);
        f.setCaretColor(ACCENT_CYAN);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_CARD2),
                new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        c.setForeground(TEXT_PRIMARY);
        c.setBackground(BG_DARK);
        c.setBorder(BorderFactory.createLineBorder(BG_CARD2));
        return c;
    }

    private Component createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BG_CARD2);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Updates ───────────────────────────────────────────────────────────────

    private void updateStats() {
        if (statsLabel != null) {
            statsLabel.setText(String.format("<html><span style='color:#8892B0'>%d devices · %d active</span></html>",
                    dm.getTotalDevices(), dm.getActiveCount()));
        }
    }

    private void refreshLog() {
        if (logArea == null) return;
        List<String> events = dm.getRecentEvents(30);
        StringBuilder sb = new StringBuilder();
        for (int i = events.size() - 1; i >= 0; i--) {
            sb.append(events.get(i)).append("\n");
        }
        logArea.setText(sb.toString());
    }

    private void startRefreshTimer() {
        refreshTimer = new javax.swing.Timer(3000, e -> {
            updateStats();
            refreshLog();
        });
        refreshTimer.start();
    }

    private void showToast(String message) {
        dm.logEvent(message);
        refreshLog();
        JOptionPane.showMessageDialog(this, message, "SmartHome", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Dark UI defaults
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", BG_CARD);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", BG_DARK);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", BG_DARK);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("ScrollBar.thumb", BG_CARD2);
        UIManager.put("ScrollBar.track", BG_CARD);

        SwingUtilities.invokeLater(() -> {
            SmartHomeApp app = new SmartHomeApp();
            app.setVisible(true);
        });
    }
}
