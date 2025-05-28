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

// Validation based on your Order.kt (all fields seem non-nullable except id)
if (
    !isset($data->medicineId) ||
    !isset($data->supplierId) ||
    !isset($data->quantity) ||
    !isset($data->price) ||
    !isset($data->expirationDate) || // Will be string like "2023-10-27"
    !isset($data->orderDate) ||       // Will be string like "2023-10-27"
    !isset($data->remainingQuantity)
) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Missing required fields.", "id" => null));
    exit();
}

$id = isset($data->id) && is_numeric($data->id) && $data->id > 0 ? (int)$data->id : null;
$medicineId = (int)$data->medicineId;
$supplierId = (int)$data->supplierId;
$quantity = (int)$data->quantity;
$price = (float)$data->price;
$expirationDate = $conn->real_escape_string(trim($data->expirationDate)); // Assuming 'YYYY-MM-DD'
$orderDate = $conn->real_escape_string(trim($data->orderDate));           // Assuming 'YYYY-MM-DD'
$remainingQuantity = (int)$data->remainingQuantity;


$response = array("success" => false, "message" => "An error occurred.", "id" => $id);

if ($id !== null) { // UPDATE
    $sql = "UPDATE orders SET medicineId = ?, supplierId = ?, quantity = ?, price = ?, expirationDate = ?, orderDate = ?, remainingQuantity = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    // Types: i (int), i (int), i (int), d (double), s (string for date), s (string for date), i (int), i (id)
    $stmt->bind_param("iiidssii", $medicineId, $supplierId, $quantity, $price, $expirationDate, $orderDate, $remainingQuantity, $id);
} else { // INSERT
    $sql = "INSERT INTO orders (medicineId, supplierId, quantity, price, expirationDate, orderDate, remainingQuantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iiidssi", $medicineId, $supplierId, $quantity, $price, $expirationDate, $orderDate, $remainingQuantity);
}

if ($stmt === false) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "SQL Prepare failed: " . $conn->error, "id" => $id));
    exit();
}

if ($stmt->execute()) {
    if ($id === null) { // INSERT
        $newId = $conn->insert_id;
        $response = array("success" => true, "message" => "Order entry created successfully.", "id" => $newId);
        http_response_code(201);
    } else { // UPDATE
        if ($stmt->affected_rows > 0) {
            $response = array("success" => true, "message" => "Order entry updated successfully.", "id" => $id);
        } else {
             $response = array("success" => true, "message" => "Order entry data was the same or ID not found, no update performed.", "id" => $id);
        }
        http_response_code(200);
    }
} else {
    http_response_code(500);
    $response = array("success" => false, "message" => "Order entry creation/update failed: " . $stmt->error, "id" => $id);
}
$stmt->close();
$conn->close();
echo json_encode($response);
?>