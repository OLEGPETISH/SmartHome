# 🏠 Smart Home Application
### Лабораторная работа №1 — Порождающие паттерны проектирования

---

## Запуск проекта

```bash
# Требования: JDK 17+
chmod +x build.sh
./build.sh --run
```

---

## Реализованные паттерны

### 1. 🏭 Factory Method
**Файл:** `src/main/java/com/smarthome/factory/DeviceFactory.java`

Абстрактный метод `createDevice()` определяет интерфейс создания устройств.
Конкретные фабрики (`LampFactory`, `ThermostatFactory`, `CameraFactory` и др.)
переопределяют его, создавая нужный тип устройства.

```java
DeviceFactory factory = new DeviceFactory.LampFactory();
Device lamp = factory.createAndConfigure("Living Room Light", "Living Room", "Philips");
```

**Структура:**
```
DeviceFactory (abstract)
├── LampFactory
├── ThermostatFactory
├── CameraFactory
├── AlarmFactory
├── AirConditionerFactory
└── TVFactory
```

---

### 2. 🏗️ Abstract Factory
**Файл:** `src/main/java/com/smarthome/abstractfactory/SmartHomeFactoryProvider.java`

Интерфейс `SmartHomeFactory` определяет семейство связанных устройств одного бренда.
Каждый бренд (Xiaomi, Philips, Samsung) создаёт совместимые устройства с уникальными
настройками по умолчанию.

```java
SmartHomeFactory factory = SmartHomeFactoryProvider.getFactory("Philips");
Lamp lamp = factory.createLamp("Main Light", "Living Room");       // Philips, warm, 90%
AirConditioner ac = factory.createAirConditioner("AC", "Bedroom"); // Philips-style
```

**Структура:**
```
SmartHomeFactory (interface)
├── XiaomiFactory   → energy-saving defaults, cool light
├── PhilipsFactory  → ambiance defaults, warm light
└── SamsungFactory  → performance defaults, daylight
```

---

### 3. 🔒 Singleton
**Файл:** `src/main/java/com/smarthome/singleton/DeviceManager.java`

`DeviceManager` — единственный экземпляр на всё приложение.
Хранит состояния всех устройств, лог событий, предоставляет
операции управления. Реализован с double-checked locking (потокобезопасно).

```java
DeviceManager dm = DeviceManager.getInstance(); // всегда один и тот же объект
dm.addDevice(lamp);
dm.turnOffAll();
List<Device> living = dm.getDevicesByRoom("Living Room");
```

---

### 4. 🧱 Builder
**Файл:** `src/main/java/com/smarthome/builder/AutomationScenario.java`

Сложный объект `AutomationScenario` строится пошагово через fluent API.
Поддерживает: расписания, триггеры событий, очерёдность действий, задержки.

```java
AutomationScenario night = new AutomationScenario.Builder("Night Mode")
    .description("Quiet mode for sleeping")
    .triggerAtTime("22:30")
    .turnOffRoom("Living Room")
    .setBrightness(lampId, 10)
    .setTemperature(acId, 19.0)
    .withDelay(500)
    .build();

night.execute();
```

---

### 5. 🧬 Prototype
**Файл:** `src/main/java/com/smarthome/prototype/RoomConfiguration.java`
**Файл:** `src/main/java/com/smarthome/prototype/ConfigurationRegistry.java`

`RoomConfiguration` реализует `Cloneable`. При клонировании выполняется
глубокое копирование всех `DeviceSettings`, что позволяет переиспользовать
конфигурации комнат без повторной настройки.

```java
// Создаём прототип один раз
RoomConfiguration nightProto = new RoomConfiguration("Night Mode", "Living Room")
    .setLampSettings(15, "warm")
    .setACSettings(22.0, 1);
registry.register("night_mode", nightProto);

// Клонируем для другой комнаты — быстро и без new
RoomConfiguration bedroomNight = registry.getCloneForRoom("night_mode", "Bedroom");
```

---

## Структура проекта

```
src/main/java/com/smarthome/
├── model/               # Модели устройств
│   ├── Device.java      # Интерфейс
│   ├── AbstractDevice   # Базовый класс
│   ├── Lamp.java
│   ├── Thermostat.java
│   ├── Camera.java
│   ├── Alarm.java
│   ├── AirConditioner.java
│   ├── TV.java
│   ├── DeviceType.java
│   └── DeviceSettings.java
├── factory/             # Factory Method
│   └── DeviceFactory.java
├── abstractfactory/     # Abstract Factory
│   ├── SmartHomeFactory.java
│   └── SmartHomeFactoryProvider.java
├── singleton/           # Singleton
│   └── DeviceManager.java
├── builder/             # Builder
│   └── AutomationScenario.java
├── prototype/           # Prototype
│   ├── RoomConfiguration.java
│   └── ConfigurationRegistry.java
└── ui/                  # Графический интерфейс
    └── SmartHomeApp.java
```

---

## Для Lab 2 и Lab 3 (структурные и поведенческие)

Планируется добавить:
- **Adapter** — поддержка Zigbee/Wi-Fi/Bluetooth API
- **Observer** — уведомления (движение камеры, изменение температуры)
- **Strategy** — алгоритмы энергосбережения
- **Decorator** — расширение функций устройств
