<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') { http_response_code(200); exit(); }

$servername = "localhost"; $username = "root"; $password = ""; $dbname = "medicina_db";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Connection failed: " . $conn->connect_error, "id" => null));
    exit();
}

if (!isset($_POST['id']) || !is_numeric($_POST['id']) || (int)$_POST['id'] <= 0) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "Missing or invalid 'id' parameter.", "id" => null));
    exit();
}
$categoryId = (int)$_POST['id'];

$sql = "DELETE FROM categories WHERE id = ?";
$stmt = $conn->prepare($sql);
if ($stmt === false) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Prepare failed: " . $conn->error, "id" => $categoryId));
    exit();
}
$stmt->bind_param("i", $categoryId);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(array("success" => true, "message" => "Category deleted successfully.", "id" => $categoryId));
    } else {
        http_response_code(404);
        echo json_encode(array("success" => false, "message" => "Category not found or already deleted.", "id" => $categoryId));
    }
} else {
    // Check for foreign key constraint violation if categories are linked
    if ($conn->errno == 1451) { // Error number for foreign key constraint
        http_response_code(409); // Conflict
        echo json_encode(array("success" => false, "message" => "Cannot delete category: It is currently associated with one or more medicines.", "id" => $categoryId));
    } else {
        http_response_code(500);
        echo json_encode(array("success" => false, "message" => "Error deleting category: " . $stmt->error, "id" => $categoryId));
    }
}
$stmt->close();
$conn->close();
?>