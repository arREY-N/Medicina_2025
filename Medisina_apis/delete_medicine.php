<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *"); // For development
header("Access-Control-Allow-Methods: POST, OPTIONS"); // Allow POST and OPTIONS for preflight
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// --- Handle OPTIONS request for CORS preflight ---
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// --- Database Configuration ---
$servername = "localhost";
$username = "root";        // Your MySQL username
$password = "";            // Your MySQL password
$dbname = "medisina_draft";   // Your database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    // For delete, the 'id' field in response isn't strictly for the *returned* ID,
    // but can echo back the ID that was attempted.
    echo json_encode(array("success" => false, "message" => "Connection failed: " . $conn->connect_error, "id" => null));
    exit();
}

// Check if 'id' is provided in POST request
if (!isset($_POST['id']) || !is_numeric($_POST['id']) || (int)$_POST['id'] <= 0) {
    http_response_code(400); // Bad Request
    echo json_encode(array("success" => false, "message" => "Missing or invalid 'id' parameter.", "id" => null));
    exit();
}

$medicineId = (int)$_POST['id'];

// Prepare SQL to delete the medicine
$sql = "DELETE FROM medicines WHERE id = ?";
$stmt = $conn->prepare($sql);

if ($stmt === false) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Prepare statement failed: " . $conn->error, "id" => $medicineId));
    exit();
}

$stmt->bind_param("i", $medicineId);

$response = array("success" => false, "message" => "An error occurred during delete.", "id" => $medicineId);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        $response = array("success" => true, "message" => "Medicine with ID " . $medicineId . " deleted successfully.", "id" => $medicineId);
        http_response_code(200); // OK
    } else {
        // No rows affected - check if the ID even existed
        $checkSql = "SELECT id FROM medicines WHERE id = ?";
        $checkStmt = $conn->prepare($checkSql);
        $checkStmt->bind_param("i", $medicineId);
        $checkStmt->execute();
        $checkResult = $checkStmt->get_result();

        if ($checkResult->num_rows == 0) {
            $response = array("success" => false, "message" => "Delete failed: Medicine with ID " . $medicineId . " not found.", "id" => $medicineId);
            http_response_code(404); // Not Found
        } else {
            // This case should ideally not happen if ID existed but delete had 0 affected rows (unless DB issue)
            $response = array("success" => false, "message" => "Medicine with ID " . $medicineId . " was found but not deleted (0 rows affected).", "id" => $medicineId);
            http_response_code(500); // Server error or unexpected state
        }
        $checkStmt->close();
    }
} else {
    $response = array("success" => false, "message" => "Error deleting medicine: " . $stmt->error, "id" => $medicineId);
    http_response_code(500);
}

$stmt->close();
$conn->close();

echo json_encode($response);
?>