<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *"); // Allow all origins (for development)

// --- Database Configuration ---
$servername = "localhost"; // Or 127.0.0.1
$username = "root";        // Default WAMP username (change if you have a different one)
$password = "";            // Default WAMP password (change if you have set one)
$dbname = "medisina_draft";   // <<< ENSURE THIS DATABASE EXISTS AND IS SPELLED CORRECTLY

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    // Return an error response in JSON format
    http_response_code(500); // Internal Server Error
    echo json_encode(
        array("error" => "Connection failed: " . $conn->connect_error)
    );
    exit(); // Terminate script execution
}

// It's good practice to set the charset for the connection
if (!$conn->set_charset("utf8mb4")) {
    http_response_code(500);
    echo json_encode(array("error" => "Error loading character set utf8mb4: " . $conn->error));
    exit();
}
// ... after connection ...
if (!$conn->query("USE medisina_draft")) {
    die("Error selecting database: " . $conn->error);
}

$sql = "SELECT brandName, regulationId, price, description FROM medicines";
$result = $conn->query($sql);

$medicines = array();

if ($result) {
    if ($result->num_rows > 0) {
        // Fetch all results
        while($row = $result->fetch_assoc()) {
            // Ensure numeric types are correctly cast if necessary for JSON
            $row['brandName'] = $row['brandName'];
            $row['regulationId'] = (int)$row['regulationId'];
            $row['price'] = (float)$row['price']; // PHP floats map to JSON numbers
            $medicines[] = $row;
        }
        http_response_code(200); // OK
        echo json_encode($medicines);
    } else {
        http_response_code(200); // OK, but no data
        echo json_encode(array()); // Return empty array if no medicines found
    }
} else {
    // Error in query execution
    http_response_code(500); // Internal Server Error
    echo json_encode(
        array("error" => "Query failed: " . $conn->error)
    );
}

$conn->close();
?>