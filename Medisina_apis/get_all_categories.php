<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

$servername = "localhost"; $username = "root"; $password = ""; $dbname = "medicina_db";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("error" => "Connection failed: " . $conn->connect_error));
    exit();
}

// Select columns based on your Category data class
$sql = "SELECT id, categoryName, description, hexColor FROM categories";
$result = $conn->query($sql);
$categories = array();

if ($result) {
    while($row = $result->fetch_assoc()) {
        $row['id'] = (int)$row['id'];
        // Ensure other types if necessary, though strings are fine by default from DB
        $categories[] = $row;
    }
    http_response_code(200);
    echo json_encode($categories);
} else {
    http_response_code(500);
    echo json_encode(array("error" => "Query failed: " . $conn->error));
}
$conn->close();
?>