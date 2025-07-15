# Advanced Programming Project - Noy Malka Cohen: #

Java Computational Graph System
This project is a full-stack system simulating a computational graph using a publisher/subscriber model, parallel agent architecture, a custom configuration loader, and a lightweight HTTP server with an interactive web interface. It was developed as a final project integrating six exercises, each contributing a distinct layer of functionality—from core logic to the user interface.

Background:
The project is a combination of several exercises:

Exercise 1 – Core Model & Pub/Sub Architecture
We implemented the core model: a directed computational graph using the publisher/subscriber pattern. Topics act as channels for messages between agents. This enables building modular, reactive computation flows (e.g., camera → AI detector → gesture recognizer).

Exercise 2 – Concurrency via Active Object Pattern
To avoid blocking when multiple agents are subscribed to a topic, we introduced the ParallelAgent, a decorator implementing the Active Object design pattern. Each agent handles messages in its own thread, improving concurrency and responsiveness.

Exercise 3 – Configuration Object
We introduced a Config class to initialize agents and their topic bindings while ensuring the graph remains acyclic.

Exercise 4 – Generic Configuration Loader
We added the ability to load configurations from a .conf file uploaded via the web interface. This allowed secure, dynamic rewiring of agents and topics.

Exercise 5 – Custom HTTP Server (Controller Layer)
We implemented a lightweight HTTP server to handle browser requests (GET/POST), mimicking a REST API. The server routes requests to different servlet handlers responsible for uploading, messaging, and generating views.

Exercise 6 – View Layer in the Browser
We created a dynamic web interface using HTML, JavaScript, and vis.js to:
Upload configuration files, send messages to topics and view the computational graph and topic values in real time



Features
- Upload .conf files to generate graphs
- Send topic messages via HTML form
- View graph updates and topic values
- Built using custom HTTP server and full-stack Java
- HTML view with dynamic rendering using vis.js

📦 Installation
1. Clone the repository
bash
Copy
Edit
git clone https://github.com/Noymalkac/Advanced_programming_project.git
cd Advanced_programming_project
2. Compile the Java source files
On Windows:

bash
Copy
Edit
for /R %f in (*.java) do @echo %f >> sources.txt
javac -d bin @sources.txt
del sources.txt
⚠️ Requires Java 8+ installed and available via javac.

▶️ Run the Server
Start the server using:

bash
Copy
Edit
java -cp bin Main
The server will launch on http://localhost:8080

The browser will open automatically to:
http://localhost:8080/app/index.html
