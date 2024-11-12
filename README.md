# 🗓️ EduScheduler | Smart Timetable Management System

![EduScheduler](/timetable_data/timetable_CSE-A-2024.png?height=200&width=600)

## 📚 Table of Contents

- [About](#about)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## 🎓 About

The Timetable Management System is a Java-based application designed to create and manage academic timetables for educational institutions. It automatically generates timetables for multiple sections, handles course scheduling, and provides an easy-to-use interface for viewing and managing class schedules.

## ✨ Features

- 📅 Automatic timetable generation from JSON course data
- 🏫 Support for multiple sections (e.g., Section A and B)
- 🕒 Intelligent time slot allocation
- 🍽️ Lunch break consideration
- 👀 View generated timetables
- 🔍 Find free time slots
- 💾 Save and load timetable data

## 🚀 Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/timetable-management-system.git
   cd timetable-management-system
   ```

2. Download the JSON library:
   - Visit [Maven Repository](https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar)
   - Download the `json-20230227.jar` file

3. Create a `lib` folder in your project directory and move the JAR file into it:
   ```
   mkdir lib
   mv /path/to/downloaded/json-20230227.jar lib/
   ```

4. Compile the Java files:
   ```
   javac -cp .:lib/json-20230227.jar *.java
   ```

## 🖥️ Usage

1. Prepare your course data:
   - Edit the `timetable_data/courses.json` file with your course information

2. Run the program:
   ```
   java -cp .:lib/json-20230227.jar Main
   ```

3. Follow the on-screen menu to:
   - View timetables
   - Check free time slots
   - Exit and save the timetable

## 📊 Example Timetable

Here's a sample of what a generated timetable might look like:

```
╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                       Timetable for Section A                                                    ║
╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║   Time   │   Monday   │   Tuesday  │  Wednesday │  Thursday  │   Friday   ║
╠══════════╪════════════╪════════════╪════════════╪════════════╪════════════╣
║ 09:00-10:30 │ CS207      │           │ CS207      │           │ CS208      │
║ 10:45-12:15 │           │ HS201      │           │ CS201      │           │
║ 12:15-13:15 │           │           │           │           │           │
║ 14:30-16:00 │           │           │ CS208      │           │ MA201      │
║ 16:00-17:30 │ CS208      │ CS202      │           │           │           │
╚══════════╧════════════╧════════════╧════════════╧════════════╧════════════╝

Course Details:
═══════════════
MA201     : Probability (3-1-0-0-4)
           Instructor: Dr. Anand Kumar

CS201     : Discrete Mathematics (3-1-0-0-4)
           Instructor: Dr. Animesh Bari

CS207     : Object Oriented Programming (3-0-2-0-4)
           Instructor: Dr. Pramod Yelmewad

CS208     : Computer Architecture (3-0-2-0-4)
           Instructor: Dr. Prafulla Patil

CS202     : Design & Analysis of Algorithms (3-1-2-0-5)
           Instructor: Dr. Dhirendra Pal

HS201     : Industrial Social Psychology (3-0-0-0-3)
           Instructor: Dr. Neevazhagan

HOD: Dr. John Doe
```

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check [issues page](https://github.com/yourusername/timetable-management-system/issues) if you want to contribute.

## 📄 License

This project is [MIT](https://choosealicense.com/licenses/mit/) licensed.

---

Made with ❤️ by [Your Name](https://github.com/yourusername)
