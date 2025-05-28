<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, PUT, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') { http_response_code(200); exit(); }

$servername = "localhost"; $username = "root"; $password_db = ""; $dbname = "medisina_draft"; // Renamed $password to $password_db
$conn = new mysqli($servername, $username, $password_db, $dbname);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Connection failed: " . $conn->connect_error, "id" => null));
    exit();
}

$data = json_decode(file_get_contents("php://input"));

// Basic Validation
if (!isset($data->username) || !isset($data->email) || !isset($data->designationId)) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Missing required fields (username, email, designationId).", "id" => null));
    exit();
}
// For new accounts, password is required. For updates, it might be optional.
if (!isset($data->id) && !isset($data->password)) { // If new account and no password
     http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Password is required for new accounts.", "id" => null));
    exit();
}


$id = isset($data->id) && is_numeric($data->id) && $data->id > 0 ? (int)$data->id : null;
$username_val = $conn->real_escape_string(trim($data->username)); // Renamed $username to $username_val
$email = $conn->real_escape_string(trim($data->email));
$designationId = (int)$data->designationId;
$firstName = isset($data->firstName) ? $conn->real_escape_string(trim($data->firstName)) : "";
$lastName = isset($data->lastName) ? $conn->real_escape_string(trim($data->lastName)) : "";
$isActive = isset($data->isActive) ? (bool)$data->isActive : true; // Default to true if not provided


$response = array("success" => false, "message" => "An error occurred.", "id" => $id);

if ($id !== null) { // UPDATE
    // Password update logic: only update if a new password is provided
    if (isset($data->password) && !empty(trim($data->password))) {
        $password_val = password_hash(trim($data->password), PASSWORD_DEFAULT); // Renamed $password to $password_val
        $sql = "UPDATE accounts SET username = ?, password = ?, email = ?, designationId = ?, firstName = ?, lastName = ?, isActive = ? WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssisssi", $username_val, $password_val, $email, $designationId, $firstName, $lastName, $isActive, $id);
    } else {
        $sql = "UPDATE accounts SET username = ?, email = ?, designationId = ?, firstName = ?, lastName = ?, isActive = ? WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ssisssi", $username_val, $email, $designationId, $firstName, $lastName, $isActive, $id);
    }
} else { // INSERT
    if (!isset($data->password) || empty(trim($data->password))) {
        http_response_code(400);
        echo json_encode(array("success" => false, "message" => "Password is required for new accounts.", "id" => null));
        exit();
    }
    $password_val = password_hash(trim($data->password), PASSWORD_DEFAULT); // Hash password for new accounts
    $sql = "INSERT INTO accounts (username, password, email, designationId, firstName, lastName, isActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssisss", $username_val, $password_val, $email, $designationId, $firstName, $lastName, $isActive);
}

if ($stmt === false) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "SQL Prepare failed: " . $conn->error, "id" => $id));
    exit();
}

if ($stmt->execute()) {
    if ($id === null) { // INSERT
        $newId = $conn->insert_id;
        $response = array("success" => true, "message" => "Account created successfully.", "id" => $newId);
        http_response_code(201);
    } else { // UPDATE
        if ($stmt->affected_rows > 0) {
            $response = array("success" => true, "message" => "Account updated successfully.", "id" => $id);
        } else {
             $response = array("success" => true, "message" => "Account data was the same or ID not found, no update performed.", "id" => $id);
        }
        http_response_code(200);
    }
} else {
    // Check for duplicate username or email
    if ($conn->errno == 1062) { // Error number for duplicate entry
         http_response_code(409); // Conflict
         $response = array("success" => false, "message" => "Account creation/update failed: Username or Email already exists. " . $stmt->error, "id" => $id);
    } else {
        http_response_code(500);
        $response = array("success" => false, "message" => "Account creation/update failed: " . $stmt->error, "id" => $id);
    }
}
$stmt->close();
$conn->close();
echo json_encode($response);
?>