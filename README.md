# 🗓️ EduSchedulerX | Advanced Academic Timetable Management System

![EduSchedulerX Banner](/time_table/timetable_CSE-A-2023.png)

## 📚 Table of Contents
- [About](#-about)
- [Features](#-features)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Usage](#-usage)
- [Data Configuration](#-data-configuration)
- [Example Output](#-example-output)
- [Contributing](#-contributing)
- [License](#-license)

## 🎓 About
EduSchedulerX is a sophisticated Java-based timetable management system designed for academic institutions. It automates the process of creating and managing class schedules while considering various constraints such as course types, faculty availability, and classroom assignments. The system provides an intuitive interface for generating, viewing, and managing academic schedules efficiently.

## ✨ Features
- 📅 Automated timetable generation from CSV data
- 🏫 Support for multiple batches and departments
- 📚 Handling of different course types (lecture, practical, minor)
- 🎯 Intelligent time slot allocation with conflict prevention
- 🔍 Free slot finder functionality
- 📊 Multiple output formats (Console, CSV, PNG)
- 🔄 Save and load timetable states
- 🍽️ Automatic lunch break scheduling
- 📱 User-friendly console interface

## 💻 Requirements
- Java Development Kit (JDK) 8 or higher
- Minimum 1GB RAM (2GB recommended for large datasets)
- CSV file support for data input

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/EduSchedulerX.git
cd EduSchedulerX
```

### 2. Create Required Directories
```bash
mkdir -p timetable_data time_table
```

### 3. Prepare CSV Files
Place the following CSV files in the `timetable_data` directory:
- `courses.csv`
- `batches.csv`

### 4. Compile the Project
```bash
javac *.java
```

## 🖥️ Usage
1. Start the application:
```bash
java Main
```

2. Interactive Menu Options:
   - View all timetables
   - View specific timetable
   - View free slots
   - Generate timetable images
   - Generate timetable CSV files
   - Exit and save changes

## 📋 Data Configuration

### courses.csv Structure
| Column | Description |
|--------|-------------|
| id | Unique course identifier |
| courseCode | Course code |
| name | Course name |
| courseType | Course type (Elective/Core) |
| branch | Academic branch |
| section | Course section |
| lecture | Lecture details |
| theory | Theory hours |
| practical | Practical hours |
| credits | Course credits |
| hoursPerWeek | Total hours per week |
| eligibleFacultyIds | IDs of eligible faculty |

### Example courses.csv Entry
```csv
01234,DS457,Industrial Social Psychology,Elective,DSAI,A,2023,0,0,0,3,4,F085
```

### batches.csv Structure
| Column | Description |
|--------|-------------|
| id | Unique batch identifier |
| batchName | Name of the batch |
| year | Academic year |
| strength | Number of students |
| courseIds | Semicolon-separated course IDs |
| lectureRoomIDs | Lecture room identifiers |
| practicalRoomIDs | Practical room identifiers |

### Example batches.csv Entry
```csv
B012,DSAI-A-2021,450,DS401;CS457;CS451;CS472;DS452;DS457,C401,C002
```

## 📊 Example Output
```plaintext


███████╗██████╗ ██╗   ██╗███████╗ ██████╗██╗  ██╗███████╗██████╗ ██╗   ██╗██╗     ███████╗██████╗ ██╗  ██╗
██╔════╝██╔══██╗██║   ██║██╔════╝██╔════╝██║  ██║██╔════╝██╔══██╗██║   ██║██║     ██╔════╝██╔══██╗╚██╗██╔╝
█████╗  ██║  ██║██║   ██║███████╗██║     ███████║█████╗  ██║  ██║██║   ██║██║     █████╗  ██████╔╝ ╚███╔╝ 
██╔══╝  ██║  ██║██║   ██║╚════██║██║     ██╔══██║██╔══╝  ██║  ██║██║   ██║██║     ██╔══╝  ██╔══██╗ ██╔██╗ 
███████╗██████╔╝╚██████╔╝███████║╚██████╗██║  ██║███████╗██████╔╝╚██████╔╝███████╗███████╗██║  ██║██╔╝ ██╗
╚══════╝╚═════╝  ╚═════╝ ╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝╚═════╝  ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

                    Welcome to EduSchedulerX - Automating Academic Excellence                    
                    A Java-based solution for managing academic timetables                      

═══════════════════════════════════════════════════════════════════════════════════════════════════

Number of course lines: 82
Number of courses loaded: 47
Number of batch lines: 32
Number of batches loaded: 12

╔════════════════════════════════╗
║   Timetable Management System  ║
╠════════════════════════════════╣
║ 1. View All Timetables         ║
║ 2. View Specific Timetable     ║
║ 3. View Free Slots             ║
║ 4. Generate Timetable Image    ║
║ 5. Generate Timetable CSV      ║
║ 6. Exit                        ║
╚════════════════════════════════╝
Choose an option: 1

=== All Timetables ===

Timetable for CSE-A-2023:

╔═════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                    Timetable for CSE-A-2023                                    ║
╠═════════════════════════════════════════════════════════════════════════════════════════════════╣
║   Time        │   Monday    │   Tuesday   │  Wednesday  │  Thursday   │   Friday    ║
╠══════════════╪════════════╪════════════╪════════════╪════════════╪════════════╣
║ 08:00-09:00  │           │ CY201      │           │           │ GEN406     │
║ 09:00-10:30  │ CS207      │ CS201      │ MA201      │ CS208      │ CS208      │
║ 10:45-12:15  │ MA201      │ MA201      │ HS206      │ CS202      │ CS201      │
║ 14:30-16:00  │ CS208      │ CS208      │ CS201      │ CS201      │ MA201      │
║ 16:00-17:30  │ CS202      │ CS207      │ CS207      │ CS207      │ CS207      │
╚══════════════╧════════════╧════════════╧════════════╧════════════╧════════════╝

Course Details:
═══════════════
Code       │ Name                                │ Type       │ Credits    │ Branch/Section  │ Hours           │ Faculty IDs    
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
CY201      │ Minor in cybersecurity              │ minor      │ 1-0-0-0-1  │ CSE/A           │ L-1 T-0 P-0     │ F001           
GEN406     │ Minor in genai                      │ minor      │ 1-0-0-0-1  │ CSE/A           │ L-1 T-0 P-0     │ F001           
MA201      │ Probability                         │ Theory     │ 3-1-0-0-4  │ CSE/A           │ L-3 T-1 P-0     │ F001           
CS201      │ Discrete Mathematics                │ Theory     │ 3-1-0-0-4  │ CSE/A           │ L-3 T-1 P-0     │ F051           
CS207      │ Object Oriented Programming         │ Theory     │ 3-0-2-0-4  │ CSE/A           │ L-3 T-0 P-2     │ F052           
CS208      │ Computer Architecture               │ Theory     │ 3-0-2-0-4  │ CSE/A           │ L-3 T-0 P-2     │ F004           
CS202      │ Design & Analysis of Algorithms     │ Theory     │ 3-1-2-0-5  │ CSE/A           │ L-3 T-1 P-0     │ F053           
HS206      │ Industrial Social Pschology         │ Theory     │ 3-0-0-0-3  │ CSE/A           │ L-2 T-0 P-2     │ F006           

Minor Courses:
═══════════════
CY201      │ Minor in cybersecurity         │ TUE 08:00-09:00     
GEN406     │ Minor in genai                 │ FRI 08:00-09:00     

Press Enter to continue to the next timetable...

```

## 🤝 Contributing
Contributions are welcome! Please follow these steps:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License
[Add your license information here]

## 👨‍💻 Author
Made with ❤️ by [Anas](https://github.com/ANAS727189)