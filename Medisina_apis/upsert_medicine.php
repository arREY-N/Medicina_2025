<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *"); // For development
header("Access-Control-Allow-Methods: POST, PUT, OPTIONS"); // Allow POST, PUT, and OPTIONS for preflight
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
$dbname = "medisina_draft";   // Your database name (make sure this is correct)

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Connection failed: " . $conn->connect_error, "id" => null));
    exit();
}

// Get the posted data
// The Medicine.java model has: id, brandName, regulationId, price, description
$data = json_decode(file_get_contents("php://input"));

// Basic validation
if (
    !isset($data->brandName) ||
    !isset($data->regulationId) || // Assuming regulationId is always sent
    !isset($data->price)         // Assuming price is always sent
    // description can be optional or empty
) {
    http_response_code(400); // Bad Request
    echo json_encode(array("success" => false, "message" => "Missing required medicine data (brandName, regulationId, price).", "id" => null));
    exit();
}

// Sanitize and prepare data
// 'id' can be null or not present for INSERT, or have a value for UPDATE
$id = isset($data->id) && is_numeric($data->id) && $data->id > 0 ? (int)$data->id : null;
$brandName = $conn->real_escape_string(trim($data->brandName));
$regulationId = (int)$data->regulationId; // Cast to int
$price = (float)$data->price;             // Cast to float
$description = isset($data->description) ? $conn->real_escape_string(trim($data->description)) : "";

if (empty($brandName)) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Brand name cannot be empty.", "id" => null));
    exit();
}

$response = array("success" => false, "message" => "An error occurred during upsert.", "id" => $id); // Default id to existing if update

if ($id !== null) {
    // UPDATE existing medicine
    $sql = "UPDATE medicines SET brandName = ?, regulationId = ?, price = ?, description = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    if ($stmt === false) {
        http_response_code(500);
        echo json_encode(array("success" => false, "message" => "Prepare statement failed (UPDATE): " . $conn->error, "id" => $id));
        exit();
    }
    // Column types in DB: brandName (VARCHAR), regulationId (INT), price (DECIMAL/FLOAT), description (TEXT), id (INT)
    // PHP bind_param types: s (string), i (integer), d (double), b (blob)
    $stmt->bind_param("sidsi", $brandName, $regulationId, $price, $description, $id);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            $response = array("success" => true, "message" => "Medicine updated successfully.", "id" => $id);
            http_response_code(200); // OK
        } else {
            // Check if ID exists if no rows affected
            $checkSql = "SELECT id FROM medicines WHERE id = ?";
            $checkStmt = $conn->prepare($checkSql);
            $checkStmt->bind_param("i", $id);
            $checkStmt->execute();
            $checkResult = $checkStmt->get_result();
            if ($checkResult->num_rows == 0) {
                 $response = array("success" => false, "message" => "Update failed: Medicine with ID " . $id . " not found.", "id" => $id);
                 http_response_code(404); // Not Found
            } else {
                 $response = array("success" => true, "message" => "Medicine data was the same, no update performed.", "id" => $id);
                 http_response_code(200); // OK, but no change
            }
            $checkStmt->close();
        }
    } else {
        $response = array("success" => false, "message" => "Error updating medicine: " . $stmt->error, "id" => $id);
        http_response_code(500);
    }
    $stmt->close();

} else {
    // INSERT new medicine
    $sql = "INSERT INTO medicines (brandName, regulationId, price, description) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
     if ($stmt === false) {
        http_response_code(500);
        echo json_encode(array("success" => false, "message" => "Prepare statement failed (INSERT): " . $conn->error, "id" => null));
        exit();
    }
    $stmt->bind_param("sids", $brandName, $regulationId, $price, $description);

    if ($stmt->execute()) {
        $newId = $conn->insert_id; // Get the ID of the newly inserted row
        if ($newId > 0) {
            $response = array("success" => true, "message" => "Medicine inserted successfully.", "id" => $newId);
            http_response_code(201); // Created
        } else {
            $response = array("success" => false, "message" => "Failed to insert medicine, no ID returned by database.", "id" => null);
            http_response_code(500);
        }
    } else {
        $response = array("success" => false, "message" => "Error inserting medicine: " . $stmt->error, "id" => null);
        http_response_code(500);
    }
    $stmt->close();
}

echo json_encode($response);
$conn->close();
?>