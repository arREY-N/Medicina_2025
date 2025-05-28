<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') { http_response_code(200); exit(); }

$servername = "localhost"; $username = "root"; $password = ""; $dbname = "medisina_draft";
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
$accountId = (int)$_POST['id'];

$sql = "DELETE FROM accounts WHERE id = ?";
$stmt = $conn->prepare($sql);
if ($stmt === false) {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Prepare failed: " . $conn->error, "id" => $accountId));
    exit();
}
$stmt->bind_param("i", $accountId);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(array("success" => true, "message" => "Account deleted successfully.", "id" => $accountId));
    } else {
        http_response_code(404);
        echo json_encode(array("success" => false, "message" => "Account not found or already deleted.", "id" => $accountId));
    }
} else {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Error deleting account: " . $stmt->error, "id" => $accountId));
}
$stmt->close();
$conn->close();
?>