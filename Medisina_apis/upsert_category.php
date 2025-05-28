<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, PUT, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') { http_response_code(200); exit(); }

$servername = "localhost"; $username = "root"; $password = ""; $dbname = "medicina_db";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Connection failed: " . $conn->connect_error, "id" => null));
    exit();
}

$data = json_decode(file_get_contents("php://input"));

// Basic Validation
if (!isset($data->categoryName)) { // categoryName is essential
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Missing required field: categoryName.", "id" => null));
    exit();
}

$id = isset($data->id) && is_numeric($data->id) && $data->id > 0 ? (int)$data->id : null;
$categoryName = $conn->real_escape_string(trim($data->categoryName));
$description = isset($data->description) ? $conn->real_escape_string(trim($data->description)) : "";
$hexColor = isset($data->hexColor) ? $conn->real_escape_string(trim($data->hexColor)) : "#9E9E9E"; // Default if not provided

if (empty($categoryName)) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Category name cannot be empty.", "id" => null));
    exit();
}


$response = array("success" => false, "message" => "An error occurred.", "id" => $id);

if ($id !== null) { // UPDATE
    $sql = "UPDATE categories SET categoryName = ?, description = ?, hexColor = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssi", $categoryName, $description, $hexColor, $id);
} else { // INSERT
    $sql = "INSERT INTO categories (categoryName, description, hexColor) VALUES (?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sss", $categoryName, $description, $hexColor);
}

if ($stmt === false) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "SQL Prepare failed: " . $conn->error, "id" => $id));
    exit();
}

if ($stmt->execute()) {
    if ($id === null) { // INSERT
        $newId = $conn->insert_id;
        $response = array("success" => true, "message" => "Category created successfully.", "id" => $newId);
        http_response_code(201);
    } else { // UPDATE
        if ($stmt->affected_rows > 0) {
            $response = array("success" => true, "message" => "Category updated successfully.", "id" => $id);
        } else {
             $response = array("success" => true, "message" => "Category data was the same or ID not found, no update performed.", "id" => $id);
        }
        http_response_code(200);
    }
} else {
    if ($conn->errno == 1062) { // Duplicate entry for categoryName if it's UNIQUE
         http_response_code(409);
         $response = array("success" => false, "message" => "Category creation/update failed: Category name might already exist. " . $stmt->error, "id" => $id);
    } else {
        http_response_code(500);
        $response = array("success" => false, "message" => "Category creation/update failed: " . $stmt->error, "id" => $id);
    }
}
$stmt->close();
$conn->close();
echo json_encode($response);
?>